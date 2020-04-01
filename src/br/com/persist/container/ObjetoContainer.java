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
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.KeyStroke;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import br.com.persist.Instrucao;
import br.com.persist.banco.Conexao;
import br.com.persist.banco.ConexaoProvedor;
import br.com.persist.banco.Persistencia;
import br.com.persist.busca_apos.BuscaAutoApos;
import br.com.persist.busca_apos.GrupoBuscaAutoApos;
import br.com.persist.busca_auto.BuscaAuto;
import br.com.persist.busca_auto.GrupoBuscaAuto;
import br.com.persist.busca_auto.TabelaBuscaAuto;
import br.com.persist.comp.BarraButton;
import br.com.persist.comp.Button;
import br.com.persist.comp.Label;
import br.com.persist.comp.Menu;
import br.com.persist.comp.MenuItem;
import br.com.persist.comp.Panel;
import br.com.persist.comp.ScrollPane;
import br.com.persist.comp.TextField;
import br.com.persist.desktop.Objeto;
import br.com.persist.dialogo.ComplementoDialogo;
import br.com.persist.dialogo.ConsultaDialogo;
import br.com.persist.dialogo.FragmentoDialogo;
import br.com.persist.dialogo.UpdateDialogo;
import br.com.persist.dialogo.VariaveisDialogo;
import br.com.persist.formulario.ConsultaFormulario;
import br.com.persist.formulario.ObjetoContainerFormularioInterno;
import br.com.persist.formulario.UpdateFormulario;
import br.com.persist.link_auto.GrupoLinkAuto;
import br.com.persist.link_auto.LinkAuto;
import br.com.persist.listener.FragmentoListener;
import br.com.persist.listener.ObjetoContainerListener;
import br.com.persist.listener.TabelaListener;
import br.com.persist.modelo.ListagemModelo;
import br.com.persist.modelo.OrdenacaoModelo;
import br.com.persist.modelo.RegistroModelo;
import br.com.persist.modelo.VariaveisModelo;
import br.com.persist.renderer.CellInfoRenderer;
import br.com.persist.renderer.CellRenderer;
import br.com.persist.tabela.CabecalhoColuna;
import br.com.persist.tabela.Coluna;
import br.com.persist.tabela.IndiceValor;
import br.com.persist.tabela.Tabela;
import br.com.persist.tabela.TabelaUtil;
import br.com.persist.util.Acao;
import br.com.persist.util.Action;
import br.com.persist.util.ButtonPopup;
import br.com.persist.util.ChaveValor;
import br.com.persist.util.Constantes;
import br.com.persist.util.Fragmento;
import br.com.persist.util.IIni;
import br.com.persist.util.IJanela;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;
import br.com.persist.util.MenuPadrao2;
import br.com.persist.util.MenuPadrao3;
import br.com.persist.util.Preferencias;
import br.com.persist.util.Transferidor;
import br.com.persist.util.Util;

public class ObjetoContainer extends Panel implements ActionListener, ItemListener, Runnable, IIni {
	private static final long serialVersionUID = 1L;
	private final Button btnArrasto = new Button(Action.actionIconDestacar());
	private final AtomicBoolean processado = new AtomicBoolean();
	private final TextField txtComplemento = new TextField(35);
	private final transient ObjetoContainerListener listener;
	private final transient List<GrupoLinkAuto> listaLink;
	private static final Logger LOG = Logger.getGlobal();
	private final transient ConexaoProvedor provedor;
	private final Toolbar toolbar = new Toolbar();
	private final JComboBox<Conexao> cmbConexao;
	private final Tabela tabela = new Tabela();
	private CabecalhoColuna cabecalhoFiltro;
	private final transient Objeto objeto;
	private boolean tamanhoAutomatico;
	private final boolean buscaAuto;
	private transient Thread thread;
	private Component suporte;
	private int contadorAuto;

	public ObjetoContainer(IJanela janela, ConexaoProvedor provedor, Conexao padrao, Objeto objeto,
			ObjetoContainerListener listener, Graphics g, boolean buscaAuto) {
		tabela.setMapaChaveamento(Util.criarMapaCampoNomes(objeto.getChaveamento()));
		objeto.setMapaSequencias(Util.criarMapaSequencias(objeto.getSequencias()));
		tabela.setMapeamento(Util.criarMapaCampoChave(objeto.getMapeamento()));
		listaLink = LinkAuto.listaGrupoLinkAuto(objeto.getLinkAutomatico());
		txtComplemento.addActionListener(new ActionListenerInner());
		cmbConexao = Util.criarComboConexao(provedor, padrao);
		txtComplemento.addMouseListener(complementoListener);
		txtComplemento.setText(objeto.getComplemento());
		tabela.setTabelaListener(tabelaListener);
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

		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), Constantes.EXEC);
		getActionMap().put(Constantes.EXEC, toolbar.atualizar.atualizarAcao);
	}

	@Override
	public void ini(Graphics graphics) {
		TabelaUtil.ajustar(tabela, graphics);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		final ButtonComplemento complemento = new ButtonComplemento();
		final Button excluir = new Button(new ExcluirRegistrosAcao());
		final ButtonAtualizar atualizar = new ButtonAtualizar();
		final ButtonBuscaAuto buscaAuto = new ButtonBuscaAuto();
		final ButtonFuncoes funcoes = new ButtonFuncoes();
		final ButtonBaixar baixar = new ButtonBaixar();
		final ButtonUpdate update = new ButtonUpdate();
		final Label labelTotal = new Label(Color.BLUE);
		final ButtonUtil util = new ButtonUtil();

		protected void ini(IJanela janela, Objeto objeto) {
			super.ini(janela, false, false);

			add(btnArrasto);
			add(true, new ButtonInfo());
			add(true, excluir);
			add(true, util);
			add(buscaAuto);
			add(true, update);
			add(atualizar);
			add(true, complemento);
			add(txtComplemento);
			add(labelTotal);
			add(baixar);
			add(funcoes);
			add(true, cmbConexao);

			buscaAuto.complemento(objeto);
			update.complemento(objeto);
		}

		void excluirAtualizarEnable(boolean b) {
			excluir.setEnabled(b);
			update.setEnabled(b);
		}

		class ButtonUtil extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action fragmentoAcao = Action.actionMenu(Constantes.LABEL_FRAGMENTO, Icones.FRAGMENTO);
			private Action variaveisAcao = Action.actionMenu(Constantes.LABEL_VARIAVEIS, Icones.VAR);

			ButtonUtil() {
				super("label.util", Icones.FRAGMENTO);

				addMenuItem(fragmentoAcao);
				addMenuItem(true, variaveisAcao);

				eventos();
			}

			private void eventos() {
				fragmentoAcao.setActionListener(e -> {
					FragmentoDialogo form = new FragmentoDialogo((Frame) null, null, fragmentoListener);

					if (listener instanceof Component) {
						form.setLocationRelativeTo((Component) listener);
					} else if (suporte instanceof Component) {
						form.setLocationRelativeTo(suporte);
					}

					form.setVisible(true);
				});

				variaveisAcao.setActionListener(e -> {
					VariaveisDialogo form = new VariaveisDialogo((Frame) null, null);

					if (listener instanceof Component) {
						form.setLocationRelativeTo((Component) listener);
					} else if (suporte instanceof Component) {
						form.setLocationRelativeTo(suporte);
					}

					form.setVisible(true);
				});
			}
		}

		class ButtonBaixar extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action limpar2Acao = Action.actionMenu(Constantes.LABEL_LIMPAR2, Icones.NOVO);
			private Action limparAcao = Action.actionMenu(Constantes.LABEL_LIMPAR, Icones.NOVO);
			private Action conexaoAcao = Action.actionMenu(Constantes.LABEL_CONEXAO2, null);
			private Action objetoAcao = Action.actionMenu(Constantes.LABEL_OBJETO, null);

			ButtonBaixar() {
				super("label.baixar", Icones.BAIXAR);

				addMenuItem(conexaoAcao);
				addMenuItem(true, objetoAcao);
				addMenuItem(true, limparAcao);
				addMenuItem(limpar2Acao);

				eventos();
			}

			private void eventos() {
				objetoAcao.setActionListener(e -> txtComplemento.setText(objeto.getComplemento()));
				limparAcao.setActionListener(e -> txtComplemento.setText(""));

				conexaoAcao.setActionListener(e -> {
					Conexao conexao = (Conexao) cmbConexao.getSelectedItem();
					String string = "";

					if (conexao != null) {
						string = conexao.getFinalComplemento();
					}

					txtComplemento.setText(string);
				});

				limpar2Acao.setActionListener(e -> {
					boolean salvar = false;

					ChaveValor cv = VariaveisModelo.get("LIMPAR2");

					if (cv == null) {
						cv = new ChaveValor("LIMPAR2", "AND 1 > 2");
						VariaveisModelo.adicionar(cv);
						salvar = true;
					}

					if (salvar) {
						VariaveisModelo.salvar();
						VariaveisModelo.inicializar();
					}

					txtComplemento.setText(cv.getValor());

					if (objeto.isAjusteAutoEnter()) {
						tamanhoAutomatico = true;
					}

					ObjetoContainer.this.actionPerformed(null);

					if (objeto.isAjusteAutoEnter()) {
						tamanhoAutomatico = false;
					}
				});
			}
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
						int excluido = modelo.excluirRegistro(linha, objeto.getPrefixoNomeTabela());

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

		class ButtonComplemento extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action concatAcao = Action.actionMenu("label.concatenado", null);
			private Action normalAcao = Action.actionMenu("label.normal", null);

			ButtonComplemento() {
				super("label.complemento", Icones.BAIXAR2);

				addMenuItem(concatAcao);
				addMenuItem(true, normalAcao);

				concatAcao.setActionListener(e -> processar(false));
				normalAcao.setActionListener(e -> processar(true));
			}

			private void processar(boolean normal) {
				Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

				if (conexao == null) {
					return;
				}

				String complement = Util.getContentTransfered();

				if (Util.estaVazio(complement)) {
					txtComplemento.setText(objeto.getComplemento());

				} else {
					if (normal) {
						txtComplemento.setText(complement);
					} else {
						String s = txtComplemento.getText().trim();
						txtComplemento.setText(s + " " + complement);
					}

					if (objeto.isAjusteAutoEnter()) {
						tamanhoAutomatico = true;
					}

					ObjetoContainer.this.actionPerformed(null);

					if (objeto.isAjusteAutoEnter()) {
						tamanhoAutomatico = false;
					}
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

				atualizarAcao.setActionListener(e -> {
					if (objeto.isAjusteAutoEnter()) {
						tamanhoAutomatico = true;
					}

					ObjetoContainer.this.actionPerformed(null);

					if (objeto.isAjusteAutoEnter()) {
						tamanhoAutomatico = false;
					}
				});

				sincronizarAcao.setActionListener(e -> {
					CabecalhoColuna temp = cabecalhoFiltro;
					processado.set(true);

					cabecalhoFiltro = null;

					if (objeto.isAjusteAutoEnter()) {
						tamanhoAutomatico = true;
					}

					ObjetoContainer.this.actionPerformed(null);

					if (objeto.isAjusteAutoEnter()) {
						tamanhoAutomatico = false;
					}

					if (!processado.get()) {
						cabecalhoFiltro = temp;
					}
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
				List<GrupoBuscaAutoApos> listaGrupoApos = BuscaAutoApos
						.listaGrupoBuscaAutoApos(objeto.getBuscaAutomaticaApos());
				List<GrupoBuscaAuto> listaGrupo = BuscaAuto.listaGrupoBuscaAuto(objeto.getBuscaAutomatica());

				for (GrupoBuscaAuto grupo : listaGrupo) {
					GrupoBuscaAutoApos grupoApos = TabelaUtil.proximo(listaGrupoApos, grupo);
					addMenu(new MenuBuscaAuto(grupo, grupoApos));
				}

				habilitado = !listaGrupo.isEmpty();
				setEnabled(habilitado);
			}

			void habilitar(boolean b) {
				setEnabled(habilitado && b);
			}

			class MenuBuscaAuto extends MenuPadrao2 {
				private static final long serialVersionUID = 1L;
				private final transient GrupoBuscaAutoApos grupoApos;
				private final transient GrupoBuscaAuto grupo;

				MenuBuscaAuto(GrupoBuscaAuto grupo, GrupoBuscaAutoApos grupoApos) {
					super(grupo.getNomeGrupoCampo(), Icones.CONFIG2, "nao_chave");

					this.grupoApos = grupoApos;
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
					grupo.setNumeroColetores(lista);
					listener.buscaAutomatica(grupo, Util.getStringLista(lista, apostrofes, false));
					setEnabled(grupo.isProcessado());

					if (grupo.isProcessado() && grupoApos != null) {
						listener.buscaAutomaticaApos(grupoApos);
					}

					if (!objeto.isColunaInfo()) {
						return;
					}

					List<Integer> indices = TabelaUtil.getIndicesColuna(tabela);

					for (int linha : indices) {
						TabelaUtil.atualizarLinhaColetores(tabela, linha, coluna, grupo);
					}

					TabelaUtil.ajustar(tabela, ObjetoContainer.this.getGraphics());
				}
			}
		}

		class ButtonUpdate extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action dadosAcao = Action.actionMenu("label.dados", Icones.TABELA);

			ButtonUpdate() {
				super(Constantes.LABEL_UPDATE, Icones.UPDATE);

				addMenuItem(dadosAcao);
				addMenu(true, new MenuUpdate());
				addMenu(true, new MenuDelete());
				addMenu(true, new MenuInsert());

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
					super(Constantes.LABEL_UPDATE, Icones.UPDATE);

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

							String instrucao = modelo.getUpdate(linhas[0], objeto.getPrefixoNomeTabela());

							if (Util.estaVazio(instrucao)) {
								return;
							}

							abrir(abrirEmForm, conexao, instrucao);
						}
					}
				}
			}

			class MenuDelete extends MenuPadrao3 {
				private static final long serialVersionUID = 1L;

				MenuDelete() {
					super(Constantes.LABEL_DELETE, Icones.EXCLUIR);

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

							String instrucao = modelo.getDelete(linhas[0], objeto.getPrefixoNomeTabela());

							if (Util.estaVazio(instrucao)) {
								return;
							}

							abrir(abrirEmForm, conexao, instrucao);
						}
					}
				}
			}

			class MenuInsert extends MenuPadrao3 {
				private static final long serialVersionUID = 1L;

				MenuInsert() {
					super(Constantes.LABEL_INSERT, Icones.CRIAR);

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
							String instrucao = modelo.getInsert(linhas[0], objeto.getPrefixoNomeTabela());

							if (Util.estaVazio(instrucao)) {
								return;
							}

							abrir(abrirEmForm, conexao, instrucao);
						}
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
						ConsultaFormulario form = new ConsultaFormulario(null, instrucao.getNome(), provedor, conexao,
								instrucao.getValor(), chaves, false);
						if (listener instanceof Component) {
							form.setLocationRelativeTo((Component) listener);
						}
						form.setVisible(true);
					} else {
						ConsultaDialogo form = new ConsultaDialogo((Frame) null, null, provedor, conexao,
								instrucao.getValor(), chaves, false);
						form.setTitle(instrucao.getNome());
						if (listener instanceof Component) {
							form.setLocationRelativeTo((Component) listener);
						}
						form.setVisible(true);
					}
				}

				private void abrirUpdate(boolean abrirEmForm, Conexao conexao, Map<String, String> chaves) {
					if (abrirEmForm) {
						UpdateFormulario form = new UpdateFormulario(null, instrucao.getNome(), provedor, conexao,
								instrucao.getValor(), chaves);
						if (listener instanceof Component) {
							form.setLocationRelativeTo((Component) listener);
						}
						form.setVisible(true);
					} else {
						UpdateDialogo form = new UpdateDialogo((Frame) null, null, instrucao.getNome(), provedor,
								conexao, instrucao.getValor(), chaves);
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
						txtComplemento.limpar();
						return;
					}

					if (minimo) {
						txtComplemento.setText("AND " + chaves[0] + " = (SELECT MIN(" + chaves[0] + ") FROM "
								+ objeto.getTabelaEsquema(conexao.getEsquema()) + ")");
					} else {
						txtComplemento.setText("AND " + chaves[0] + " = (SELECT MAX(" + chaves[0] + ") FROM "
								+ objeto.getTabelaEsquema(conexao.getEsquema()) + ")");
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

		private void abrir(boolean abrirEmForm, Conexao conexao, String instrucao) {
			if (abrirEmForm) {
				UpdateFormulario form = new UpdateFormulario(null, Mensagens.getString(Constantes.LABEL_ATUALIZAR),
						provedor, conexao, instrucao);
				if (listener instanceof Component) {
					form.setLocationRelativeTo((Component) listener);
				}
				form.setVisible(true);
			} else {
				UpdateDialogo form = new UpdateDialogo((Frame) null, null, provedor, conexao, instrucao);
				if (listener instanceof Component) {
					form.setLocationRelativeTo((Component) listener);
				}
				form.setVisible(true);
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
				addMenu(true, new MenuDML());

				eventos();
			}

			private void eventos() {
				apelidoAcao.setActionListener(e -> {
					if (listener instanceof ObjetoContainerFormularioInterno) {
						ObjetoContainerFormularioInterno interno = (ObjetoContainerFormularioInterno) listener;
						Object resp = Util.getValorInputDialog(ObjetoContainer.this, "label.apelido",
								interno.getApelido(), interno.getApelido());

						if (resp == null) {
							return;
						}

						interno.setApelido(resp.toString().trim());
					}
				});
			}

			class MenuDML extends Menu {
				private static final long serialVersionUID = 1L;

				public MenuDML() {
					super("label.dml", Icones.EXECUTAR);
					add(false, new MenuInsert());
					add(true, new MenuUpdate());
					add(true, new MenuDelete());
					add(true, new MenuSelect());
				}

				class MenuInsert extends MenuPadrao3 {
					private static final long serialVersionUID = 1L;

					MenuInsert() {
						super(Constantes.LABEL_INSERT, Icones.CRIAR);

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
							String instrucao = modelo.getInsert(objeto.getPrefixoNomeTabela());

							if (Util.estaVazio(instrucao)) {
								return;
							}

							abrir(abrirEmForm, conexao, instrucao);
						}
					}
				}

				class MenuUpdate extends MenuPadrao3 {
					private static final long serialVersionUID = 1L;

					MenuUpdate() {
						super(Constantes.LABEL_UPDATE, Icones.UPDATE);

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
							String instrucao = modelo.getUpdate(objeto.getPrefixoNomeTabela());

							if (Util.estaVazio(instrucao)) {
								return;
							}

							abrir(abrirEmForm, conexao, instrucao);
						}
					}
				}

				class MenuDelete extends MenuPadrao3 {
					private static final long serialVersionUID = 1L;

					MenuDelete() {
						super(Constantes.LABEL_DELETE, Icones.EXCLUIR);

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
							String instrucao = modelo.getDelete(objeto.getPrefixoNomeTabela());

							if (Util.estaVazio(instrucao)) {
								return;
							}

							abrir(abrirEmForm, conexao, instrucao);
						}
					}
				}

				class MenuSelect extends MenuPadrao3 {
					private static final long serialVersionUID = 1L;

					MenuSelect() {
						super("label.select", Icones.TABELA);

						formularioAcao.setActionListener(e -> abrirSelect(true));
						dialogoAcao.setActionListener(e -> abrirSelect(false));
					}

					private void abrirSelect(boolean abrirEmForm) {
						Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

						if (conexao == null) {
							return;
						}

						String instrucao = getConsulta(conexao, "").toString();

						if (Util.estaVazio(instrucao)) {
							return;
						}

						abrir(abrirEmForm, conexao, instrucao);
					}

					private void abrir(boolean abrirEmForm, Conexao conexao, String instrucao) {
						if (abrirEmForm) {
							ConsultaFormulario form = new ConsultaFormulario(null,
									Mensagens.getString(Constantes.LABEL_CONSULTA), provedor, conexao, instrucao, null,
									false);
							if (listener instanceof Component) {
								form.setLocationRelativeTo((Component) listener);
							}
							form.setVisible(true);
						} else {
							ConsultaDialogo form = new ConsultaDialogo((Frame) null, null, provedor, conexao, instrucao,
									null, false);
							if (listener instanceof Component) {
								form.setLocationRelativeTo((Component) listener);
							}
							form.setVisible(true);
						}
					}
				}
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
						listener.setTitulo(
								objeto.getTitle(modeloOrdenacao, Mensagens.getString(Constantes.LABEL_METADADOS)));

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
		if (ItemEvent.SELECTED != e.getStateChange()) {
			return;
		}

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

	private StringBuilder getConsulta(Conexao conexao, String complemento) {
		StringBuilder builder = new StringBuilder(
				"SELECT * FROM " + objeto.getTabelaEsquema(conexao.getEsquema()) + " WHERE 1=1");
		builder.append(" " + txtComplemento.getText());
		builder.append(" " + complemento);
		builder.append(" " + objeto.getFinalConsulta());

		return builder;
	}

	private boolean continuar(String complemento, String chaveMsg) {
		if (!Util.estaVazio(txtComplemento.getText())) {
			return true;
		}

		if (!Util.estaVazio(complemento)) {
			return true;
		}

		if (!objeto.isCcsc()) {
			return true;
		}

		return Util.confirmar(ObjetoContainer.this, chaveMsg);
	}

	public void processarObjeto(String complemento, Graphics g, CabecalhoColuna cabecalho) {
		Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

		if (conexao == null) {
			return;
		}

		if (!continuar(complemento, "msg.ccsc")) {
			processado.set(false);
			return;
		}

		StringBuilder builder = getConsulta(conexao, complemento);

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

			TabelaBuscaAuto tabelaBuscaAuto = objeto.getTabelaBuscaAuto();

			if (tabelaBuscaAuto != null) {
				int coluna = TabelaUtil.getIndiceColuna(tabela, tabelaBuscaAuto.getCampo());

				if (coluna != -1) {
					TabelaUtil.contabilizarColetores(tabela, coluna, tabelaBuscaAuto);
				}

				objeto.setTabelaBuscaAuto(null);
			}
		} catch (Exception ex) {
			Util.stackTraceAndMessage("PAINEL OBJETO: " + objeto.getId() + " -> " + objeto.getTabela2(), ex, this);
		}

		toolbar.buscaAuto.habilitar(tabela.getModel().getRowCount() > 0 && buscaAuto);
		tabelaListener.tabelaMouseClick(tabela, -1);
		configAlturaAutomatica();
	}

	private void configAlturaAutomatica() {
		if (objeto.isAjusteAutoForm() && tamanhoAutomatico) {
			listener.configAlturaAutomatica(tabela.getModel().getRowCount());
		}
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted() && toolbar.atualizar.itemAtualizarAuto.isDisplayable()) {
			try {
				Thread.sleep(Preferencias.getIntervaloPesquisaAuto());
				contadorAuto++;
				toolbar.atualizar.itemAtualizarAuto
						.setText(Mensagens.getString(Constantes.LABEL_ATUALIZAR_AUTO) + " " + contadorAuto);
				tamanhoAutomatico = true;
				actionPerformed(null);
				tamanhoAutomatico = false;
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

			if (objeto.isAjusteAutoEnter()) {
				tamanhoAutomatico = true;
			}

			actionPerformed(null);

			if (objeto.isAjusteAutoEnter()) {
				tamanhoAutomatico = false;
			}
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

	private class ActionListenerInner implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (objeto.isAjusteAutoEnter()) {
				tamanhoAutomatico = true;
			}

			ObjetoContainer.this.actionPerformed(null);

			if (objeto.isAjusteAutoEnter()) {
				tamanhoAutomatico = false;
			}
		}
	}

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
		tamanhoAutomatico = true;
		ObjetoContainer.this.actionPerformed(null);
		tamanhoAutomatico = false;
	}

	public void buscaAutomaticaApos() {
		toolbar.baixar.limpar2Acao.actionPerformed(null);
	}

	public void linkAutomatico(String campo, String argumento) {
		if (!objeto.isLinkAuto() || argumento == null) {
			return;
		}

		OrdenacaoModelo modelo = (OrdenacaoModelo) tabela.getModel();
		TableModel model = modelo.getModel();
		tabela.clearSelection();

		if (model instanceof RegistroModelo) {
			int coluna = TabelaUtil.getIndiceColuna(tabela, campo);

			if (coluna == -1) {
				return;
			}

			for (int i = 0; i < modelo.getRowCount(); i++) {
				if (argumento.equals(modelo.getValueAt(i, coluna))) {
					tabela.addRowSelectionInterval(i, i);
				}
			}
		}
	}

	public void atualizarFormulario() {
		Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

		if (conexao == null) {
			return;
		}

		tamanhoAutomatico = true;
		ObjetoContainer.this.actionPerformed(null);
		tamanhoAutomatico = false;
	}

	private transient TabelaListener tabelaListener = new TabelaListener() {
		@Override
		public void copiarNomeColuna(Tabela tabela, String nome, String anterior) {
			String string = Util.estaVazio(anterior) ? "" : anterior;
			txtComplemento.setText("AND " + nome + " = " + string);
		}

		public void concatenarNomeColuna(Tabela tabela, String nome) {
			String complemento = txtComplemento.getText();
			txtComplemento.setText(complemento + " AND " + nome + " = ");
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
					toolbar.labelTotal.setText("" + linhas.length);
				} else {
					toolbar.excluirAtualizarEnable(false);
					toolbar.labelTotal.setText("");
				}

				if (colunaClick >= 0 && linhas != null && linhas.length == 1 && !listaLink.isEmpty()) {
					int indiceLinkSelecionado = -1;

					for (int i = 0; i < listaLink.size(); i++) {
						GrupoLinkAuto link = listaLink.get(i);

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

	public void selecionarConexao(Conexao conexao) {
		if (conexao != null) {
			cmbConexao.setSelectedItem(conexao);
		}
	}
}