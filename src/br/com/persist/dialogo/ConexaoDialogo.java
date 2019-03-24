package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JTable;
import javax.swing.JToolBar;

import br.com.persist.banco.Conexao;
import br.com.persist.comp.Button;
import br.com.persist.comp.ScrollPane;
import br.com.persist.formulario.Formulario;
import br.com.persist.modelo.ConexaoModelo;
import br.com.persist.tabela.TabelaUtil;
import br.com.persist.util.Action;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class ConexaoDialogo extends DialogoAbstrato {
	private static final long serialVersionUID = 1L;
	private final ConexaoModelo modelo = new ConexaoModelo();
	private final JTable tabela = new JTable(modelo);
	private final Toolbar toolbar = new Toolbar();
	private final Formulario formulario;

	public ConexaoDialogo(Formulario formulario) {
		super(formulario, Mensagens.getString("label.conexao"), 1000, 600, false);
		this.formulario = formulario;
		montarLayout();
		configurar();
		setVisible(true);
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(tabela));
	}

	private void configurar() {
		tabela.getColumnModel().getColumn(0).setCellRenderer(new ConexaoStatusRenderer());
		tabela.getColumnModel().getColumn(0).setCellEditor(new ConexaoStatusEditor());
		tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				toolbar.abrirAcao.actionPerformed(null);
			}
		});
	}

	protected void processar() {
	}

	private class Toolbar extends JToolBar {
		private static final long serialVersionUID = 1L;
		private Action fecharAcao = Action.actionIcon("label.final_conexoes", Icones.BANCO_DESCONECTA);
		private Action conectaAcao = Action.actionIcon("label.conectar", Icones.CONECTA);
		private Action salvarAcao = Action.actionIcon("label.salvar", Icones.SALVAR);
		private Action abrirAcao = Action.actionIcon("label.baixar", Icones.BAIXAR);
		private Action copiarAcao = Action.actionIcon("label.copiar", Icones.COPIA);
		private Action topAcao = Action.actionIcon("label.primeiro", Icones.TOP);
		private Action novoAcao = Action.actionIcon("label.novo", Icones.NOVO);

		Toolbar() {
			add(new Button(topAcao));
			addSeparator();
			add(new Button(conectaAcao));
			addSeparator();
			add(new Button(fecharAcao));
			addSeparator();
			add(new Button(novoAcao));
			add(new Button(copiarAcao));
			addSeparator();
			add(new Button(abrirAcao));
			add(new Button(salvarAcao));

			eventos();
		}

		private void eventos() {
			abrirAcao.setActionListener(e -> {
				try {
					modelo.abrir();
					formulario.atualizarConexoes();
					TabelaUtil.ajustar(tabela, getGraphics(), 40);
				} catch (Exception ex) {
					Util.stackTraceAndMessage("ABRIR: ", ex, ConexaoDialogo.this);
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

			conectaAcao.setActionListener(e -> {
				int[] linhas = tabela.getSelectedRows();

				if (linhas != null && linhas.length == 1) {
					try {
						Conexao conexao = modelo.getConexao(linhas[0]);
						Conexao.getConnection2(conexao);
						Util.mensagem(ConexaoDialogo.this, "SUCESSO");
						tabela.repaint();
					} catch (Exception ex) {
						Util.stackTraceAndMessage("ERRO", ex, ConexaoDialogo.this);
					}
				}
			});

			fecharAcao.setActionListener(e -> {
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
					Util.stackTraceAndMessage("SALVAR: ", ex, ConexaoDialogo.this);
				}
			});
		}
	}
}