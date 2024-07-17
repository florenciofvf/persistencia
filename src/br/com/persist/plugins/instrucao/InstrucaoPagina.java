package br.com.persist.plugins.instrucao;

import static br.com.persist.componente.BarraButtonEnum.ATUALIZAR;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
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
import br.com.persist.componente.ToolbarPesquisa;
import br.com.persist.plugins.instrucao.compilador.BibliotecaContexto;
import br.com.persist.plugins.instrucao.compilador.Compilador;
import br.com.persist.plugins.instrucao.processador.Processador;

public class InstrucaoPagina extends Panel {
	private final PainelResultado painelResultado = new PainelResultado();
	public final JTextPane textArea = new JTextPane();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private ScrollPane scrollPane;
	private final File file;

	public InstrucaoPagina(File file) {
		this.file = Objects.requireNonNull(file);
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

	void setFontTextArea(Font font) {
		textArea.setFont(font);
	}

	private class PainelResultado extends Panel {
		private static final long serialVersionUID = 1L;
		private JTextPane textPane = new JTextPane();

		private PainelResultado() {
			add(BorderLayout.NORTH, new ToolbarPesquisa(textPane));
			add(BorderLayout.CENTER, new ScrollPane(textPane));
		}

		private void setText(String string) {
			textPane.setText(string);
			SwingUtilities.invokeLater(() -> textPane.scrollRectToVisible(new Rectangle()));
		}
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private Action executarAcao = acaoIcon("label.executar", Icones.EXECUTAR);
		private final TextField txtPesquisa = new TextField(35);
		private transient Selecao selecao;

		private Toolbar() {
			super.ini(new Nil(), LIMPAR, BAIXAR, COPIAR, COLAR, ATUALIZAR);
			addButton(executarAcao);
			atualizarAcao.text(InstrucaoMensagens.getString("label.compilar_arquivo"));
			txtPesquisa.setToolTipText(Mensagens.getString("label.pesquisar"));
			executarAcao.setActionListener(e -> executar());
			txtPesquisa.addActionListener(this);
			add(txtPesquisa);
			add(label);
		}

		Action acaoIcon(String chave, Icon icon) {
			return Action.acaoIcon(InstrucaoMensagens.getString(chave), icon);
		}

		private void executar() {
			String biblioteca = file.getName();
			try {
				Processador processador = new Processador();
				List<Object> resposta = processador.processar(biblioteca, "main");
				painelResultado.setText(resposta.toString());
			} catch (InstrucaoException ex) {
				painelResultado.setText(Util.getStackTrace(InstrucaoConstantes.PAINEL_INSTRUCAO, ex));
			}
		}

		@Override
		protected void atualizar() {
			String biblioteca = file.getName();
			try {
				Compilador compilador = new Compilador();
				BibliotecaContexto biblio = compilador.compilar(biblioteca);
				boolean resp = biblio != null;
				painelResultado.setText(resp ? InstrucaoMensagens.getString("msg.compilado")
						: InstrucaoMensagens.getString("msg.nao_compilado"));
			} catch (IOException | InstrucaoException ex) {
				painelResultado.setText(Util.getStackTrace(InstrucaoConstantes.PAINEL_INSTRUCAO, ex));
			}
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
			if (!Util.isEmpty(txtPesquisa.getText())) {
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
		Util.mensagem(InstrucaoPagina.this, InstrucaoMensagens.getString("msg.arquivo_reservado"));
	}

	boolean ehArquivoReservadoIgnorados() {
		return InstrucaoContainer.ehArquivoReservadoIgnorados(getNome());
	}

	private void abrir() {
		textArea.setText(Constantes.VAZIO);
		if (file.exists()) {
			try {
				int value = getValueScrollPane();
				textArea.setText(conteudo(file));
				setValueScrollPane(value);
			} catch (Exception ex) {
				Util.stackTraceAndMessage(InstrucaoConstantes.PAINEL_INSTRUCAO, ex, this);
			}
		}
	}

	public static String conteudo(File file) throws IOException {
		if (file != null && file.exists()) {
			StringBuilder sb = new StringBuilder();
			try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
				int i = reader.read();
				while (i != -1) {
					sb.append((char) i);
					i = reader.read();
				}
			}
			return sb.toString();
		}
		return "";
	}

	public void excluir() {
		if (file.exists()) {
			Path path = FileSystems.getDefault().getPath(file.getAbsolutePath());
			try {
				Files.delete(path);
			} catch (IOException e) {
				Util.stackTraceAndMessage(InstrucaoConstantes.PAINEL_INSTRUCAO, e, this);
			}
		}
	}

	public void salvar(AtomicBoolean atomic) {
		if (!Util.confirmaSalvarMsg(this, Constantes.TRES,
				InstrucaoMensagens.getString("msg.confirmar_salvar_ativa"))) {
			return;
		}
		try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
			pw.print(textArea.getText());
			atomic.set(true);
		} catch (Exception ex) {
			Util.stackTraceAndMessage(InstrucaoConstantes.PAINEL_INSTRUCAO, ex, this);
		}
	}
}