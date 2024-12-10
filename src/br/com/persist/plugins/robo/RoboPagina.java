package br.com.persist.plugins.robo;

import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.BorderLayout;
import java.awt.Robot;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Icon;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
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
import br.com.persist.componente.TextEditor;
import br.com.persist.componente.TextEditorLine;
import br.com.persist.componente.TextField;

public class RoboPagina extends Panel {
	private final JTabbedPane tabbedPane = new JTabbedPane();
	public final TextEditor textEditor = new TextEditor();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private ScrollPane scrollPane;
	private final File file;

	public RoboPagina(File file) {
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
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
		panelArea.add(BorderLayout.CENTER, textEditor);
		scrollPane = new ScrollPane(panelArea);
		scrollPane.setRowHeaderView(new TextEditorLine(textEditor));
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

	private class Toolbar extends BarraButton implements ActionListener {
		private Action executarAcao = acaoIcon("label.executar", Icones.EXECUTAR);
		private final TextField txtPesquisa = new TextField(35);
		private static final long serialVersionUID = 1L;
		private transient Selecao selecao;

		private Toolbar() {
			super.ini(new Nil(), LIMPAR, BAIXAR, COPIAR, COLAR);
			executarAcao.setActionListener(e -> new Thread(this::executar).start());
			txtPesquisa.setToolTipText(Mensagens.getString("label.pesquisar"));
			txtPesquisa.addActionListener(this);
			addButton(executarAcao);
			add(txtPesquisa);
			add(label);
		}

		Action acaoIcon(String chave, Icon icon) {
			return Action.acaoIcon(RoboMensagens.getString(chave), icon);
		}

		private void executar() {
			String string = Util.getString(textEditor);
			if (Util.isEmpty(string)) {
				Util.mensagem(RoboPagina.this, RoboMensagens.getString("erro.sem_conteudo"));
				return;
			}
			Robot robot = null;
			try {
				robot = new Robot();
			} catch (Exception ex) {
				Util.mensagem(RoboPagina.this, ex.getMessage());
				return;
			}
			TokenUtil util = new TokenUtil(string);
			List<String> lista = util.listar();
			for (String str : lista) {
				String[] array = str.split(" ");
				Robo robo = RoboProvedor.getRobo(array[0]);
				if (robo != null) {
					try {
						robo.processar(robot, array);
					} catch (Exception ex) {
						Util.mensagem(RoboPagina.this, ex.getMessage());
					}
				}
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
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
				StringBuilder sb = new StringBuilder();
				int value = getValueScrollPane();
				String linha = br.readLine();
				while (linha != null) {
					sb.append(linha + Constantes.QL);
					linha = br.readLine();
				}
				textEditor.setText(sb.toString());
				setValueScrollPane(value);
			} catch (Exception ex) {
				Util.stackTraceAndMessage(RoboConstantes.PAINEL_ROBO, ex, this);
			}
		}
	}

	public void excluir() {
		if (file.exists()) {
			Path path = FileSystems.getDefault().getPath(file.getAbsolutePath());
			try {
				Files.delete(path);
			} catch (IOException e) {
				Util.stackTraceAndMessage(RoboConstantes.PAINEL_ROBO, e, this);
			}
		}
	}

	public void salvar(AtomicBoolean atomic) {
		if (!Util.confirmaSalvarMsg(this, Constantes.TRES, RoboMensagens.getString("msg.confirmar_salvar_ativa"))) {
			return;
		}
		try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
			pw.print(textEditor.getText());
			atomic.set(true);
		} catch (Exception ex) {
			Util.stackTraceAndMessage(RoboConstantes.PAINEL_ROBO, ex, this);
		}
	}

	public void contemConteudo(Set<String> set, String string) {
		if (Util.contemStringEm(file, string, true)) {
			set.add(file.getAbsolutePath());
		}
	}
}

class TokenUtil {
	final String string;
	int indice;

	TokenUtil(String string) {
		this.string = string == null ? "" : string;
	}

	boolean contem() {
		return indice < string.length();
	}

	String get() {
		StringBuilder sb = new StringBuilder();
		while (indice < string.length()) {
			char c = string.charAt(indice);
			indice++;
			if (c == '\n') {
				break;
			}
			sb.append(c);
		}
		return sb.toString();
	}

	List<String> listar() {
		List<String> lista = new ArrayList<>();
		while (contem()) {
			String str = get().trim();
			if (valida(str)) {
				lista.add(str);
			}
		}
		return lista;
	}

	boolean valida(String s) {
		return s.length() > 0 && !s.startsWith("#") && !s.startsWith("--") && !s.startsWith(".");
	}
}