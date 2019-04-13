package br.com.persist.container;

import java.awt.BorderLayout;
import java.awt.Graphics;

import javax.swing.JTable;

import br.com.persist.banco.Conexao;
import br.com.persist.comp.BarraButton;
import br.com.persist.comp.Panel;
import br.com.persist.comp.ScrollPane;
import br.com.persist.editor.ConexaoStatusEditor;
import br.com.persist.modelo.ConexaoModelo;
import br.com.persist.principal.Formulario;
import br.com.persist.renderer.ConexaoStatusRenderer;
import br.com.persist.tabela.TabelaUtil;
import br.com.persist.util.Action;
import br.com.persist.util.IIni;
import br.com.persist.util.IJanela;
import br.com.persist.util.Icones;
import br.com.persist.util.Util;

public class ConexaoContainer extends Panel implements IIni {
	private static final long serialVersionUID = 1L;
	private final ConexaoModelo modelo = new ConexaoModelo();
	private final JTable tabela = new JTable(modelo);
	private final Toolbar toolbar = new Toolbar();
	private final Formulario formulario;

	public ConexaoContainer(IJanela janela, Formulario formulario) {
		this.formulario = formulario;
		toolbar.ini(janela);
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(tabela));
	}

	private void configurar() {
		tabela.getColumnModel().getColumn(0).setCellRenderer(new ConexaoStatusRenderer());
		tabela.getColumnModel().getColumn(0).setCellEditor(new ConexaoStatusEditor());
		tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		toolbar.abrirAcao.actionPerformed(null);
	}

	@Override
	public void ini(Graphics graphics) {
		TabelaUtil.ajustar(tabela, graphics);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action desconectaAcao = Action.actionIcon("label.final_conexoes", Icones.BANCO_DESCONECTA);
		private Action conectaAcao = Action.actionIcon("label.conectar", Icones.CONECTA);
		private Action copiarAcao = Action.actionIcon("label.copiar", Icones.COPIA);
		private Action topAcao = Action.actionIcon("label.primeiro", Icones.TOP);
		private Action novoAcao = Action.actionIcon("label.novo", Icones.NOVO);
		private Action salvarAcao = Action.actionIconSalvar();
		private Action abrirAcao = Action.actionIconBaixar();

		@Override
		public void ini(IJanela janela) {
			super.ini(janela);

			addButton(abrirAcao);
			addButton(salvarAcao);
			addButton(true, topAcao);
			addButton(true, conectaAcao);
			addButton(true, desconectaAcao);
			addButton(true, novoAcao);
			addButton(copiarAcao);

			eventos();
		}

		private void eventos() {
			abrirAcao.setActionListener(e -> {
				try {
					modelo.abrir();
					formulario.atualizarConexoes();
					TabelaUtil.ajustar(tabela, getGraphics());
				} catch (Exception ex) {
					Util.stackTraceAndMessage("ABRIR: ", ex, ConexaoContainer.this);
				}
			});

			topAcao.setActionListener(e -> {
				int[] linhas = tabela.getSelectedRows();

				if (linhas != null && linhas.length == 1 && modelo.getColumnCount() > 1 && linhas[0] > 0) {
					modelo.primeiro(linhas[0]);
					modelo.fireTableDataChanged();
					tabela.setRowSelectionInterval(0, 0);
				}
			});

			conectaAcao.setActionListener(e -> conectar());

			desconectaAcao.setActionListener(e -> {
				try {
					Conexao.fecharConexoes();
					tabela.repaint();
				} catch (Exception ex) {
					Util.stackTraceAndMessage(getClass().getName() + ".fechar()", ex, formulario);
				}
			});

			novoAcao.setActionListener(e -> modelo.novo());

			copiarAcao.setActionListener(e -> {
				int[] linhas = tabela.getSelectedRows();

				if (linhas != null && linhas.length > 0) {
					for (int i : linhas) {
						Conexao c = modelo.getConexao(i);
						modelo.adicionar(c.clonar());
					}

					modelo.fireTableDataChanged();
				}
			});

			salvarAcao.setActionListener(e -> {
				try {
					modelo.salvar();
					formulario.atualizarConexoes();
				} catch (Exception ex) {
					Util.stackTraceAndMessage("SALVAR: ", ex, ConexaoContainer.this);
				}
			});
		}

		private void conectar() {
			int[] linhas = tabela.getSelectedRows();

			if (linhas != null && linhas.length == 1) {
				try {
					Conexao conexao = modelo.getConexao(linhas[0]);
					Conexao.getConnection2(conexao);
					Util.mensagem(ConexaoContainer.this, "SUCESSO");
					tabela.repaint();
				} catch (Exception ex) {
					Util.stackTraceAndMessage("ERRO", ex, ConexaoContainer.this);
				}
			}
		}
	}
}