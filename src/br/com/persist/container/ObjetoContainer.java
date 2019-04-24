package br.com.persist.container;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.dnd.DnDConstants;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import br.com.persist.Instrucao;
import br.com.persist.banco.Conexao;
import br.com.persist.banco.ConexaoProvedor;
import br.com.persist.banco.Persistencia;
import br.com.persist.comp.BarraButton;
import br.com.persist.comp.Button;
import br.com.persist.comp.Label;
import br.com.persist.comp.MenuItem;
import br.com.persist.comp.Panel;
import br.com.persist.comp.ScrollPane;
import br.com.persist.comp.TextField;
import br.com.persist.desktop.Objeto;
import br.com.persist.dialogo.ComplementoDialogo;
import br.com.persist.dialogo.ConsultaDialogo;
import br.com.persist.dialogo.FragmentoDialogo;
import br.com.persist.dialogo.UpdateDialogo;
import br.com.persist.formulario.ConsultaFormulario;
import br.com.persist.formulario.ObjetoContainerFormularioInterno;
import br.com.persist.formulario.UpdateFormulario;
import br.com.persist.listener.FragmentoListener;
import br.com.persist.listener.ObjetoContainerListener;
import br.com.persist.listener.TabelaListener;
import br.com.persist.modelo.ListagemModelo;
import br.com.persist.modelo.OrdenacaoModelo;
import br.com.persist.modelo.RegistroModelo;
import br.com.persist.renderer.CellInfoRenderer;
import br.com.persist.renderer.CellRenderer;
import br.com.persist.tabela.CabecalhoColuna;
import br.com.persist.tabela.Coluna;
import br.com.persist.tabela.IndiceValor;
import br.com.persist.tabela.Tabela;
import br.com.persist.tabela.TabelaUtil;
import br.com.persist.util.Acao;
import br.com.persist.util.Action;
import br.com.persist.util.BuscaAuto;
import br.com.persist.util.BuscaAuto.Grupo;
import br.com.persist.util.ButtonPopup;
import br.com.persist.util.Constantes;
import br.com.persist.util.Fragmento;
import br.com.persist.util.IIni;
import br.com.persist.util.IJanela;
import br.com.persist.util.Icones;
import br.com.persist.util.LinkAuto;
import br.com.persist.util.LinkAuto.Link;
import br.com.persist.util.Mensagens;
import br.com.persist.util.MenuPadrao2;
import br.com.persist.util.MenuPadrao3;
import br.com.persist.util.Preferencias;
import br.com.persist.util.Transferidor;
import br.com.persist.util.Util;

public class ObjetoContainer extends Panel implements ActionListener, ItemListener, Runnable, IIni {
	private static final long serialVersionUID = 1L;
	private final Button btnArrasto = new Button(Action.actionIconDestacar());
	private final TextField txtComplemento = new TextField(35);
	private final transient ObjetoContainerListener listener;
	private static final Logger LOG = Logger.getGlobal();
	private final transient ConexaoProvedor provedor;
	private final Toolbar toolbar = new Toolbar();
	private final transient List<Link> listaLink;
	private final JComboBox<Conexao> cmbConexao;
	private final Tabela tabela = new Tabela();
	private CabecalhoColuna cabecalhoFiltro;
	private final transient Objeto objeto;
	private final boolean buscaAuto;
	private transient Thread thread;
	private Component suporte;
	private int contadorAuto;

	public ObjetoContainer(IJanela janela, ConexaoProvedor provedor, Conexao padrao, Objeto objeto,
			ObjetoContainerListener listener, Graphics g, boolean buscaAuto) {
		tabela.setMapaChaveamento(Util.criarMapaCampoNomes(objeto.getChaveamento()));
		listaLink = LinkAuto.criarLinksAuto(objeto.getLinkAutomatico());
		cmbConexao = Util.criarComboConexao(provedor, padrao);
		txtComplemento.addMouseListener(complementoListener);
		txtComplemento.setText(objeto.getComplemento());
		tabela.setTabelaListener(tabelaListener);
		txtComplemento.addActionListener(this);
		cmbConexao.addItemListener(this);
		toolbar.ini(janela, objeto);
		this.buscaAuto = buscaAuto;
		this.provedor = provedor;
		this.listener = listener;
		this.objeto = objeto;
		montarLayout();
		configurar();
		processarObjeto("", g, null);
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(tabela));
	}

	private void configurar() {
		DragSource dragSource = DragSource.getDefaultDragSource();

		dragSource.createDefaultDragGestureRecognizer(btnArrasto, DnDConstants.ACTION_COPY, dge -> {
			Conexao conexao = (Conexao) cmbConexao.getSelectedItem();
			String apelido = null;

			if (listener instanceof ObjetoContainerFormularioInterno) {
				ObjetoContainerFormularioInterno interno = (ObjetoContainerFormularioInterno) listener;
				apelido = interno.getApelido();
			}

			dge.startDrag(null, new Transferidor(objeto, conexao, listener.getDimensoes(), apelido), listenerArrasto);
		});
	}

	@Override
	public void ini(Graphics graphics) {
		TabelaUtil.ajustar(tabela, graphics);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action limparAcao = Action.actionIcon("label.limpar", Icones.NOVO, e -> txtComplemento.setText(""));
		private Action complementoAcao = Action.actionIcon("label.complemento", Icones.BAIXAR2);
		private Action fragmentoAcao = Action.actionIcon("label.fragmento", Icones.FRAGMENTO);
		private Action baixarAcao = Action.actionIcon("label.baixar", Icones.BAIXAR,
				e -> txtComplemento.setText(objeto.getComplemento()));
		final Button excluir = new Button(new ExcluirRegistrosAcao());
		final ButtonAtualizar atualizar = new ButtonAtualizar();
		final ButtonBuscaAuto buscaAuto = new ButtonBuscaAuto();
		final ButtonFuncoes funcoes = new ButtonFuncoes();
		final ButtonUpdate update = new ButtonUpdate();
		final Label labelTotal = new Label(Color.BLUE);

		protected void ini(IJanela janela, Objeto objeto) {
			super.ini(janela);

			add(btnArrasto);
			add(true, new ButtonInfo());
			add(true, excluir);
			addButton(true, fragmentoAcao);
			add(buscaAuto);
			add(true, update);
			add(atualizar);
			addButton(true, complementoAcao);
			add(txtComplemento);
			add(labelTotal);
			add(funcoes);
			addButton(true, limparAcao);
			addButton(baixarAcao);
			add(cmbConexao);

			buscaAuto.complemento(objeto);
			update.complemento(objeto);

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
					ObjetoContainer.this.actionPerformed(null);
				} else {
					txtComplemento.setText(objeto.getComplemento());
				}
			});

			fragmentoAcao.setActionListener(e -> {
				FragmentoDialogo form = new FragmentoDialogo((Frame) null, fragmentoListener);

				if (listener instanceof Component) {
					form.setLocationRelativeTo((Component) listener);
				} else if (suporte instanceof Component) {
					form.setLocationRelativeTo(suporte);
				}

				form.setVisible(true);
			});
		}

		void excluirAtualizarEnable(boolean b) {
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

				if (linhas != null && linhas.length > 0 && Util.confirmaExclusao(ObjetoContainer.this)) {
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
					tabelaListener.tabelaMouseClick(tabela, -1);
				}
			}
		}

		class ButtonAtualizar extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action sincronizarAcao = Action.actionMenu(Constantes.LABEL_SINCRONIZAR, Icones.SINCRONIZAR);
			private MenuItem itemAtualizarAuto = new MenuItem(Constantes.LABEL_ATUALIZAR_AUTO, Icones.ATUALIZAR);
			private Action atualizarAcao = Action.actionMenuAtualizar();

			ButtonAtualizar() {
				super(Constantes.LABEL_ATUALIZAR, Icones.ATUALIZAR);

				addMenuItem(atualizarAcao);
				addMenuItem(true, sincronizarAcao);
				addMenuItem(true, itemAtualizarAuto);
				itemAtualizarAuto.setText(itemAtualizarAuto.getText() + "   ");
				itemAtualizarAuto.setToolTipText(Mensagens.getString("hint.atualizar_auto"));

				eventos();
			}

			private void eventos() {
				itemAtualizarAuto.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent e) {
						if (thread == null) {
							itemAtualizarAuto.setText(Mensagens.getString(Constantes.LABEL_ATUALIZAR_AUTO));
							thread = new Thread(ObjetoContainer.this);
							contadorAuto = 0;
							thread.start();
						}
					}

					@Override
					public void mouseExited(MouseEvent e) {
						if (thread != null) {
							thread.interrupt();
							thread = null;
						}
					}
				});

				atualizarAcao.setActionListener(e -> ObjetoContainer.this.actionPerformed(null));
				sincronizarAcao.setActionListener(e -> {
					cabecalhoFiltro = null;
					ObjetoContainer.this.actionPerformed(null);
				});
			}
		}

		class ButtonBuscaAuto extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private boolean habilitado;

			ButtonBuscaAuto() {
				super("label.buscaAuto", Icones.FIELDS);
			}

			void complemento(Objeto objeto) {
				List<Grupo> listaGrupo = BuscaAuto.criarGruposAuto(objeto.getBuscaAutomatica());

				for (Grupo grupo : listaGrupo) {
					addMenu(new MenuBuscaAuto(grupo));
				}

				habilitado = !listaGrupo.isEmpty();
				setEnabled(habilitado);
			}

			void habilitar(boolean b) {
				setEnabled(habilitado && b);
			}

			class MenuBuscaAuto extends MenuPadrao2 {
				private static final long serialVersionUID = 1L;
				private final transient Grupo grupo;

				MenuBuscaAuto(Grupo grupo) {
					super(grupo.getDescricao(), Icones.CONFIG2, "nao_chave");

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

					grupo.setProcessado(false);
					grupo.setArgumentos(lista);
					listener.buscaAutomatica(grupo, Util.getStringLista(lista, apostrofes));
					setEnabled(grupo.isProcessado());

					if (!objeto.isColunaInfo()) {
						return;
					}

					List<Integer> indices = TabelaUtil.getIndicesColuna(tabela);

					for (int i : indices) {
						TabelaUtil.atualizarIndice(i, tabela, grupo, coluna);
					}

					TabelaUtil.ajustar(tabela, ObjetoContainer.this.getGraphics());
				}
			}
		}

		class ButtonUpdate extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action dadosAcao = Action.actionMenu("label.dados", Icones.TABELA);

			ButtonUpdate() {
				super("label.update", Icones.UPDATE);

				addMenuItem(dadosAcao);
				addMenu(true, new MenuUpdate());

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
							Util.mensagem(ObjetoContainer.this, sb.toString());
						}
					}
				});
			}

			class MenuUpdate extends MenuPadrao3 {
				private static final long serialVersionUID = 1L;

				MenuUpdate() {
					super("label.update", Icones.UPDATE);

					formularioAcao.setActionListener(e -> abrirUpdate(true));
					dialogoAcao.setActionListener(e -> abrirUpdate(false));
				}

				private void abrirUpdate(boolean abrirEmForm) {
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

							String instrucao = modelo.getUpdate(linhas[0]);

							if (Util.estaVazio(instrucao)) {
								return;
							}

							abrir(abrirEmForm, conexao, instrucao);
						}
					}
				}

				private void abrir(boolean abrirEmForm, Conexao conexao, String instrucao) {
					if (abrirEmForm) {
						UpdateFormulario form = new UpdateFormulario(Mensagens.getString(Constantes.LABEL_ATUALIZAR),
								provedor, conexao, instrucao);
						if (listener instanceof Component) {
							form.setLocationRelativeTo((Component) listener);
						}
						form.setVisible(true);
					} else {
						UpdateDialogo form = new UpdateDialogo((Frame) null, provedor, conexao, instrucao);
						if (listener instanceof Component) {
							form.setLocationRelativeTo((Component) listener);
						}
						form.setVisible(true);
					}
				}
			}

			void complemento(Objeto objeto) {
				if (objeto == null || objeto.getInstrucoes().isEmpty()) {
					return;
				}

				for (Instrucao i : objeto.getInstrucoes()) {
					if (!Util.estaVazio(i.getValor())) {
						addMenu(true, new MenuInstrucao(i));
					}
				}
			}

			class MenuInstrucao extends MenuPadrao3 {
				private static final long serialVersionUID = 1L;
				private final transient Instrucao instrucao;

				MenuInstrucao(Instrucao instrucao) {
					super(instrucao.getNome(), instrucao.isSelect() ? Icones.ATUALIZAR : Icones.CALC, "nao_chave");
					this.instrucao = instrucao;

					formularioAcao.setActionListener(e -> abrirInstrucao(true));
					dialogoAcao.setActionListener(e -> abrirInstrucao(false));
				}

				public void abrirInstrucao(boolean abrirEmForm) {
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
								abrirSelect(abrirEmForm, conexao, chaves);
							} else {
								abrirUpdate(abrirEmForm, conexao, chaves);
							}
						}
					}
				}

				private void abrirSelect(boolean abrirEmForm, Conexao conexao, Map<String, String> chaves) {
					if (abrirEmForm) {
						ConsultaFormulario form = new ConsultaFormulario(instrucao.getNome(), provedor, conexao,
								instrucao.getValor(), chaves);
						if (listener instanceof Component) {
							form.setLocationRelativeTo((Component) listener);
						}
						form.setVisible(true);
					} else {
						ConsultaDialogo form = new ConsultaDialogo((Frame) null, instrucao.getNome(), provedor, conexao,
								instrucao.getValor(), chaves);
						if (listener instanceof Component) {
							form.setLocationRelativeTo((Component) listener);
						}
						form.setVisible(true);
					}
				}

				private void abrirUpdate(boolean abrirEmForm, Conexao conexao, Map<String, String> chaves) {
					if (abrirEmForm) {
						UpdateFormulario form = new UpdateFormulario(instrucao.getNome(), provedor, conexao,
								instrucao.getValor(), chaves);
						if (listener instanceof Component) {
							form.setLocationRelativeTo((Component) listener);
						}
						form.setVisible(true);
					} else {
						UpdateDialogo form = new UpdateDialogo((Frame) null, instrucao.getNome(), provedor, conexao,
								instrucao.getValor(), chaves);
						if (listener instanceof Component) {
							form.setLocationRelativeTo((Component) listener);
						}
						form.setVisible(true);
					}
				}
			}
		}

		class ButtonFuncoes extends ButtonPopup {
			private static final long serialVersionUID = 1L;

			ButtonFuncoes() {
				super("label.funcoes", Icones.SOMA);

				MenuItem maximo = new MenuItem(new MinimoMaximoAcao(false));
				MenuItem minimo = new MenuItem(new MinimoMaximoAcao(true));
				maximo.setToolTipText(Mensagens.getString("msg.maximo_minimo"));
				minimo.setToolTipText(Mensagens.getString("msg.maximo_minimo"));

				addMenuItem(new TotalizarRegistrosAcao(false));
				addMenuItem(true, new TotalizarRegistrosAcao(true));
				addMenuItem(true, minimo);
				addMenuItem(maximo);
			}

			class MinimoMaximoAcao extends Action {
				private static final long serialVersionUID = 1L;
				private final boolean minimo;

				MinimoMaximoAcao(boolean minimo) {
					super(true, minimo ? "label.minimo" : "label.maximo", Icones.VAR);
					this.minimo = minimo;
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

					if (minimo) {
						txtComplemento.setText("AND " + chaves[0] + " = (SELECT MIN(" + chaves[0] + ") FROM "
								+ objeto.getTabela(conexao.getEsquema()) + ")");
					} else {
						txtComplemento.setText("AND " + chaves[0] + " = (SELECT MAX(" + chaves[0] + ") FROM "
								+ objeto.getTabela(conexao.getEsquema()) + ")");
					}

					ObjetoContainer.this.actionPerformed(null);
				}
			}

			class TotalizarRegistrosAcao extends Action {
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
						int i = Persistencia.getTotalRegistros(conn, objeto,
								complemento ? txtComplemento.getText() : "", conexao);
						toolbar.labelTotal.setText("" + i);
					} catch (Exception ex) {
						Util.stackTraceAndMessage("TOTAL", ex, ObjetoContainer.this);
					}
				}
			}
		}

		class ButtonInfo extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action apelidoAcao = Action.actionMenu("label.apelido", Icones.TAG2);

			ButtonInfo() {
				super("label.meta_dados", Icones.INFO);

				addMenuItem(apelidoAcao);
				addMenuItem(true, new ChavesPrimariasAcao());
				addMenuItem(true, new ChavesExportadasAcao());
				addMenuItem(new ChavesImportadasAcao());
				addMenuItem(true, new MetaDadosAcao());
				addMenuItem(true, new InfoBancoAcao());
				addMenuItem(new EsquemaAcao());

				eventos();
			}

			private void eventos() {
				apelidoAcao.setActionListener(e -> {
					if (listener instanceof ObjetoContainerFormularioInterno) {
						ObjetoContainerFormularioInterno interno = (ObjetoContainerFormularioInterno) listener;
						String valor = interno.getApelido();
						String resp = Util.getValorInputDialog(ObjetoContainer.this, "label.apelido", valor);

						if (resp == null) {
							return;
						}

						interno.setApelido(resp);
					}
				});
			}

			class ChavesPrimariasAcao extends Action {
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
						listener.setTitulo(objeto.getTitle(modeloOrdenacao, "CHAVE-PRIMARIA"));

						tabela.setModel(modeloOrdenacao);
						configCabecalhoColuna(modeloListagem);
						TabelaUtil.ajustar(tabela, ObjetoContainer.this.getGraphics());
					} catch (Exception ex) {
						Util.stackTraceAndMessage("CHAVE-PRIMARIA", ex, ObjetoContainer.this);
					}
				}
			}

			class ChavesImportadasAcao extends Action {
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
						listener.setTitulo(objeto.getTitle(modeloOrdenacao, "CHAVES-IMPORTADAS"));

						tabela.setModel(modeloOrdenacao);
						configCabecalhoColuna(modeloListagem);
						TabelaUtil.ajustar(tabela, ObjetoContainer.this.getGraphics());
					} catch (Exception ex) {
						Util.stackTraceAndMessage("CHAVES-IMPORTADAS", ex, ObjetoContainer.this);
					}
				}
			}

			class ChavesExportadasAcao extends Action {
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
						listener.setTitulo(objeto.getTitle(modeloOrdenacao, "CHAVES-EXPORTADAS"));

						tabela.setModel(modeloOrdenacao);
						configCabecalhoColuna(modeloListagem);
						TabelaUtil.ajustar(tabela, ObjetoContainer.this.getGraphics());
					} catch (Exception ex) {
						Util.stackTraceAndMessage("CHAVES-EXPORTADAS", ex, ObjetoContainer.this);
					}
				}
			}

			class InfoBancoAcao extends Action {
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
						listener.setTitulo(objeto.getTitle(modeloOrdenacao, "INFO-BANCO"));

						tabela.setModel(modeloOrdenacao);
						configCabecalhoColuna(modeloListagem);
						TabelaUtil.ajustar(tabela, ObjetoContainer.this.getGraphics());
					} catch (Exception ex) {
						Util.stackTraceAndMessage("INFO-BANCO", ex, ObjetoContainer.this);
					}
				}
			}

			class MetaDadosAcao extends Action {
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
						listener.setTitulo(objeto.getTitle(modeloOrdenacao, "META-DADOS"));

						tabela.setModel(modeloOrdenacao);
						configCabecalhoColuna(modeloListagem);
						TabelaUtil.ajustar(tabela, ObjetoContainer.this.getGraphics());
					} catch (Exception ex) {
						Util.stackTraceAndMessage("META-DADOS", ex, ObjetoContainer.this);
					}
				}
			}

			class EsquemaAcao extends Action {
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
						listener.setTitulo(objeto.getTitle(modeloOrdenacao, "ESQUEMA"));

						tabela.setModel(modeloOrdenacao);
						configCabecalhoColuna(modeloListagem);
						TabelaUtil.ajustar(tabela, ObjetoContainer.this.getGraphics());
					} catch (Exception ex) {
						Util.stackTraceAndMessage("ESQUEMA", ex, ObjetoContainer.this);
					}
				}
			}

			void configCabecalhoColuna(ListagemModelo modelo) {
				OrdenacaoModelo modeloOrdenacao = (OrdenacaoModelo) tabela.getModel();
				TableColumnModel columnModel = tabela.getColumnModel();
				List<Coluna> colunas = modelo.getColunasInfo();

				for (int i = 0; i < colunas.size(); i++) {
					TableColumn tableColumn = columnModel.getColumn(i);
					Coluna coluna = colunas.get(i);

					CabecalhoColuna cabecalhoColuna = new CabecalhoColuna(ObjetoContainer.this, modeloOrdenacao, coluna,
							false);

					tableColumn.setHeaderRenderer(cabecalhoColuna);
				}

				toolbar.buscaAuto.habilitar(false);
				toolbar.excluirAtualizarEnable(false);
			}
		}
	}

	private transient MouseListener complementoListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() >= Constantes.DOIS) {
				ComplementoDialogo form = new ComplementoDialogo((Dialog) null, objeto, txtComplemento);

				if (listener instanceof Component) {
					form.setLocationRelativeTo((Component) listener);
				} else if (suporte instanceof Component) {
					form.setLocationRelativeTo(suporte);
				}

				form.setVisible(true);
			}
		}
	};

	public Objeto getObjeto() {
		return objeto;
	}

	private transient DragSourceListener listenerArrasto = new DragSourceListener() {
		@Override
		public void dropActionChanged(DragSourceDragEvent dsde) {
			LOG.log(Level.FINEST, "dropActionChanged");
		}

		@Override
		public void dragEnter(DragSourceDragEvent dsde) {
			LOG.log(Level.FINEST, "dragEnter");
		}

		@Override
		public void dragOver(DragSourceDragEvent dsde) {
			LOG.log(Level.FINEST, "dragOver");
		}

		@Override
		public void dragExit(DragSourceEvent dse) {
			LOG.log(Level.FINEST, "dragExit");
		}

		@Override
		public void dragDropEnd(DragSourceDropEvent dsde) {
			if (Preferencias.isFecharAposSoltar() && dsde.getDropSuccess()) {
				toolbar.fechar();
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
		builder.append(" " + objeto.getFinalConsulta());

		try {
			Connection conn = Conexao.getConnection(conexao);
			RegistroModelo modeloRegistro = Persistencia.criarModeloRegistro(conn, builder.toString(),
					objeto.getChavesArray(), objeto, conexao);
			OrdenacaoModelo modeloOrdenacao = new OrdenacaoModelo(modeloRegistro);
			listener.setTitulo(objeto.getTitle(modeloOrdenacao));
			objeto.setComplemento(txtComplemento.getText());
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

				if (coluna.isColunaInfo()) {
					tableColumn.setCellRenderer(new CellInfoRenderer());
				}

				CabecalhoColuna cabecalhoColuna = new CabecalhoColuna(this, modeloOrdenacao, coluna, true);

				if (cabecalhoColuna.equals(cabecalho)) {
					cabecalhoColuna.copiar(cabecalho);
					cabecalhoFiltro = cabecalhoColuna;
				}

				tableColumn.setHeaderRenderer(cabecalhoColuna);
			}

			TabelaUtil.ajustar(tabela, g == null ? getGraphics() : g);

			br.com.persist.util.BuscaAuto.Tabela tabelaPesquisaAuto = objeto.getTabelaPesquisaAuto();

			if (tabelaPesquisaAuto != null) {
				int coluna = TabelaUtil.getIndiceColuna(tabela, tabelaPesquisaAuto.getCampo());

				if (coluna != -1) {
					TabelaUtil.contabilizarTabela(tabela, tabelaPesquisaAuto, coluna);
				}

				objeto.setTabelaPesquisaAuto(null);
			}
		} catch (Exception ex) {
			Util.stackTraceAndMessage("PAINEL OBJETO", ex, this);
		}

		toolbar.buscaAuto.habilitar(tabela.getModel().getRowCount() > 0 && buscaAuto);
		tabelaListener.tabelaMouseClick(tabela, -1);
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted() && toolbar.atualizar.itemAtualizarAuto.isDisplayable()) {
			try {
				Thread.sleep(Preferencias.getIntervaloPesquisaAuto());
				contadorAuto++;
				toolbar.atualizar.itemAtualizarAuto
						.setText(Mensagens.getString(Constantes.LABEL_ATUALIZAR_AUTO) + " " + contadorAuto);
				actionPerformed(null);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		toolbar.atualizar.itemAtualizarAuto.setText(Mensagens.getString(Constantes.LABEL_ATUALIZAR_AUTO));
		contadorAuto = 0;
		thread = null;
	}

	private transient FragmentoListener fragmentoListener = new FragmentoListener() {
		@Override
		public void configFragmento(Fragmento f) {
			txtComplemento.setText(f.getValor());
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

		txtComplemento.setText("AND " + campo + " IN (" + argumentos + ")");
		ObjetoContainer.this.actionPerformed(null);
	}

	public void atualizarFormulario() {
		Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

		if (conexao == null) {
			return;
		}

		ObjetoContainer.this.actionPerformed(null);
	}

	private transient TabelaListener tabelaListener = new TabelaListener() {
		@Override
		public void copiarNomeColuna(Tabela tabela, String nome) {
			txtComplemento.setText("AND " + nome + " = ");
		}

		@Override
		public void tabelaMouseClick(Tabela tabela, int colunaClick) {
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

				if (colunaClick >= 0 && linhas != null && linhas.length == 1 && !listaLink.isEmpty()) {
					int indiceLinkSelecionado = -1;

					for (int i = 0; i < listaLink.size(); i++) {
						Link link = listaLink.get(i);

						if (TabelaUtil.getIndiceColuna(tabela, link.getCampo()) == colunaClick) {
							indiceLinkSelecionado = i;
						}
					}

					if (indiceLinkSelecionado == -1) {
						return;
					}

					List<String> lista = TabelaUtil.getValoresColuna(tabela, colunaClick);

					if (lista.size() != 1) {
						return;
					}

					listener.linkAutomatico(listaLink.get(indiceLinkSelecionado), lista.get(0));
				}
			} else {
				toolbar.excluirAtualizarEnable(false);
			}
		}
	};

	public Component getSuporte() {
		return suporte;
	}

	public void setSuporte(Component suporte) {
		this.suporte = suporte;
	}
}