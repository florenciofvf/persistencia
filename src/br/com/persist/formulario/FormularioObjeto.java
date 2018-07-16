package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import br.com.persist.Objeto;
import br.com.persist.banco.Persistencia;
import br.com.persist.comp.Button;
import br.com.persist.comp.ScrollPane;
import br.com.persist.tabela.CabecalhoColuna;
import br.com.persist.tabela.CellRenderer;
import br.com.persist.tabela.Coluna;
import br.com.persist.tabela.IndiceValor;
import br.com.persist.tabela.ModeloOrdenacao;
import br.com.persist.tabela.ModeloRegistro;
import br.com.persist.tabela.Tabela;
import br.com.persist.tabela.TabelaUtil;
import br.com.persist.util.Acao;
import br.com.persist.util.Icones;
import br.com.persist.util.Util;

public class FormularioObjeto extends JFrame {
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private CabecalhoColuna cabecalhoFiltro;
	private final Objeto objeto;
	private Tabela tabela;

	public FormularioObjeto(Formulario formulario, Objeto objeto, Graphics g) {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle(objeto.getId());
		this.objeto = objeto;
		setSize(800, 600);
		setLocationRelativeTo(formulario);
		processarObjeto("", g, null);
		montarLayout();
		setVisible(true);
	}

	public void processarObjeto(String complemento, Graphics g, CabecalhoColuna cabecalho) {
		String[] chaves = objeto.getChaves().trim().split(",");
		String consulta = "SELECT * FROM " + objeto.getTabela() + " WHERE 1=1 " + complemento;

		try {
			ModeloRegistro modeloRegistro = Persistencia.criarModeloRegistro(consulta, chaves, objeto.getTabela());
			ModeloOrdenacao modeloOrdenacao = new ModeloOrdenacao(modeloRegistro);
			setTitle(objeto.getId() + " [" + modeloOrdenacao.getRowCount() + "]");
			cabecalhoFiltro = null;

			if (tabela == null) {
				tabela = new Tabela(modeloOrdenacao);
			} else {
				tabela.setModel(modeloOrdenacao);
			}

			TableColumnModel columnModel = tabela.getColumnModel();
			List<Coluna> colunas = modeloRegistro.getColunas();

			for (int i = 0; i < colunas.size(); i++) {
				TableColumn tableColumn = columnModel.getColumn(i);
				Coluna coluna = colunas.get(i);

				if (coluna.isChave()) {
					tableColumn.setCellRenderer(new CellRenderer());
				}

				CabecalhoColuna cabecalhoColuna = new CabecalhoColuna(this, modeloOrdenacao, coluna);

				if (cabecalhoColuna.equals(cabecalho)) {
					cabecalhoColuna.copiar(cabecalho);
					cabecalhoFiltro = cabecalhoColuna;
				}

				tableColumn.setHeaderRenderer(cabecalhoColuna);
			}

			TabelaUtil.ajustar(tabela, g == null ? getGraphics() : g, 40);
		} catch (Exception ex) {
			Util.stackTraceAndMessage("FILTRO", ex, this);
		}
	}

	private void montarLayout() {
		setLayout(new BorderLayout());
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(tabela));
	}

	private class Toolbar extends JToolBar {
		private static final long serialVersionUID = 1L;

		public Toolbar() {
			add(new Button(new FecharAcao(false)));
			addSeparator();
			add(new Button(new ExcluirRegistrosAcao()));
			add(new Button(new SincronizarRegistrosAcao()));
			add(new Button(new AtualizarRegistrosAcao()));
		}
	}

	private class FecharAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public FecharAcao(boolean menu) {
			super(menu, "label.fechar", Icones.SAIR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}

	private class SincronizarRegistrosAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public SincronizarRegistrosAcao() {
			super(false, "label.sincronizar", Icones.SINCRONIZAR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			cabecalhoFiltro = null;
			new AtualizarRegistrosAcao().actionPerformed(null);
		}
	}

	private class AtualizarRegistrosAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public AtualizarRegistrosAcao() {
			super(false, "label.atualizar", Icones.ATUALIZAR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			processarObjeto(cabecalhoFiltro == null ? "" : cabecalhoFiltro.getFiltroComplemento(), null,
					cabecalhoFiltro);
		}
	}

	private class ExcluirRegistrosAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public ExcluirRegistrosAcao() {
			super(false, "label.excluir_registro", Icones.EXCLUIR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int[] linhas = tabela.getSelectedRows();

			if (linhas != null && linhas.length > 0) {
				if (Util.confirmaExclusao(FormularioObjeto.this)) {
					ModeloOrdenacao modelo = (ModeloOrdenacao) tabela.getModel();

					List<List<IndiceValor>> listaValores = new ArrayList<>();

					for (int linha : linhas) {
						int excluido = modelo.excluirRegistro(linha);

						if (excluido == 1) {
							List<IndiceValor> chaves = modelo.getValoresChaves(linha);

							if (chaves.isEmpty()) {
								throw new IllegalStateException();
							}

							listaValores.add(chaves);
						}
					}

					modelo.excluirValoresChaves(listaValores);
					modelo.iniArray();
					modelo.fireTableDataChanged();
				}
			}
		}
	}
}