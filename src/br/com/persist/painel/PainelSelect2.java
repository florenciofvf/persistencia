package br.com.persist.painel;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.swing.JComboBox;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;

import br.com.persist.Objeto;
import br.com.persist.banco.Conexao;
import br.com.persist.banco.Persistencia;
import br.com.persist.comp.Button;
import br.com.persist.comp.Panel;
import br.com.persist.comp.ScrollPane;
import br.com.persist.comp.TextArea;
import br.com.persist.principal.Formulario;
import br.com.persist.modelo.RegistroModelo;
import br.com.persist.modelo.VazioModelo;
import br.com.persist.tabela.TabelaUtil;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.Icones;
import br.com.persist.util.Util;

public class PainelSelect2 extends Panel {
	private static final long serialVersionUID = 1L;
	private static final String PAINEL_SELECT = "PAINEL SELECT";
	private static final File file = new File("consultas/consultas");
	private final JTable tabela = new JTable(new VazioModelo());
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private final JComboBox<Conexao> cmbConexao;

	public PainelSelect2(Formulario formulario, Conexao padrao) {
		cmbConexao = new JComboBox<>(formulario.getConexoes());
		tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		if (padrao != null) {
			cmbConexao.setSelectedItem(padrao);
		}
		toolbar.add(cmbConexao);
		montarLayout();
		abrir();
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);

		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, textArea, new ScrollPane(tabela));
		split.setDividerLocation(200);
		add(BorderLayout.CENTER, split);
	}

	private void abrir() {
		if (file.exists()) {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
				String linha = br.readLine();

				while (linha != null) {
					textArea.append(linha + Constantes.QL);
					linha = br.readLine();
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PAINEL_SELECT, ex, PainelSelect2.this);
			}
		}
	}

	private class Toolbar extends JToolBar {
		private static final long serialVersionUID = 1L;
		private Action atualizarAcao = Action.actionIcon("label.atualizar", Icones.ATUALIZAR);
		private Action salvarAcao = Action.actionIcon("label.salvar", Icones.SALVAR);

		Toolbar() {
			add(new Button(salvarAcao));
			addSeparator();
			add(new Button(atualizarAcao));

			eventos();
		}

		private void eventos() {
			atualizarAcao.setActionListener(e -> atualizar());

			salvarAcao.setActionListener(e -> {
				try {
					PrintWriter pw = new PrintWriter(file);
					pw.print(textArea.getText());
					pw.close();
				} catch (Exception ex) {
					Util.stackTraceAndMessage(PAINEL_SELECT, ex, PainelSelect2.this);
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
			TabelaUtil.ajustar(tabela, getGraphics(), 40);
		} catch (Exception ex) {
			Util.stackTraceAndMessage(PAINEL_SELECT, ex, this);
		}
	}
}