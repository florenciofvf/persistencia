package br.com.persist.plugins.checagem;

import static br.com.persist.componente.BarraButtonEnum.ATUALIZAR;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Icon;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextField;
import br.com.persist.marca.XMLException;

public class ChecagemPagina extends Panel {
	private static final long serialVersionUID = 1L;
	private final ToolbarParametro toolbarParametro = new ToolbarParametro();
	private final JTabbedPane tabbedPane = new JTabbedPane();
	public final JTextPane areaParametros = new JTextPane();
	private ScrollPane scrollPane;
	private final File file;

	public ChecagemPagina(File file) {
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		this.file = file;
		montarLayout();
		abrir();
	}

	private void montarLayout() {
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, criarPanelParametro(), criarPanelResultado());
		SwingUtilities.invokeLater(() -> split.setDividerLocation(.99));
		add(BorderLayout.CENTER, split);
	}

	private Panel criarPanelParametro() {
		Panel panel = new Panel();
		panel.add(BorderLayout.NORTH, toolbarParametro);
		Panel panelArea = new Panel();
		panelArea.add(BorderLayout.CENTER, areaParametros);
		scrollPane = new ScrollPane(panelArea);
		panel.add(BorderLayout.CENTER, scrollPane);
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

	static Action actionMenu(String chave) {
		return Action.acaoMenu(ChecagemMensagens.getString(chave), null);
	}

	static Action actionIcon(String chave, Icon icon) {
		return Action.acaoIcon(ChecagemMensagens.getString(chave), icon);
	}

	private class ToolbarParametro extends BarraButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private Action sincronizarAcao = actionIcon("label.atualizar_cache2", Icones.SINCRONIZAR);
		private Action checarAcao = actionIcon("label.checar_sentenca", Icones.SUCESSO);
		private Action criarAcao = actionIcon("label.nova_sentenca", Icones.CRIAR2);
		private final TextField txtPesquisa = new TextField(35);
		private transient Selecao selecao;

		private ToolbarParametro() {
			super.ini(new Nil(), LIMPAR, BAIXAR, COPIAR, COLAR, ATUALIZAR);
			addButton(criarAcao);
			addButton(checarAcao);
			addButton(sincronizarAcao);
			atualizarAcao.text(ChecagemMensagens.getString("label.atualizar_cache"));
			txtPesquisa.setToolTipText(Mensagens.getString("label.pesquisar"));
			sincronizarAcao.setActionListener(e -> sincronizarSentencas());
			checarAcao.setActionListener(e -> checarSentenca());
			criarAcao.setActionListener(e -> novaSentenca());
			txtPesquisa.addActionListener(this);
			add(txtPesquisa);
			add(label);
		}

		private void sincronizarSentencas() {
			try {
				ChecagemUtil.atualizarEstrutura(file, areaParametros.getText());
				mensagemSucesso();
			} catch (ChecagemException | XMLException e) {
				Util.mensagem(ChecagemPagina.this, e.getMessage());
			}
		}

		@Override
		protected void atualizar() {
			try {
				ChecagemUtil.atualizarEstrutura(file);
				mensagemSucesso();
			} catch (ChecagemException | XMLException | IOException e) {
				Util.mensagem(ChecagemPagina.this, e.getMessage());
			}
		}

		private void novaSentenca() {
			StringBuilder sb = new StringBuilder(areaParametros.getText());
			if (sb.length() > 0) {
				sb.append(Constantes.QL);
			}
			sb.append("<set>" + Constantes.QL);
			sb.append("    <![CDATA[" + Constantes.QL);
			sb.append("        " + Constantes.QL);
			sb.append("    ]]>" + Constantes.QL);
			sb.append("</set>");
			areaParametros.setText(sb.toString());
		}

		private void checarSentenca() {
			if (Util.estaVazio(areaParametros.getText())) {
				Util.mensagem(ChecagemPagina.this, ChecagemMensagens.getString("msg.nenhuma_sentenca_declarada"));
				return;
			}
			try {
				ChecagemUtil.checarEstrutura(areaParametros.getText());
				mensagemSucesso();
			} catch (ChecagemException | XMLException e) {
				Util.mensagem(ChecagemPagina.this, e.getMessage());
			}
		}

		private void mensagemSucesso() {
			Util.mensagem(ChecagemPagina.this, "SUCESSO");
		}

		@Override
		protected void limpar() {
			areaParametros.setText(Constantes.VAZIO);
		}

		@Override
		protected void baixar() {
			abrir();
			selecao = null;
			label.limpar();
		}

		@Override
		protected void copiar() {
			String string = Util.getString(areaParametros);
			Util.setContentTransfered(string);
			copiarMensagem(string);
			areaParametros.requestFocus();
		}

		@Override
		protected void colar(boolean numeros, boolean letras) {
			Util.getContentTransfered(areaParametros, numeros, letras);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.estaVazio(txtPesquisa.getText())) {
				selecao = Util.getSelecao(areaParametros, selecao, txtPesquisa.getText());
				selecao.selecionar(label);
			} else {
				label.limpar();
			}
		}
	}

	public String getConteudo() {
		return areaParametros.getText();
	}

	public String getNome() {
		return file.getName();
	}

	private void abrir() {
		areaParametros.setText(Constantes.VAZIO);
		if (file.exists()) {
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
				StringBuilder sb = new StringBuilder();
				int value = getValueScrollPane();
				String linha = br.readLine();
				while (linha != null) {
					sb.append(linha + Constantes.QL);
					linha = br.readLine();
				}
				areaParametros.setText(sb.toString());
				setValueScrollPane(value);
			} catch (Exception ex) {
				Util.stackTraceAndMessage(ChecagemConstantes.PAINEL_CHECAGEM, ex, this);
			}
		}
	}

	public void excluir() {
		if (file.exists()) {
			Path path = FileSystems.getDefault().getPath(file.getAbsolutePath());
			try {
				Files.delete(path);
			} catch (IOException e) {
				Util.stackTraceAndMessage(ChecagemConstantes.PAINEL_CHECAGEM, e, this);
			}
		}
	}

	public void salvar(AtomicBoolean atomic) {
		if (!Util.confirmaSalvarMsg(this, Constantes.TRES, ChecagemMensagens.getString("msg.confirmar_salvar_ativa"))) {
			return;
		}
		try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
			pw.print(areaParametros.getText());
			atomic.set(true);
		} catch (Exception ex) {
			Util.stackTraceAndMessage(ChecagemConstantes.PAINEL_CHECAGEM, ex, this);
		}
	}
}