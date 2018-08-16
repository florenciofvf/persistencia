package br.com.persist.objeto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JToolBar;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import br.com.persist.Objeto;
import br.com.persist.banco.Conexao;
import br.com.persist.banco.Persistencia;
import br.com.persist.comp.Button;
import br.com.persist.comp.Label;
import br.com.persist.comp.Panel;
import br.com.persist.comp.ScrollPane;
import br.com.persist.comp.TextField;
import br.com.persist.formulario.Transferidor;
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

public class PainelObjeto extends Panel implements ActionListener, ItemListener {
	private static final long serialVersionUID = 1L;
	private final Button btnArrasto = new Button(new DestacarAcao());
	private final TextField txtComplemento = new TextField(35);
	private final Toolbar toolbar = new Toolbar();
	private final JComboBox<Conexao> cmbConexao;
	private final PainelObjetoListener listener;
	private final Tabela tabela = new Tabela();
	private CabecalhoColuna cabecalhoFiltro;
	private final Objeto objeto;

	public PainelObjeto(PainelObjetoListener listener, Objeto objeto, Graphics g, Conexao padrao) {
		cmbConexao = new JComboBox<>(listener.getConexoes());
		txtComplemento.setText(objeto.getComplemento());
		if (padrao != null) {
			cmbConexao.setSelectedItem(padrao);
		}
		txtComplemento.addActionListener(this);
		cmbConexao.addItemListener(this);
		toolbar.add(txtComplemento);
		this.listener = listener;
		toolbar.complementoBtn();
		toolbar.add(cmbConexao);
		toolbar.add(btnArrasto);
		this.objeto = objeto;
		processarObjeto("", g, null);
		montarLayout();
		config();
	}

	public Frame getFrame() {
		return listener.getFrame();
	}

	private void config() {
		DragSource dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(btnArrasto, DnDConstants.ACTION_COPY, listenerInicio);
	}

	private DragGestureListener listenerInicio = new DragGestureListener() {
		@Override
		public void dragGestureRecognized(DragGestureEvent dge) {
			Conexao conexao = (Conexao) cmbConexao.getSelectedItem();
			dge.startDrag(null, new Transferidor(objeto, conexao, listener.getDimensoes()), listenerArrasto);
		}
	};

	private DragSourceListener listenerArrasto = new DragSourceListener() {
		@Override
		public void dropActionChanged(DragSourceDragEvent dsde) {
		}

		@Override
		public void dragEnter(DragSourceDragEvent dsde) {
		}

		@Override
		public void dragOver(DragSourceDragEvent dsde) {
		}

		@Override
		public void dragExit(DragSourceEvent dse) {
		}

		@Override
		public void dragDropEnd(DragSourceDropEvent dsde) {
		}
	};

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
			ModeloRegistro modeloRegistro = Persistencia.criarModeloRegistro(conn, builder.toString(), chaves, objeto);
			ModeloOrdenacao modeloOrdenacao = new ModeloOrdenacao(modeloRegistro);
			listener.setTitle(objeto.getId() + " [" + modeloOrdenacao.getRowCount() + "]");

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
			Util.stackTraceAndMessage("PAINEL OBJETO", ex, this);
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
			addSeparator();
			add(new Button(new SincronizarRegistrosAcao()));
			add(new Button(new AtualizarRegistrosAcao()));
			addSeparator();
			add(new Button(new TotalizarRegistrosAcao()));
			add(total);
		}

		void complementoBtn() {
			add(new Button(new MaximoAcao()));
			addSeparator();
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

	private class MaximoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public MaximoAcao() {
			super(false, "label.maximo", Icones.VAR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (Util.estaVazio(objeto.getChaves())) {
				txtComplemento.setText("");
				return;
			}

			String[] chaves = objeto.getChaves().trim().split(",");

			if (chaves.length == 1) {
				txtComplemento.setText(
						"AND " + chaves[0] + " = (SELECT MAX(" + chaves[0] + ") FROM " + objeto.getTabela() + ")");
			}
		}
	}

	private class FecharAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public FecharAcao() {
			super(false, "label.fechar", Icones.SAIR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			listener.dispose();
		}
	}

	private class DestacarAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public DestacarAcao() {
			super(false, "label.destacar", Icones.ARRASTAR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
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

	@Override
	public void actionPerformed(ActionEvent e) {
		new AtualizarRegistrosAcao().actionPerformed(null);
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
				Util.stackTraceAndMessage("TOTAL", ex, PainelObjeto.this);
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
				if (Util.confirmaExclusao(PainelObjeto.this)) {
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