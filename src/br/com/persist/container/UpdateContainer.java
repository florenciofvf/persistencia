package br.com.persist.container;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.KeyStroke;

import br.com.persist.banco.Conexao;
import br.com.persist.banco.ConexaoProvedor;
import br.com.persist.banco.Persistencia;
import br.com.persist.comp.BarraButton;
import br.com.persist.comp.Label;
import br.com.persist.comp.Panel;
import br.com.persist.comp.TextArea;
import br.com.persist.fichario.Fichario;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Util;

public class UpdateContainer extends Panel implements Fichario.IFicharioSalvar {
	private static final long serialVersionUID = 1L;
	private static final File file = new File("atualizacoes/atualizacoes");
	private static final String PAINEL_UPDATE = "PAINEL UPDATE";
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private final JComboBox<Conexao> cmbConexao;
	private Label labelStatus = new Label();

	public UpdateContainer(IJanela janela, ConexaoProvedor provedor, Conexao padrao, String instrucao,
			Map<String, String> mapaChaveValor) {
		textArea.setText(Util.substituir(instrucao, mapaChaveValor));
		cmbConexao = Util.criarComboConexao(provedor, padrao);
		toolbar.ini(janela, mapaChaveValor);
		montarLayout();
		config();

		if (mapaChaveValor == null || mapaChaveValor.isEmpty()) {
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

	public UpdateContainer(IJanela janela, ConexaoProvedor provedor, Conexao padrao, String instrucao) {
		textArea.setText(instrucao);
		cmbConexao = Util.criarComboConexao(provedor, padrao);
		toolbar.ini(janela);
		montarLayout();
		config();
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, textArea);
		add(BorderLayout.SOUTH, labelStatus);
		labelStatus.setForeground(Color.BLUE);
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
				Util.stackTraceAndMessage(PAINEL_UPDATE, ex, UpdateContainer.this);
			}
		}
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action atualizarAcao = Action.actionIconUpdate();

		protected void ini(IJanela janela, Map<String, String> mapaChaveValor) {
			super.ini(janela, true, mapaChaveValor == null || mapaChaveValor.isEmpty());
			configAbrirAutoFichario(Constantes.ABRIR_AUTO_FICHARIO_ATUALIZA);
			configBaixarAcao(e -> abrir());

			addButton(atualizarAcao);
			add(true, cmbConexao);

			eventos();
		}

		public void ini(IJanela janela) {
			super.ini(janela, true, false);

			addButton(atualizarAcao);
			addSeparator();
			add(cmbConexao);
			eventos();
		}

		@Override
		protected void limpar() {
			textArea.limpar();
		}

		@Override
		protected void salvar() {
			if (!Util.confirmaSalvar(UpdateContainer.this, Constantes.TRES)) {
				return;
			}

			try (PrintWriter pw = new PrintWriter(file)) {
				pw.print(textArea.getText());
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PAINEL_UPDATE, ex, UpdateContainer.this);
			}
		}

		private void eventos() {
			atualizarAcao.setActionListener(e -> atualizar());
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

		String instrucao = textArea.getSelectedText();

		if (Util.estaVazio(instrucao)) {
			instrucao = textArea.getText();
		}

		try {
			Connection conn = Conexao.getConnection(conexao);
			int atualizados = Persistencia.executar(instrucao, conn);
			labelStatus.setText("ATUALIZADOS [" + atualizados + "]");
		} catch (Exception ex) {
			Util.stackTraceAndMessage(PAINEL_UPDATE, ex, this);
		}
	}
}