package br.com.persist.plugins.entrega;

import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import br.com.persist.assistencia.ArquivoUtil;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Aba;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextEditor;
import br.com.persist.componente.TextEditorLine;

public class EntregaPagina extends Panel implements Aba {
	private final JTabbedPane tabbedPane = new JTabbedPane();
	public final TextEditor textEditor = new TextEditor();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private final EntregaFichario fichario;
	private ScrollPane scrollPane;
	private final File file;
	private int indice;

	public EntregaPagina(EntregaFichario fichario, File file) {
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
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
		scrollPane.setRowHeaderView(new TextEditorLine(textEditor));
		Panel panelScroll = new Panel();
		panelScroll.add(BorderLayout.CENTER, scrollPane);
		panel.add(BorderLayout.CENTER, new ScrollPane(panelScroll));
		return panel;
	}

	private Panel criarPanelResultado() {
		Panel panel = new Panel();
		panel.add(BorderLayout.CENTER, tabbedPane);
		return panel;
	}

	private int getValueScrollPane() {
		return scrollPane.getVerticalScrollBar().getValue();
	}

	private void setValueScrollPane(int value) {
		SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(value));
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private transient Selecao selecao;

		private Toolbar() {
			super.ini(new Nil(), LIMPAR, BAIXAR, COPIAR, COLAR);
			txtPesquisa.addActionListener(this);
			add(txtPesquisa);
			add(label);
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
				Util.stackTraceAndMessage(EntregaConstantes.PAINEL_ENTREGA, ex, this);
			}
		}
	}

	public void excluir() {
		if (file.exists()) {
			Path path = FileSystems.getDefault().getPath(file.getAbsolutePath());
			try {
				Files.delete(path);
			} catch (IOException e) {
				Util.stackTraceAndMessage(EntregaConstantes.PAINEL_ENTREGA, e, this);
			}
		}
	}

	public void salvar(AtomicBoolean atomic) {
		if (!Util.confirmaSalvarMsg(this, Constantes.TRES, EntregaMensagens.getString("msg.confirmar_salvar_ativa"))) {
			return;
		}
		try {
			ArquivoUtil.salvar(textEditor, file);
			atomic.set(true);
		} catch (Exception ex) {
			Util.stackTraceAndMessage(EntregaConstantes.PAINEL_ENTREGA, ex, this);
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