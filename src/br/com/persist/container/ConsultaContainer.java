package br.com.persist.container;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import br.com.persist.banco.Conexao;
import br.com.persist.banco.ConexaoProvedor;
import br.com.persist.banco.Persistencia;
import br.com.persist.comp.BarraButton;
import br.com.persist.comp.Panel;
import br.com.persist.comp.ScrollPane;
import br.com.persist.comp.TextArea;
import br.com.persist.desktop.Objeto;
import br.com.persist.fichario.Fichario;
import br.com.persist.modelo.RegistroModelo;
import br.com.persist.modelo.VazioModelo;
import br.com.persist.tabela.TabelaUtil;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Util;

public class ConsultaContainer extends Panel implements Fichario.IFicharioSalvar {
	private static final long serialVersionUID = 1L;
	private static final File file = new File("consultas/consultas");
	private static final String PAINEL_SELECT = "PAINEL SELECT";
	private final JTable tabela = new JTable(new VazioModelo());
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private final JComboBox<Conexao> cmbConexao;

	public ConsultaContainer(IJanela janela, ConexaoProvedor provedor, Conexao padrao, String instrucao,
			Map<String, String> mapaChaveValor, boolean abrirArquivo) {
		textArea.setText(Util.substituir(instrucao, mapaChaveValor));
		cmbConexao = Util.criarComboConexao(provedor, padrao);
		tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		toolbar.ini(janela, mapaChaveValor, abrirArquivo);
		montarLayout();
		config();

		if ((mapaChaveValor == null || mapaChaveValor.isEmpty()) && abrirArquivo) {
			abrir();
		}
	}

	private void config() {
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), Constantes.EXEC);
		getActionMap().put(Constantes.EXEC, toolbar.atualizarAcao);
	}

	@Override
	public File getFileSalvarAberto() {
		return new File(Constantes.III + getClass().getName());
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);

		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, textArea, new ScrollPane(tabela));
		split.setDividerLocation(200);
		add(BorderLayout.CENTER, split);
	}

	private void abrir() {
		textArea.limpar();

		if (file.exists()) {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
				String linha = br.readLine();

				while (linha != null) {
					textArea.append(linha + Constantes.QL2);
					linha = br.readLine();
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PAINEL_SELECT, ex, ConsultaContainer.this);
			}
		}
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action atualizarAcao = Action.actionIconAtualizar();
		private Action baixarAcao = Action.actionIconBaixar();
		private Action salvarAcao = Action.actionIconSalvar();

		protected void ini(IJanela janela, Map<String, String> mapaChaveValor, boolean abrirArquivo) {
			super.ini(janela, true);
			configAbrirAutoFichario(Constantes.ABRIR_AUTO_FICHARIO_CONSULTA);

			addButton(baixarAcao);
			addButton(atualizarAcao);

			if ((mapaChaveValor == null || mapaChaveValor.isEmpty()) && abrirArquivo) {
				addButton(true, salvarAcao);
			}

			add(true, cmbConexao);
			eventos();
		}

		@Override
		protected void limpar() {
			textArea.limpar();
		}

		private void eventos() {
			atualizarAcao.setActionListener(e -> atualizar());

			baixarAcao.setActionListener(e -> abrir());

			salvarAcao.setActionListener(e -> {
				if (!Util.confirmaSalvar(ConsultaContainer.this, Constantes.TRES)) {
					return;
				}

				try {
					PrintWriter pw = new PrintWriter(file);
					pw.print(textArea.getText());
					pw.close();
				} catch (Exception ex) {
					Util.stackTraceAndMessage(PAINEL_SELECT, ex, ConsultaContainer.this);
				}
			});
		}
	}

	public void atualizar() {
		if (Util.estaVazio(textArea.getText())) {
			return;
		}

		Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

		if (conexao == null) {
			return;
		}

		String consulta = textArea.getSelectedText();

		if (Util.estaVazio(consulta)) {
			consulta = textArea.getText();
		}

		try {
			Connection conn = Conexao.getConnection(conexao);
			RegistroModelo modeloRegistro = Persistencia.criarModeloRegistro(conn, consulta, new String[0],
					new Objeto(), conexao);
			tabela.setModel(modeloRegistro);
			TabelaUtil.ajustar(tabela, getGraphics());
		} catch (Exception ex) {
			Util.stackTraceAndMessage(PAINEL_SELECT, ex, this);
		}
	}
}