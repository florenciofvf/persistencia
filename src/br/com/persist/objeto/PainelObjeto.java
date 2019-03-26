package br.com.persist.objeto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import br.com.persist.Instrucao;
import br.com.persist.Objeto;
import br.com.persist.banco.Conexao;
import br.com.persist.banco.Persistencia;
import br.com.persist.comp.Button;
import br.com.persist.comp.Label;
import br.com.persist.comp.MenuItem;
import br.com.persist.comp.Panel;
import br.com.persist.comp.Popup;
import br.com.persist.comp.ScrollPane;
import br.com.persist.comp.TextField;
import br.com.persist.dialogo.ComplementoDialogo;
import br.com.persist.dialogo.FragmentoDialogo;
import br.com.persist.dialogo.FragmentoDialogo.FragmentoListener;
import br.com.persist.formulario.Transferidor;
import br.com.persist.modelo.ListagemModelo;
import br.com.persist.modelo.OrdenacaoModelo;
import br.com.persist.modelo.RegistroModelo;
import br.com.persist.tabela.CabecalhoColuna;
import br.com.persist.tabela.CellRenderer;
import br.com.persist.tabela.Coluna;
import br.com.persist.tabela.IndiceValor;
import br.com.persist.tabela.Tabela;
import br.com.persist.tabela.TabelaListener;
import br.com.persist.tabela.TabelaUtil;
import br.com.persist.util.Acao;
import br.com.persist.util.Action;
import br.com.persist.util.BuscaAuto;
import br.com.persist.util.BuscaAuto.Grupo;
import br.com.persist.util.Constantes;
import br.com.persist.util.Fragmento;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Preferencias;
import br.com.persist.util.Util;

public class PainelObjeto extends Panel implements ActionListener, ItemListener {
	private static final long serialVersionUID = 1L;
	private final Button btnArrasto = new Button(Action.actionIcon("label.destacar", Icones.ARRASTAR, e -> {
	}));
	private final TextField txtComplemento = new TextField(35);
	private final Toolbar toolbar = new Toolbar();
	private final JComboBox<Conexao> cmbConexao;
	private final PainelObjetoListener listener;
	private final Tabela tabela = new Tabela();
	private CabecalhoColuna cabecalhoFiltro;
	private final String nomeTabela;
	private final boolean buscaAuto;
	private final Objeto objeto;

	public PainelObjeto(PainelObjetoListener listener, Objeto objeto, Graphics g, Conexao padrao, boolean buscaAuto) {
		tabela.setMapaChaveamento(Util.criarMapaCampoNomes(objeto.getChaveamento()));
		cmbConexao = new JComboBox<>(listener.getConexoes());
		txtComplemento.addMouseListener(complementoListener);
		txtComplemento.setText(objeto.getComplemento());
		this.nomeTabela = objeto.getTabela2() + " - ";
		if (padrao != null) {
			cmbConexao.setSelectedItem(padrao);
		}
		tabela.setTabelaListener(tabelaListener);
		txtComplemento.addActionListener(this);
		toolbar.complementoBuscaAuto(objeto);
		toolbar.complementoUpdate(objeto);
		cmbConexao.addItemListener(this);
		toolbar.add(txtComplemento);
		this.buscaAuto = buscaAuto;
		this.listener = listener;
		toolbar.complementoBtn();
		toolbar.add(cmbConexao);
		this.objeto = objeto;
		processarObjeto("", g, null);
		montarLayout();
		config();
	}

	private MouseListener complementoListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() >= Constantes.DOIS) {
				new ComplementoDialogo(listener.getFrame(), objeto, txtComplemento);
			}
		}
	};

	public Objeto getObjeto() {
		return objeto;
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
			String apelido = null;

			if (listener instanceof FormularioInterno) {
				FormularioInterno interno = (FormularioInterno) listener;
				apelido = interno.getApelido();
			}

			dge.startDrag(null, new Transferidor(objeto, conexao, listener.getDimensoes(), apelido), listenerArrasto);
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
			if (Preferencias.fechar_apos_soltar && dsde.getDropSuccess()) {
				toolbar.fecharAcao.actionPerformed(null);
			}
		}
	};

	@Override
	public void itemStateChanged(ItemEvent e) {
		Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

		if (conexao == null) {
			return;
		}

		OrdenacaoModelo modelo = (OrdenacaoModelo) tabela.getModel();
		TableModel model = modelo.getModel();

		if (model instanceof RegistroModelo) {
			RegistroModelo modeloRegistro = (RegistroModelo) model;
			modeloRegistro.setConexao(conexao);
		}
	}

	public void processarObjeto(String complemento, Graphics g, CabecalhoColuna cabecalho) {
		Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

		if (conexao == null) {
			return;
		}

		StringBuilder builder = new StringBuilder(
				"SELECT * FROM " + objeto.getTabela(conexao.getEsquema()) + " WHERE 1=1");
		builder.append(" " + txtComplemento.getText());
		builder.append(" " + complemento);

		try {
			Connection conn = Conexao.getConnection(conexao);
			RegistroModelo modeloRegistro = Persistencia.criarModeloRegistro(conn, builder.toString(),
					objeto.getChavesArray(), objeto, conexao);
			OrdenacaoModelo modeloOrdenacao = new OrdenacaoModelo(modeloRegistro);
			listener.setTitle(nomeTabela + objeto.getId() + " [" + modeloOrdenacao.getRowCount() + "]");

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

				CabecalhoColuna cabecalhoColuna = new CabecalhoColuna(this, modeloOrdenacao, coluna, true);

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

		toolbar.buscaAuto.habilitar(tabela.getModel().getRowCount() > 0 && buscaAuto);
		tabelaListener.tabelaMouseClick(tabela);
	}

	private void montarLayout() {
		setLayout(new BorderLayout());
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(tabela));
	}

	private class Toolbar extends JToolBar {
		private static final long serialVersionUID = 1L;
		private Action complementoAcao = Action.actionIcon("label.complemento", Icones.BAIXAR2);
		private Action fragmentoAcao = Action.actionIcon("label.fragmento", Icones.FRAGMENTO);
		private Action fecharAcao = Action.actionIcon("label.fechar", Icones.SAIR);
		final Button excluir = new Button(new ExcluirRegistrosAcao());
		final ButtonAtualizar atualizar = new ButtonAtualizar();
		final ButtonBuscaAuto buscaAuto = new ButtonBuscaAuto();
		final ButtonFuncoes funcoes = new ButtonFuncoes();
		final ButtonUpdate update = new ButtonUpdate();
		final Label total = new Label(Color.BLUE);

		Toolbar() {
			add(new Button(fecharAcao));
			addSeparator();
			add(btnArrasto);
			addSeparator();
			add(new ButtonInfo());
			addSeparator();
			add(excluir);
			addSeparator();
			add(new Button(fragmentoAcao));
			add(buscaAuto);
			addSeparator();
			add(update);
			add(atualizar);
			addSeparator();
			add(new Button(complementoAcao));

			eventos();
		}

		private void eventos() {
			complementoAcao.setActionListener(e -> {
				Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

				if (conexao == null) {
					return;
				}

				String complemento = Util.getContentTransfered();

				if (!Util.estaVazio(complemento)) {
					txtComplemento.setText(complemento);
					objeto.setComplemento(txtComplemento.getText());
					PainelObjeto.this.actionPerformed(null);
				} else {
					txtComplemento.setText(objeto.getComplemento());
				}
			});

			fragmentoAcao.setActionListener(e -> {
				FragmentoDialogo dialogo = new FragmentoDialogo(null, fragmentoListener);

				if (listener instanceof Component) {
					dialogo.setLocationRelativeTo((Component) listener);
				}

				dialogo.setVisible(true);
			});

			fecharAcao.setActionListener(e -> listener.dispose());
		}

		void complementoBtn() {
			add(total);
			add(funcoes);
			addSeparator();

			Action limparAcao = Action.actionIcon("label.limpar", Icones.NOVO, e -> txtComplemento.setText(""));
			add(new Button(limparAcao));

			Action baixarAcao = Action.actionIcon("label.baixar", Icones.BAIXAR,
					e -> txtComplemento.setText(objeto.getComplemento()));
			add(new Button(baixarAcao));
		}

		void complementoBuscaAuto(Objeto objeto) {
			buscaAuto.complemento(objeto);
		}

		void complementoUpdate(Objeto objeto) {
			update.complemento(objeto);
		}

		public void excluirAtualizarEnable(boolean b) {
			excluir.setEnabled(b);
			update.setEnabled(b);
		}

		class ExcluirRegistrosAcao extends Acao {
			private static final long serialVersionUID = 1L;

			ExcluirRegistrosAcao() {
				super(false, "label.excluir_registro", Icones.EXCLUIR);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				int[] linhas = tabela.getSelectedRows();

				if (linhas != null && linhas.length > 0) {
					if (Util.confirmaExclusao(PainelObjeto.this)) {
						OrdenacaoModelo modelo = (OrdenacaoModelo) tabela.getModel();

						List<List<IndiceValor>> listaValores = new ArrayList<>();

						for (int linha : linhas) {
							int excluido = modelo.excluirRegistro(linha);

							if (excluido == 0 || excluido == 1) {
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
						tabelaListener.tabelaMouseClick(tabela);
					}
				}
			}
		}
	}

	private class ButtonAtualizar extends Button {
		private static final long serialVersionUID = 1L;
		private Action sincronizarAcao = Action.actionMenu("label.sincronizar", Icones.SINCRONIZAR);
		private Action atualizarAcao = Action.actionMenu("label.atualizar", Icones.ATUALIZAR);
		private Popup popup = new Popup();

		ButtonAtualizar() {
			setToolTipText(Mensagens.getString("label.atualizar"));
			popup.add(new MenuItem(atualizarAcao));
			popup.addSeparator();
			popup.add(new MenuItem(sincronizarAcao));
			setComponentPopupMenu(popup);
			setIcon(Icones.ATUALIZAR);
			addActionListener(e -> popup.show(this, 5, 5));

			eventos();
		}

		private void eventos() {
			atualizarAcao.setActionListener(e -> PainelObjeto.this.actionPerformed(null));
			sincronizarAcao.setActionListener(e -> {
				cabecalhoFiltro = null;
				PainelObjeto.this.actionPerformed(null);
			});
		}
	}

	private class ButtonBuscaAuto extends Button {
		private static final long serialVersionUID = 1L;
		private Popup popup = new Popup();
		private boolean habilitado;

		ButtonBuscaAuto() {
			setToolTipText(Mensagens.getString("label.buscaAuto"));
			setComponentPopupMenu(popup);
			setIcon(Icones.FIELDS);
			addActionListener(e -> popup.show(this, 5, 5));
		}

		void complemento(Objeto objeto) {
			List<Grupo> listaGrupo = BuscaAuto.criarGruposAuto(objeto.getBuscaAutomatica());

			for (Grupo grupo : listaGrupo) {
				popup.add(new MenuBuscaAuto(grupo));
			}

			habilitado = !listaGrupo.isEmpty();
			setEnabled(habilitado);
		}

		void habilitar(boolean b) {
			setEnabled(habilitado && b);
		}

		class MenuBuscaAuto extends JMenu {
			private static final long serialVersionUID = 1L;
			private Action comAspasAcao = Action.actionMenu("label.com_aspas", Icones.ASPAS);
			private Action semAspasAcao = Action.actionMenu("label.sem_aspas", null);
			private final Grupo grupo;

			MenuBuscaAuto(Grupo grupo) {
				super(grupo.getDescricao());
				add(new MenuItem(semAspasAcao));
				add(new MenuItem(comAspasAcao));
				setIcon(Icones.CONFIG2);
				this.grupo = grupo;

				semAspasAcao.setActionListener(e -> processar(false));
				comAspasAcao.setActionListener(e -> processar(true));
			}

			private void processar(boolean apostrofes) {
				int coluna = TabelaUtil.getIndiceColuna(tabela, grupo.getCampo());

				if (coluna == -1) {
					return;
				}

				List<String> lista = TabelaUtil.getValoresColuna(tabela, coluna);

				if (lista.isEmpty()) {
					return;
				}

				String argumentos = Util.getStringLista(lista, apostrofes);
				AtomicBoolean processado = new AtomicBoolean(false);
				listener.buscaAutomatica(grupo, argumentos, processado);
				setEnabled(processado.get());
			}
		}
	}

	private class ButtonUpdate extends Button {
		private static final long serialVersionUID = 1L;
		private Action dadosAcao = Action.actionMenu("label.dados", Icones.TABELA);
		private Popup popup = new Popup();

		ButtonUpdate() {
			setToolTipText(Mensagens.getString("label.update"));
			popup.add(new MenuItem(dadosAcao));
			popup.addSeparator();
			popup.add(new MenuItem(new UpdateAcao()));
			setComponentPopupMenu(popup);
			setIcon(Icones.UPDATE);
			addActionListener(e -> popup.show(this, 5, 5));

			eventos();
		}

		private void eventos() {
			dadosAcao.setActionListener(e -> {
				OrdenacaoModelo modelo = (OrdenacaoModelo) tabela.getModel();
				TableModel model = modelo.getModel();

				if (model instanceof RegistroModelo) {
					int[] linhas = tabela.getSelectedRows();

					if (linhas != null && linhas.length == 1) {
						StringBuilder sb = new StringBuilder(objeto.getTabela2());
						sb.append(Constantes.QL);
						modelo.getDados(linhas[0], sb);
						Util.mensagem(PainelObjeto.this, sb.toString());
					}
				}
			});
		}

		class UpdateAcao extends Acao {
			private static final long serialVersionUID = 1L;

			UpdateAcao() {
				super(true, "label.update", Icones.UPDATE);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

				if (conexao == null) {
					return;
				}

				OrdenacaoModelo modelo = (OrdenacaoModelo) tabela.getModel();
				TableModel model = modelo.getModel();

				if (model instanceof RegistroModelo) {
					int[] linhas = tabela.getSelectedRows();

					if (linhas != null && linhas.length == 1) {
						List<IndiceValor> chaves = modelo.getValoresChaves(linhas[0]);

						if (chaves.isEmpty()) {
							return;
						}

						String update = modelo.getUpdate(linhas[0]);

						if (Util.estaVazio(update)) {
							return;
						}

						FormularioUpdate form = new FormularioUpdate(Mensagens.getString("label.atualizar"), listener,
								conexao, update, null);

						if (listener instanceof Component) {
							form.setLocationRelativeTo((Component) listener);
						}

						form.setVisible(true);
					}
				}
			}
		}

		void complemento(Objeto objeto) {
			if (objeto == null || objeto.getInstrucoes().isEmpty()) {
				return;
			}

			popup.addSeparator();

			for (Instrucao i : objeto.getInstrucoes()) {
				if (!Util.estaVazio(i.getValor())) {
					popup.add(new MenuItemUpdate(i));
				}
			}
		}

		class MenuItemUpdate extends JMenuItem implements ActionListener {
			private static final long serialVersionUID = 1L;
			private final Instrucao instrucao;

			MenuItemUpdate(Instrucao instrucao) {
				setIcon(instrucao.isSelect() ? Icones.ATUALIZAR : Icones.CALC);
				setText(instrucao.getNome());
				this.instrucao = instrucao;
				addActionListener(this);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

				if (conexao == null) {
					return;
				}

				OrdenacaoModelo modelo = (OrdenacaoModelo) tabela.getModel();
				TableModel model = modelo.getModel();

				if (model instanceof RegistroModelo) {
					int[] linhas = tabela.getSelectedRows();

					if (linhas != null && linhas.length == 1) {
						Map<String, String> chaves = modelo.getMapaChaves(linhas[0]);

						if (chaves.isEmpty()) {
							return;
						}

						if (Util.estaVazio(instrucao.getValor())) {
							return;
						}

						if (instrucao.isSelect()) {
							FormularioSelect form = new FormularioSelect(instrucao.getNome(), listener, conexao,
									instrucao.getValor(), chaves);

							if (listener instanceof Component) {
								form.setLocationRelativeTo((Component) listener);
							}

							form.setVisible(true);
						} else {
							FormularioUpdate form = new FormularioUpdate(instrucao.getNome(), listener, conexao,
									instrucao.getValor(), chaves);

							if (listener instanceof Component) {
								form.setLocationRelativeTo((Component) listener);
							}

							form.setVisible(true);
						}
					}
				}
			}
		}
	}

	private class ButtonFuncoes extends Button {
		private static final long serialVersionUID = 1L;
		private Popup popup = new Popup();

		ButtonFuncoes() {
			setToolTipText(Mensagens.getString("label.funcoes"));
			popup.add(new MenuItem(new TotalizarRegistrosAcao(false)));
			popup.addSeparator();
			popup.add(new MenuItem(new TotalizarRegistrosAcao(true)));
			popup.addSeparator();
			MenuItem minimo = new MenuItem(new MinimoAcao());
			minimo.setToolTipText(Mensagens.getString("msg.maximo_minimo"));
			popup.add(minimo);
			MenuItem maximo = new MenuItem(new MaximoAcao());
			maximo.setToolTipText(Mensagens.getString("msg.maximo_minimo"));
			popup.add(maximo);
			setComponentPopupMenu(popup);
			setIcon(Icones.SOMA);
			addActionListener(e -> popup.show(this, 5, 5));
		}

		class MaximoAcao extends Acao {
			private static final long serialVersionUID = 1L;

			MaximoAcao() {
				super(true, "label.maximo", Icones.VAR);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

				if (conexao == null) {
					return;
				}

				String[] chaves = objeto.getChavesArray();

				if (chaves.length != 1) {
					txtComplemento.setText("");
					return;
				}

				txtComplemento.setText("AND " + chaves[0] + " = (SELECT MAX(" + chaves[0] + ") FROM "
						+ objeto.getTabela(conexao.getEsquema()) + ")");
				PainelObjeto.this.actionPerformed(null);
			}
		}

		class MinimoAcao extends Acao {
			private static final long serialVersionUID = 1L;

			MinimoAcao() {
				super(true, "label.minimo", Icones.VAR);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

				if (conexao == null) {
					return;
				}

				String[] chaves = objeto.getChavesArray();

				if (chaves.length != 1) {
					txtComplemento.setText("");
					return;
				}

				txtComplemento.setText("AND " + chaves[0] + " = (SELECT MIN(" + chaves[0] + ") FROM "
						+ objeto.getTabela(conexao.getEsquema()) + ")");
				PainelObjeto.this.actionPerformed(null);
			}
		}

		class TotalizarRegistrosAcao extends Acao {
			private static final long serialVersionUID = 1L;
			private final boolean complemento;

			TotalizarRegistrosAcao(boolean complemento) {
				super(true, complemento ? "label.total_filtro" : "label.total", Icones.SOMA);
				this.complemento = complemento;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

				if (conexao == null) {
					return;
				}

				try {
					Connection conn = Conexao.getConnection(conexao);
					int i = Persistencia.getTotalRegistros(conn, objeto, complemento ? txtComplemento.getText() : "",
							conexao);
					toolbar.total.setText("" + i);
				} catch (Exception ex) {
					Util.stackTraceAndMessage("TOTAL", ex, PainelObjeto.this);
				}
			}
		}
	}

	private class ButtonInfo extends Button {
		private static final long serialVersionUID = 1L;
		private Action apelidoAcao = Action.actionMenu("label.apelido", Icones.TAG2);
		private Popup popup = new Popup();

		ButtonInfo() {
			setToolTipText(Mensagens.getString("label.meta_dados"));
			popup.add(new MenuItem(apelidoAcao));
			popup.addSeparator();
			popup.add(new MenuItem(new ChavesPrimariasAcao()));
			popup.addSeparator();
			popup.add(new MenuItem(new ChavesExportadasAcao()));
			popup.add(new MenuItem(new ChavesImportadasAcao()));
			popup.addSeparator();
			popup.add(new MenuItem(new MetaDadosAcao()));
			popup.addSeparator();
			popup.add(new MenuItem(new InfoBancoAcao()));
			popup.add(new MenuItem(new EsquemaAcao()));
			setComponentPopupMenu(popup);
			setIcon(Icones.INFO);
			addActionListener(e -> popup.show(this, 5, 5));

			eventos();
		}

		private void eventos() {
			apelidoAcao.setActionListener(e -> {
				if (listener instanceof FormularioInterno) {
					FormularioInterno interno = (FormularioInterno) listener;
					String valor = interno.getApelido();
					String resp = Util.getValorInputDialog(PainelObjeto.this, "label.apelido", valor);

					if (resp == null) {
						return;
					}

					interno.setApelido(resp);
				}
			});
		}

		class ChavesPrimariasAcao extends Acao {
			private static final long serialVersionUID = 1L;

			ChavesPrimariasAcao() {
				super(true, "label.chave_primaria", Icones.PKEY);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

				if (conexao == null) {
					return;
				}

				try {
					Connection conn = Conexao.getConnection(conexao);
					ListagemModelo modeloListagem = Persistencia.criarModeloChavePrimaria(conn, objeto, conexao);
					OrdenacaoModelo modeloOrdenacao = new OrdenacaoModelo(modeloListagem);
					listener.setTitle(
							nomeTabela + objeto.getId() + " [" + modeloOrdenacao.getRowCount() + "] - CHAVE-PRIMARIA");

					tabela.setModel(modeloOrdenacao);
					configCabecalhoColuna(modeloListagem);
					TabelaUtil.ajustar(tabela, getGraphics(), 40);
				} catch (Exception ex) {
					Util.stackTraceAndMessage("CHAVE-PRIMARIA", ex, PainelObjeto.this);
				}
			}
		}

		class ChavesImportadasAcao extends Acao {
			private static final long serialVersionUID = 1L;

			ChavesImportadasAcao() {
				super(true, "label.chaves_importadas", Icones.KEY);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

				if (conexao == null) {
					return;
				}

				try {
					Connection conn = Conexao.getConnection(conexao);
					ListagemModelo modeloListagem = Persistencia.criarModeloChavesImportadas(conn, objeto, conexao);
					OrdenacaoModelo modeloOrdenacao = new OrdenacaoModelo(modeloListagem);
					listener.setTitle(nomeTabela + objeto.getId() + " [" + modeloOrdenacao.getRowCount()
							+ "] - CHAVES-IMPORTADAS");

					tabela.setModel(modeloOrdenacao);
					configCabecalhoColuna(modeloListagem);
					TabelaUtil.ajustar(tabela, getGraphics(), 40);
				} catch (Exception ex) {
					Util.stackTraceAndMessage("CHAVES-IMPORTADAS", ex, PainelObjeto.this);
				}
			}
		}

		class ChavesExportadasAcao extends Acao {
			private static final long serialVersionUID = 1L;

			ChavesExportadasAcao() {
				super(true, "label.chaves_exportadas", Icones.KEY);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

				if (conexao == null) {
					return;
				}

				try {
					Connection conn = Conexao.getConnection(conexao);
					ListagemModelo modeloListagem = Persistencia.criarModeloChavesExportadas(conn, objeto, conexao);
					OrdenacaoModelo modeloOrdenacao = new OrdenacaoModelo(modeloListagem);
					listener.setTitle(nomeTabela + objeto.getId() + " [" + modeloOrdenacao.getRowCount()
							+ "] - CHAVES-EXPORTADAS");

					tabela.setModel(modeloOrdenacao);
					configCabecalhoColuna(modeloListagem);
					TabelaUtil.ajustar(tabela, getGraphics(), 40);
				} catch (Exception ex) {
					Util.stackTraceAndMessage("CHAVES-EXPORTADAS", ex, PainelObjeto.this);
				}
			}
		}

		class InfoBancoAcao extends Acao {
			private static final long serialVersionUID = 1L;

			InfoBancoAcao() {
				super(true, "label.info_banco", null);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

				if (conexao == null) {
					return;
				}

				try {
					Connection conn = Conexao.getConnection(conexao);
					ListagemModelo modeloListagem = Persistencia.criarModeloInfoBanco(conn);
					OrdenacaoModelo modeloOrdenacao = new OrdenacaoModelo(modeloListagem);
					listener.setTitle(
							nomeTabela + objeto.getId() + " [" + modeloOrdenacao.getRowCount() + "] - INFO-BANCO");

					tabela.setModel(modeloOrdenacao);
					configCabecalhoColuna(modeloListagem);
					TabelaUtil.ajustar(tabela, getGraphics(), 40);
				} catch (Exception ex) {
					Util.stackTraceAndMessage("INFO-BANCO", ex, PainelObjeto.this);
				}
			}
		}

		class MetaDadosAcao extends Acao {
			private static final long serialVersionUID = 1L;

			MetaDadosAcao() {
				super(true, "label.meta_dados", null);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

				if (conexao == null) {
					return;
				}

				try {
					Connection conn = Conexao.getConnection(conexao);
					ListagemModelo modeloListagem = Persistencia.criarModeloMetaDados(conn, objeto, conexao);
					OrdenacaoModelo modeloOrdenacao = new OrdenacaoModelo(modeloListagem);
					listener.setTitle(
							nomeTabela + objeto.getId() + " [" + modeloOrdenacao.getRowCount() + "] - META-DADOS");

					tabela.setModel(modeloOrdenacao);
					configCabecalhoColuna(modeloListagem);
					TabelaUtil.ajustar(tabela, getGraphics(), 40);
				} catch (Exception ex) {
					Util.stackTraceAndMessage("META-DADOS", ex, PainelObjeto.this);
				}
			}
		}

		class EsquemaAcao extends Acao {
			private static final long serialVersionUID = 1L;

			EsquemaAcao() {
				super(true, "label.esquema", null);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

				if (conexao == null) {
					return;
				}

				try {
					Connection conn = Conexao.getConnection(conexao);
					ListagemModelo modeloListagem = Persistencia.criarModeloEsquema(conn);
					OrdenacaoModelo modeloOrdenacao = new OrdenacaoModelo(modeloListagem);
					listener.setTitle(
							nomeTabela + objeto.getId() + " [" + modeloOrdenacao.getRowCount() + "] - ESQUEMA");

					tabela.setModel(modeloOrdenacao);
					configCabecalhoColuna(modeloListagem);
					TabelaUtil.ajustar(tabela, getGraphics(), 40);
				} catch (Exception ex) {
					Util.stackTraceAndMessage("ESQUEMA", ex, PainelObjeto.this);
				}
			}
		}

		private void configCabecalhoColuna(ListagemModelo modelo) {
			OrdenacaoModelo modeloOrdenacao = (OrdenacaoModelo) tabela.getModel();
			TableColumnModel columnModel = tabela.getColumnModel();
			List<Coluna> colunas = modelo.getColunasInfo();

			for (int i = 0; i < colunas.size(); i++) {
				TableColumn tableColumn = columnModel.getColumn(i);
				Coluna coluna = colunas.get(i);

				CabecalhoColuna cabecalhoColuna = new CabecalhoColuna(PainelObjeto.this, modeloOrdenacao, coluna,
						false);

				tableColumn.setHeaderRenderer(cabecalhoColuna);
			}
		}
	}

	private FragmentoListener fragmentoListener = new FragmentoListener() {
		@Override
		public void configFragmento(Fragmento f) {
			txtComplemento.setText(f.getValor());
			objeto.setComplemento(txtComplemento.getText());
			actionPerformed(null);
		}

		@Override
		public List<String> getGruposFiltro() {
			OrdenacaoModelo modelo = (OrdenacaoModelo) tabela.getModel();
			List<String> colunas = new ArrayList<>();
			TableModel model = modelo.getModel();

			for (int i = 0; i < model.getColumnCount(); i++) {
				colunas.add(model.getColumnName(i).toUpperCase());
			}

			return colunas;
		}
	};

	@Override
	public void actionPerformed(ActionEvent e) {
		processarObjeto(cabecalhoFiltro == null ? "" : cabecalhoFiltro.getFiltroComplemento(), null, cabecalhoFiltro);
	}

	public void buscaAutomatica(String campo, String argumentos) {
		Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

		if (conexao == null) {
			return;
		}

		String complemento = "AND " + campo + " IN (" + argumentos + ")";
		txtComplemento.setText(complemento);
		objeto.setComplemento(txtComplemento.getText());
		PainelObjeto.this.actionPerformed(null);
	}

	public void atualizarFormulario() {
		Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

		if (conexao == null) {
			return;
		}

		PainelObjeto.this.actionPerformed(null);
	}

	private TabelaListener tabelaListener = new TabelaListener() {
		@Override
		public void copiarNomeColuna(Tabela tabela, String nome) {
			txtComplemento.setText("AND " + nome + " = ");
		}

		@Override
		public void tabelaMouseClick(Tabela tabela) {
			OrdenacaoModelo modelo = (OrdenacaoModelo) tabela.getModel();
			TableModel model = modelo.getModel();

			if (model instanceof RegistroModelo) {
				int[] linhas = tabela.getSelectedRows();

				if (linhas != null && linhas.length > 0) {
					String[] chaves = objeto.getChavesArray();

					toolbar.update.setEnabled(chaves.length > 0 && linhas.length == 1);
					toolbar.excluir.setEnabled(chaves.length > 0);
				} else {
					toolbar.excluirAtualizarEnable(false);
				}
			} else {
				toolbar.excluirAtualizarEnable(false);
			}
		}
	};
}