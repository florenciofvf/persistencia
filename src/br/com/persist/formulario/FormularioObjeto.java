package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import br.com.persist.Objeto;
import br.com.persist.banco.Conexao;
import br.com.persist.banco.Persistencia;
import br.com.persist.comp.Button;
import br.com.persist.comp.Label;
import br.com.persist.comp.ScrollPane;
import br.com.persist.modelo.ModeloOrdenacao;
import br.com.persist.modelo.ModeloRegistro;
import br.com.persist.tabela.CabecalhoColuna;
import br.com.persist.tabela.CellRenderer;
import br.com.persist.tabela.Coluna;
import br.com.persist.tabela.IndiceValor;
import br.com.persist.tabela.Tabela;
import br.com.persist.tabela.TabelaUtil;
import br.com.persist.util.Acao;
import br.com.persist.util.Icones;
import br.com.persist.util.Util;

public class FormularioObjeto extends JFrame implements ItemListener {
	private static final long serialVersionUID = 1L;
	private final JTextField txtComplemento = new JTextField(20);
	private final Toolbar toolbar = new Toolbar();
	private final JComboBox<Conexao> cmbConexao;
	private final Tabela tabela = new Tabela();
	private CabecalhoColuna cabecalhoFiltro;
	private final Objeto objeto;

	public FormularioObjeto(Formulario formulario, Objeto objeto, Graphics g, Conexao padrao) {
		cmbConexao = new JComboBox<>(formulario.getConexoes());
		txtComplemento.setText(objeto.getComplemento());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		if (padrao != null) {
			cmbConexao.setSelectedItem(padrao);
		}
		cmbConexao.addItemListener(this);
		toolbar.add(txtComplemento);
		setTitle(objeto.getId());
		toolbar.complementoBtn();
		toolbar.add(cmbConexao);
		this.objeto = objeto;
		setSize(800, 600);
		setLocationRelativeTo(formulario);
		processarObjeto("", g, null);
		montarLayout();
		setVisible(true);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

		if (conexao == null) {
			return;
		}

		ModeloOrdenacao modelo = (ModeloOrdenacao) tabela.getModel();
		TableModel model = modelo.getModel();

		if (model instanceof ModeloRegistro) {
			ModeloRegistro modeloRegistro = (ModeloRegistro) model;
			modeloRegistro.setConexao(conexao);
		}
	}

	public void processarObjeto(String complemento, Graphics g, CabecalhoColuna cabecalho) {
		String[] chaves = objeto.getChaves().trim().split(",");
		StringBuilder builder = new StringBuilder("SELECT * FROM " + objeto.getTabela() + " WHERE 1=1");
		builder.append(" " + txtComplemento.getText());
		builder.append(" " + complemento);

		Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

		if (conexao == null) {
			return;
		}

		try {
			Connection conn = Conexao.getConnection(conexao);
			ModeloRegistro modeloRegistro = Persistencia.criarModeloRegistro(conn, builder.toString(), chaves,
					objeto.getTabela());
			ModeloOrdenacao modeloOrdenacao = new ModeloOrdenacao(modeloRegistro);
			setTitle(objeto.getId() + " [" + modeloOrdenacao.getRowCount() + "]");

			modeloRegistro.setConexao(conexao);
			tabela.setModel(modeloOrdenacao);
			cabecalhoFiltro = null;

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
		final Label total = new Label(Color.BLUE);

		public Toolbar() {
			add(new Button(new FecharAcao()));
			addSeparator();
			add(new Button(new ExcluirRegistrosAcao()));
			add(new Button(new SincronizarRegistrosAcao()));
			add(new Button(new AtualizarRegistrosAcao()));
			add(new Button(new TotalizarRegistrosAcao()));
			add(total);
		}

		void complementoBtn() {
			add(new Button(new LimparAcao()));
			add(new Button(new BaixarAcao()));
		}
	}

	private class LimparAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public LimparAcao() {
			super(false, "label.limpar", Icones.NOVO);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			txtComplemento.setText("");
		}
	}

	private class BaixarAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public BaixarAcao() {
			super(false, "label.baixar", Icones.BAIXAR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			txtComplemento.setText(objeto.getComplemento());
		}
	}

	private class FecharAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public FecharAcao() {
			super(false, "label.fechar", Icones.SAIR);
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

	private class TotalizarRegistrosAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public TotalizarRegistrosAcao() {
			super(false, "label.total", Icones.SOMA);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

			if (conexao == null) {
				return;
			}

			try {
				Connection conn = Conexao.getConnection(conexao);
				int i = Persistencia.getTotalRegistros(conn, objeto);
				toolbar.total.setText("" + i);
			} catch (Exception ex) {
				Util.stackTraceAndMessage("TOTAL", ex, FormularioObjeto.this);
			}
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