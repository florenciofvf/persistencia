package br.com.persist.conexao;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.io.File;

import javax.swing.JTable;

import br.com.persist.chave_valor.ChaveValorEditor;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.ScrollPane;
import br.com.persist.container.AbstratoContainer;
import br.com.persist.fichario.Fichario;
import br.com.persist.icone.Icones;
import br.com.persist.principal.Formulario;
import br.com.persist.tabela.TabelaUtil;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.IIni;
import br.com.persist.util.IJanela;
import br.com.persist.util.Util;

public class ConexaoContainer extends AbstratoContainer implements IIni, Fichario.IFicharioSalvar {
	private static final long serialVersionUID = 1L;
	private final ConexaoModelo modelo = new ConexaoModelo();
	private final JTable tabela = new JTable(modelo);
	private final Toolbar toolbar = new Toolbar();
	private ConexaoFormulario conexaoFormulario;

	public ConexaoContainer(IJanela janela, Formulario formulario) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		configurar();
	}

	public ConexaoFormulario getConexaoFormulario() {
		return conexaoFormulario;
	}

	public void setConexaoFormulario(ConexaoFormulario conexaoFormulario) {
		this.conexaoFormulario = conexaoFormulario;
	}

	@Override
	public File getFileSalvarAberto() {
		return new File(Constantes.III + getClass().getName());
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(tabela));
	}

	private void configurar() {
		tabela.getColumnModel().getColumn(0).setCellRenderer(new ConexaoStatusRenderer());
		tabela.getColumnModel().getColumn(0).setCellEditor(new ConexaoStatusEditor());
		tabela.getColumnModel().getColumn(3).setCellEditor(new ChaveValorEditor());
		tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		toolbar.getBaixarAcao().actionPerformed(null);
	}

	@Override
	public void ini(Graphics graphics) {
		TabelaUtil.ajustar(tabela, graphics);
	}

	@Override
	protected void destacarEmFormulario() {
		formulario.getFichario().getConexoes().destacarEmFormulario(formulario, this);
	}

	@Override
	protected void clonarEmFormulario() {
		formulario.getFichario().getConexoes().clonarEmFormulario(formulario, this);
	}

	@Override
	protected void abrirEmFormulario() {
		ConexaoFormulario.criar(formulario);
	}

	@Override
	protected void retornoAoFichario() {
		if (conexaoFormulario != null) {
			conexaoFormulario.retornoAoFichario();
		}
	}

	public void setJanela(IJanela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action desconectaAcao = Action.actionIcon("label.final_conexoes", Icones.BANCO_DESCONECTA);
		private Action conectaAcao = Action.actionIcon("label.conectar", Icones.CONECTA);
		private Action sucessoAcao = Action.actionIcon("label.aplicar", Icones.SUCESSO);
		private Action descerAcao = Action.actionIcon("label.descer", Icones.BAIXAR2);
		private Action copiarAcao = Action.actionIcon("label.copiar", Icones.COPIA);
		private Action subirAcao = Action.actionIcon("label.subir", Icones.TOP);
		private Action infoAcao = Action.actionIcon("label.info", Icones.INFO);

		public void ini(IJanela janela) {
			super.ini(janela, true, true);
			configButtonDestacar(e -> destacarEmFormulario(), e -> abrirEmFormulario(), e -> retornoAoFichario(),
					e -> clonarEmFormulario());
			configAbrirAutoFichario(Constantes.ABRIR_AUTO_FICHARIO_CONEXAO);
			configBaixarAcao(null);

			addButton(true, descerAcao);
			addButton(subirAcao);
			addButton(true, conectaAcao);
			addButton(true, sucessoAcao);
			addButton(true, infoAcao);
			addButton(true, desconectaAcao);
			addButton(copiarAcao);

			eventos();
		}

		@Override
		protected void limpar() {
			modelo.novo();
		}

		@Override
		protected void salvar() {
			try {
				modelo.salvar();
				formulario.atualizarConexoes();
			} catch (Exception ex) {
				Util.stackTraceAndMessage("SALVAR: ", ex, ConexaoContainer.this);
			}
		}

		private void eventos() {
			getLimparAcao().rotulo(Constantes.LABEL_NOVO);

			infoAcao.setActionListener(e -> formulario.getFichario().infoConexao());

			sucessoAcao.setActionListener(e -> selecionarConexao());

			conectaAcao.setActionListener(e -> conectar());

			copiarAcao.setActionListener(e -> copiar());

			descerAcao.setActionListener(e -> descer());

			subirAcao.setActionListener(e -> subir());

			getBaixarAcao().setActionListener(e -> {
				try {
					modelo.abrir();
					formulario.atualizarConexoes();
					TabelaUtil.ajustar(tabela, getGraphics());
				} catch (Exception ex) {
					Util.stackTraceAndMessage("ABRIR: ", ex, ConexaoContainer.this);
				}
			});

			desconectaAcao.setActionListener(e -> {
				try {
					Conexao.fecharConexoes();
					tabela.repaint();
				} catch (Exception ex) {
					Util.stackTraceAndMessage(getClass().getName() + ".fechar()", ex, formulario);
				}
			});
		}

		private void selecionarConexao() {
			int[] linhas = tabela.getSelectedRows();

			if (linhas != null && linhas.length == 1) {
				Conexao c = modelo.getConexao(linhas[0]);
				formulario.getFichario().selecionarConexao(c);
			}
		}

		private void subir() {
			int[] linhas = tabela.getSelectedRows();
			int registros = modelo.getRowCount();

			if (linhas != null && linhas.length == 1 && registros > 1 && linhas[0] > 0) {
				int i = modelo.anterior(linhas[0]);
				modelo.fireTableDataChanged();

				if (i != -1) {
					tabela.setRowSelectionInterval(i, i);
				}
			}
		}

		private void descer() {
			int[] linhas = tabela.getSelectedRows();
			int registros = modelo.getRowCount();

			if (linhas != null && linhas.length == 1 && registros > 1 && linhas[0] + 1 < registros) {
				int i = modelo.proximo(linhas[0]);
				modelo.fireTableDataChanged();

				if (i != -1) {
					tabela.setRowSelectionInterval(i, i);
				}
			}
		}

		private void copiar() {
			int[] linhas = tabela.getSelectedRows();

			if (linhas != null && linhas.length > 0) {
				for (int i : linhas) {
					Conexao c = modelo.getConexao(i);
					modelo.adicionar(c.clonar());
				}

				modelo.fireTableDataChanged();
			}
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
					Util.stackTraceAndMessage(Constantes.ERRO, ex, ConexaoContainer.this);
				}
			}
		}
	}
}