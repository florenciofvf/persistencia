package br.com.persist.plugins.legado;

import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Icon;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import br.com.persist.assistencia.ArquivoUtil;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Aba;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextEditor;
import br.com.persist.componente.TextEditorLine;
import br.com.persist.marca.XML;

public class LegadoPagina extends Panel implements Aba {
	private final TextEditor textEditorResult = new TextEditor();
	public final TextEditor textEditor = new TextEditor();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private final LegadoFichario fichario;
	private ScrollPane scrollPane;
	private final File file;
	private int indice;

	public LegadoPagina(LegadoFichario fichario, File file) {
		this.fichario = fichario;
		this.file = file;
		montarLayout();
		abrir();
	}

	private void montarLayout() {
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, criarPanel(), criarPanelResultado());
		SwingUtilities.invokeLater(() -> split.setDividerLocation(.99));
		split.setOneTouchExpandable(true);
		split.setContinuousLayout(true);
		add(BorderLayout.CENTER, split);
		textEditor.setListener(
				TextEditor.newTextEditorAdapter(toolbar::focusInputPesquisar, fichario::salvar, toolbar::baixar));
	}

	private Panel criarPanel() {
		Panel panel = new Panel();
		panel.add(BorderLayout.NORTH, toolbar);
		Panel panelArea = new Panel();
		panelArea.add(BorderLayout.CENTER, textEditor);
		scrollPane = new ScrollPane(panelArea);
		panel.add(BorderLayout.CENTER, scrollPane);
		scrollPane.setRowHeaderView(new TextEditorLine(textEditor));
		return panel;
	}

	private Panel criarPanelResultado() {
		Panel panel = new Panel();
		ScrollPane scrollPaneResult = new ScrollPane(textEditorResult);
		scrollPaneResult.setRowHeaderView(new TextEditorLine(textEditorResult));
		panel.add(BorderLayout.CENTER, scrollPaneResult);
		return panel;
	}

	private int getValueScrollPane() {
		return scrollPane.getVerticalScrollBar().getValue();
	}

	private void setValueScrollPane(int value) {
		SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(value));
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private Action executarAcao = acaoIcon("label.executar", Icones.EXECUTAR);
		private static final long serialVersionUID = 1L;
		private transient Selecao selecao;

		private Toolbar() {
			super.ini(new Nil(), LIMPAR, BAIXAR, COPIAR, COLAR);
			executarAcao.setActionListener(e -> executar());
			txtPesquisa.addActionListener(this);
			addButton(executarAcao);
			add(txtPesquisa);
			add(label);
		}

		Action acaoIcon(String chave, Icon icon) {
			return Action.acaoIcon(LegadoMensagens.getString(chave), icon);
		}

		private void executar() {
			String string = textEditor.getText();
			if (Util.isEmpty(string)) {
				textEditorResult.setText("Editor vazio.");
				return;
			}
			try {
				LegadoHandler handler = new LegadoHandler();
				XML.processar(new ByteArrayInputStream(string.getBytes()), handler);
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				for (Legado obj : handler.getLista()) {
					obj.gerar(pw);
				}
				textEditorResult.setText(sw.toString());
			} catch (Exception ex) {
				Util.stackTraceAndMessage(LegadoConstantes.PAINEL_LEGADO, ex, LegadoPagina.this);
			}
		}

		@Override
		protected void limpar() {
			textEditor.setText(Constantes.VAZIO);
		}

		@Override
		protected void baixar() {
			abrir();
			selecao = null;
			label.limpar();
		}

		@Override
		protected void copiar() {
			String string = Util.getString(textEditor);
			Util.setContentTransfered(string);
			copiarMensagem(string);
			textEditor.requestFocus();
		}

		@Override
		protected void colar(boolean numeros, boolean letras) {
			Util.getContentTransfered(textEditor, numeros, letras);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				selecao = Util.getSelecao(textEditor, selecao, txtPesquisa.getText());
				selecao.selecionar(label);
			} else {
				label.limpar();
			}
		}
	}

	public String getConteudo() {
		return textEditor.getText();
	}

	public String getNome() {
		return file.getName();
	}

	private void abrir() {
		textEditor.setText(Constantes.VAZIO);
		if (file.exists()) {
			try {
				int value = getValueScrollPane();
				textEditor.setText(ArquivoUtil.getString(file));
				setValueScrollPane(value);
			} catch (Exception ex) {
				Util.stackTraceAndMessage(LegadoConstantes.PAINEL_LEGADO, ex, this);
			}
		}
	}

	public void excluir() {
		if (file.exists()) {
			Path path = FileSystems.getDefault().getPath(file.getAbsolutePath());
			try {
				Files.delete(path);
			} catch (IOException e) {
				Util.stackTraceAndMessage(LegadoConstantes.PAINEL_LEGADO, e, this);
			}
		}
	}

	public void salvar(AtomicBoolean atomic) {
		if (!Util.confirmaSalvarMsg(this, Constantes.TRES, LegadoMensagens.getString("msg.confirmar_salvar_ativa"))) {
			return;
		}
		try {
			ArquivoUtil.salvar(textEditor, file);
			atomic.set(true);
		} catch (Exception ex) {
			Util.stackTraceAndMessage(LegadoConstantes.PAINEL_LEGADO, ex, this);
		}
	}

	public void contemConteudo(Set<String> set, String string, boolean porParte) {
		if (Util.contemStringEm(file, string, porParte)) {
			set.add(file.getAbsolutePath());
		}
	}

	@Override
	public void setIndice(int i) {
		indice = i;
	}

	@Override
	public int getIndice() {
		return indice;
	}

	@Override
	public File getFile() {
		return file;
	}
}