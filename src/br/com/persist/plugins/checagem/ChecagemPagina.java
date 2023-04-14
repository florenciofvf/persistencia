package br.com.persist.plugins.checagem;

import static br.com.persist.componente.BarraButtonEnum.ATUALIZAR;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.BorderLayout;
import java.awt.Rectangle;
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
	private final PainelResultado painelResultado = new PainelResultado();
	private final transient ChecagemCor checagemCor = new ChecagemCor();
	public final JTextPane textArea = new JTextPane();
	private final Toolbar toolbar = new Toolbar();
	private ScrollPane scrollPane;
	private final File file;

	public ChecagemPagina(File file) {
		this.file = file;
		montarLayout();
		abrir();
	}

	private void montarLayout() {
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, criarPanel(), criarPanelResultado());
		SwingUtilities.invokeLater(() -> split.setDividerLocation(.99));
		add(BorderLayout.CENTER, split);
	}

	private Panel criarPanel() {
		Panel panel = new Panel();
		panel.add(BorderLayout.NORTH, toolbar);
		Panel panelArea = new Panel();
		panelArea.add(BorderLayout.CENTER, textArea);
		scrollPane = new ScrollPane(panelArea);
		panel.add(BorderLayout.CENTER, scrollPane);
		return panel;
	}

	private Panel criarPanelResultado() {
		Panel panel = new Panel();
		panel.add(BorderLayout.CENTER, painelResultado);
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

	private class PainelResultado extends Panel {
		private static final long serialVersionUID = 1L;
		private JTextPane textPane = new JTextPane();

		private PainelResultado() {
			add(BorderLayout.NORTH, new ToolbarPesquisa());
			add(BorderLayout.CENTER, new ScrollPane(textPane));
		}

		private void setText(String string) {
			textPane.setText(string);
			SwingUtilities.invokeLater(() -> textPane.scrollRectToVisible(new Rectangle()));
		}

		private class ToolbarPesquisa extends BarraButton implements ActionListener {
			private static final long serialVersionUID = 1L;
			private final TextField txtPesquisa = new TextField(35);
			private transient Selecao selecao;

			public ToolbarPesquisa() {
				super.ini(new Nil(), LIMPAR, COPIAR, COLAR);
				txtPesquisa.setToolTipText(Mensagens.getString("label.pesquisar"));
				txtPesquisa.addActionListener(this);
				add(txtPesquisa);
				add(label);
			}

			@Override
			protected void limpar() {
				textPane.setText(Constantes.VAZIO);
			}

			@Override
			protected void copiar() {
				String string = Util.getString(textPane);
				Util.setContentTransfered(string);
				copiarMensagem(string);
				textPane.requestFocus();
			}

			@Override
			protected void colar(boolean numeros, boolean letras) {
				Util.getContentTransfered(textPane, numeros, letras);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!Util.estaVazio(txtPesquisa.getText())) {
					selecao = Util.getSelecao(textPane, selecao, txtPesquisa.getText());
					selecao.selecionar(label);
				} else {
					label.limpar();
				}
			}
		}
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private Action sincronizarAcao = actionIcon("label.atualizar_cache2", Icones.SINCRONIZAR);
		private Action checarAcao = actionIcon("label.checar_sentenca", Icones.SUCESSO);
		private Action criarAcao = actionIcon("label.nova_sentenca", Icones.CRIAR2);
		private Action formatarAcao = actionIcon("label.formatar", Icones.ELEMENTO);
		private Action executarAcao = actionIcon("label.executar", Icones.EXECUTAR);
		private final TextField txtPesquisa = new TextField(35);
		private transient Selecao selecao;

		private Toolbar() {
			super.ini(new Nil(), LIMPAR, BAIXAR, COPIAR, COLAR, ATUALIZAR);
			addButton(criarAcao);
			addButton(checarAcao);
			addButton(sincronizarAcao);
			addButton(formatarAcao);
			addButton(executarAcao);
			atualizarAcao.text(ChecagemMensagens.getString("label.atualizar_cache"));
			txtPesquisa.setToolTipText(Mensagens.getString("label.pesquisar"));
			sincronizarAcao.setActionListener(e -> sincronizarSentencas());
			checarAcao.setActionListener(e -> checarSentenca());
			formatarAcao.setActionListener(e -> formatar(true));
			criarAcao.setActionListener(e -> novaSentenca());
			executarAcao.setActionListener(e -> executar());
			txtPesquisa.addActionListener(this);
			add(txtPesquisa);
			add(label);
		}

		private void executar() {
			if (ehArquivoReservadoSentencas() || ehArquivoReservadoIgnorados()) {
				mensagemReservado();
				return;
			}
			painelResultado.setText(Constantes.VAZIO);
			SwingUtilities.invokeLater(() -> {
				try {
					ChecagemUtil.atualizarEstrutura(file, textArea.getText());
				} catch (ChecagemException | XMLException e) {
					Util.mensagem(ChecagemPagina.this, e.getMessage());
				}
				Contexto ctx = new Contexto();
				String string = ChecagemUtil.executar(file.getName(), null, ctx);
				painelResultado.setText(string);
			});
		}

		private void sincronizarSentencas() {
			if (ehArquivoReservadoSentencas() || ehArquivoReservadoIgnorados()) {
				mensagemReservado();
				return;
			}
			try {
				ChecagemUtil.atualizarEstrutura(file, textArea.getText());
				mensagemSucesso();
			} catch (ChecagemException | XMLException e) {
				Util.mensagem(ChecagemPagina.this, e.getMessage());
			}
		}

		@Override
		protected void atualizar() {
			if (ehArquivoReservadoSentencas() || ehArquivoReservadoIgnorados()) {
				mensagemReservado();
				return;
			}
			try {
				ChecagemUtil.atualizarEstrutura(file);
				mensagemSucesso();
			} catch (ChecagemException | XMLException | IOException e) {
				Util.mensagem(ChecagemPagina.this, e.getMessage());
			}
		}

		private void formatar(boolean msg) {
			if (ehArquivoReservadoSentencas() || ehArquivoReservadoIgnorados()) {
				if (msg) {
					mensagemReservado();
				}
				return;
			}
			try {
				Modulo modulo = ChecagemUtil.getModulo(file);
				checagemCor.processar(textArea.getStyledDocument(), modulo);
			} catch (ChecagemException | XMLException | IOException e) {
				Util.mensagem(ChecagemPagina.this, e.getMessage());
			}
		}

		private void novaSentenca() {
			if (ehArquivoReservadoSentencas() || ehArquivoReservadoIgnorados()) {
				mensagemReservado();
				return;
			}
			checagemCor.novaSentenca(textArea.getStyledDocument());
		}

		private void checarSentenca() {
			if (ehArquivoReservadoSentencas() || ehArquivoReservadoIgnorados()) {
				mensagemReservado();
				return;
			}
			if (Util.estaVazio(textArea.getText())) {
				Util.mensagem(ChecagemPagina.this, ChecagemMensagens.getString("msg.nenhuma_sentenca_declarada"));
				return;
			}
			try {
				ChecagemUtil.checarEstrutura(textArea.getText());
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
			textArea.setText(Constantes.VAZIO);
		}

		@Override
		protected void baixar() {
			abrir();
			selecao = null;
			label.limpar();
		}

		@Override
		protected void copiar() {
			String string = Util.getString(textArea);
			Util.setContentTransfered(string);
			copiarMensagem(string);
			textArea.requestFocus();
		}

		@Override
		protected void colar(boolean numeros, boolean letras) {
			Util.getContentTransfered(textArea, numeros, letras);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.estaVazio(txtPesquisa.getText())) {
				selecao = Util.getSelecao(textArea, selecao, txtPesquisa.getText());
				selecao.selecionar(label);
			} else {
				label.limpar();
			}
		}
	}

	public String getConteudo() {
		return textArea.getText();
	}

	public String getNome() {
		return file.getName();
	}

	void mensagemReservado() {
		Util.mensagem(ChecagemPagina.this, ChecagemMensagens.getString("msg.arquivo_reservado"));
	}

	boolean ehArquivoReservadoSentencas() {
		return ChecagemContainer.ehArquivoReservadoSentencas(getNome());
	}

	boolean ehArquivoReservadoIgnorados() {
		return ChecagemContainer.ehArquivoReservadoIgnorados(getNome());
	}

	private void abrir() {
		textArea.setText(Constantes.VAZIO);
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
				textArea.setText(sb.toString());
				toolbar.formatar(false);
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
			pw.print(textArea.getText());
			atomic.set(true);
		} catch (Exception ex) {
			Util.stackTraceAndMessage(ChecagemConstantes.PAINEL_CHECAGEM, ex, this);
		}
	}
}