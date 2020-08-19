package br.com.persist.objeto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Window;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.KeyStroke;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import br.com.persist.busca_apos.BuscaAutoApos;
import br.com.persist.busca_apos.GrupoBuscaAutoApos;
import br.com.persist.busca_auto.BuscaAuto;
import br.com.persist.busca_auto.GrupoBuscaAuto;
import br.com.persist.busca_auto.TabelaBuscaAuto;
import br.com.persist.chave_valor.ChaveValor;
import br.com.persist.complemento.ComplementoDialogo;
import br.com.persist.complemento.ComplementoListener;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Button;
import br.com.persist.componente.Label;
import br.com.persist.componente.Menu;
import br.com.persist.componente.MenuItem;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextField;
import br.com.persist.conexao.Conexao;
import br.com.persist.conexao.ConexaoProvedor;
import br.com.persist.consulta.ConsultaDialogo;
import br.com.persist.consulta.ConsultaFormulario;
import br.com.persist.fragmento.Fragmento;
import br.com.persist.fragmento.FragmentoDialogo;
import br.com.persist.fragmento.FragmentoListener;
import br.com.persist.icone.Icones;
import br.com.persist.instrucao.Instrucao;
import br.com.persist.link_auto.GrupoLinkAuto;
import br.com.persist.link_auto.LinkAuto;
import br.com.persist.modelo.ListagemModelo;
import br.com.persist.persistencia.Persistencia;
import br.com.persist.renderer.CellRenderer;
import br.com.persist.tabela.CabecalhoColuna;
import br.com.persist.tabela.Coluna;
import br.com.persist.tabela.OrdenacaoModelo;
import br.com.persist.tabela.RegistroModelo;
import br.com.persist.tabela.Tabela;
import br.com.persist.tabela.TabelaListener;
import br.com.persist.tabela.TabelaUtil;
import br.com.persist.update.UpdateDialogo;
import br.com.persist.update.UpdateFormulario;
import br.com.persist.util.Acao;
import br.com.persist.util.Action;
import br.com.persist.util.ButtonPopup;
import br.com.persist.util.ConfigArquivo;
import br.com.persist.util.Constantes;
import br.com.persist.util.IIni;
import br.com.persist.util.IJanela;
import br.com.persist.util.IndiceValor;
import br.com.persist.util.Mensagens;
import br.com.persist.util.MenuPadrao2;
import br.com.persist.util.MenuPadrao3;
import br.com.persist.util.Preferencias;
import br.com.persist.util.Transferidor;
import br.com.persist.util.TransferidorDados;
import br.com.persist.util.Util;
import br.com.persist.variaveis.VariaveisDialogo;
import br.com.persist.variaveis.VariaveisModelo;

public class ObjetoContainer extends Panel implements ActionListener, ItemListener, Runnable, IIni {
	private static final long serialVersionUID = 1L;
	private final transient ActionListenerInner actionListenerInner = new ActionListenerInner();
	private transient ObjetoContainerListener.ConfigAlturaAutomatica configAlturaAutomaticaListener;
	private transient ObjetoContainerListener.BuscaAutomaticaApos buscaAutomaticaAposListener;
	private transient ObjetoContainerListener.BuscaAutomatica buscaAutomaticaListener;
	private transient ObjetoContainerListener.LinkAutomatico linkAutomaticoListener;
	private final Button btnArrasto = new Button(Action.actionIconDestacar());
	private transient ObjetoContainerListener.Componente componenteListener;
	private transient ObjetoContainerListener.Dimensao dimensaoListener;
	private transient ObjetoContainerListener.Selecao selecaoListener;
	private transient ObjetoContainerListener.Apelido apelidoListener;
	private transient ObjetoContainerListener.Titulo tituloListener;
	private final AtomicBoolean processado = new AtomicBoolean();
	private final TextField txtComplemento = new TextField(33);
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
	private boolean destacarTitulo;
	private Component suporte;
	private int contadorAuto;

	public ObjetoContainer(IJanela janela, ConexaoProvedor provedor, Conexao padrao, Objeto objeto, Graphics g,
			boolean buscaAuto) {
		tabela.setMapaChaveamento(Util.criarMapaCampoNomes(objeto.getChaveamento()));
		listaLink = LinkAuto.listaGrupoLinkAuto(objeto, objeto.getLinkAutomatico());
		objeto.setMapaSequencias(Util.criarMapaSequencias(objeto.getSequencias()));
		tabela.setMapeamento(Util.criarMapaCampoChave(objeto.getMapeamento()));
		txtComplemento.addMouseListener(mouseComplementoListener);
		txtComplemento.addActionListener(actionListenerInner);
		cmbConexao = Util.criarComboConexao(provedor, padrao);
		txtComplemento.setText(objeto.getComplemento());
		tabela.setTabelaListener(tabelaListener);
		cmbConexao.addItemListener(this);
		toolbar.ini(janela, objeto);
		this.buscaAuto = buscaAuto;
		this.provedor = provedor;
		this.objeto = objeto;
		montarLayout();
		configurar();
		processarObjeto(Constantes.VAZIO, g, null);
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(tabela));
	}

	private void configurar() {
		DragSource dragSource = DragSource.getDefaultDragSource();

		dragSource.createDefaultDragGestureRecognizer(btnArrasto, DnDConstants.ACTION_COPY, dge -> {
			Conexao conexao = (Conexao) cmbConexao.getSelectedItem();
			Dimension dimension = null;
			String apelido = null;

			if (apelidoListener != null) {
				apelido = apelidoListener.getApelido();
			}

			if (dimensaoListener != null) {
				dimension = dimensaoListener.getDimensoes();
			}

			if (dimension == null) {
				dimension = Constantes.SIZE;
			}

			dge.startDrag(null, new Transferidor(objeto, conexao, dimension, apelido), listenerArrasto);
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
		private final ButtonComplemento complemento = new ButtonComplemento();
		private final Button excluir = new Button(new ExcluirRegistrosAcao());
		private final ButtonAtualizar atualizar = new ButtonAtualizar();
		private final ButtonBuscaAuto buscaAuto = new ButtonBuscaAuto();
		private final ButtonFuncoes funcoes = new ButtonFuncoes();
		private final ButtonBaixar baixar = new ButtonBaixar();
		private final ButtonUpdate update = new ButtonUpdate();
		private final Label labelTotal = new Label(Color.BLUE);
		private final ButtonUtil util = new ButtonUtil();

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

		private void excluirAtualizarEnable(boolean b) {
			excluir.setEnabled(b);
			update.setEnabled(b);
		}

		private class ButtonUtil extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action fragmentoAcao = Action.actionMenu(Constantes.LABEL_FRAGMENTO, Icones.FRAGMENTO);
			private Action variaveisAcao = Action.actionMenu(Constantes.LABEL_VARIAVEIS, Icones.VAR);

			private ButtonUtil() {
				super("label.util", Icones.FRAGMENTO);

				addMenuItem(fragmentoAcao);
				addMenuItem(true, variaveisAcao);

				eventos();
			}

			private void eventos() {
				fragmentoAcao.setActionListener(e -> {
					FragmentoDialogo form = FragmentoDialogo.criar((Frame) null, null, fragmentoListener);
					configLocationRelativeTo(form);
					form.setVisible(true);
				});

				variaveisAcao.setActionListener(e -> {
					VariaveisDialogo form = VariaveisDialogo.criar((Frame) null, null);
					configLocationRelativeTo(form);
					form.setVisible(true);
				});
			}
		}

		private class ButtonBaixar extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action limpar2Acao = Action.actionMenu(Constantes.LABEL_LIMPAR2, Icones.NOVO);
			private Action limparAcao = Action.actionMenu(Constantes.LABEL_LIMPAR, Icones.NOVO);
			private Action conexaoAcao = Action.actionMenu(Constantes.LABEL_CONEXAO2, null);
			private Action objetoAcao = Action.actionMenu(Constantes.LABEL_OBJETO, null);

			private ButtonBaixar() {
				super("label.baixar", Icones.BAIXAR);

				addMenuItem(conexaoAcao);
				addMenuItem(true, objetoAcao);
				addMenuItem(true, limparAcao);
				addMenuItem(limpar2Acao);

				eventos();
			}

			private void eventos() {
				objetoAcao.setActionListener(e -> txtComplemento.setText(objeto.getComplemento()));
				limparAcao.setActionListener(e -> txtComplemento.limpar());

				conexaoAcao.setActionListener(e -> {
					Conexao conexao = (Conexao) cmbConexao.getSelectedItem();
					String string = Constantes.VAZIO;

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
					actionListenerInner.actionPerformed(null);
				});
			}
		}

		private class ExcluirRegistrosAcao extends Acao {
			private static final long serialVersionUID = 1L;

			private ExcluirRegistrosAcao() {
				super(false, "label.excluir_registro", Icones.EXCLUIR);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				int[] linhas = tabela.getSelectedRows();

				if (linhas != null && linhas.length > 0 && Util.confirmaExclusao(ObjetoContainer.this, false)) {
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

		private class ButtonComplemento extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action copiarAcao = Action.actionMenu("label.copiar_complemento", null);
			private Action concatAcao = Action.actionMenu("label.baixar_concatenado", null);
			private Action normalAcao = Action.actionMenu("label.baixar_normal", null);

			private ButtonComplemento() {
				super("label.complemento", Icones.BAIXAR2);

				addMenuItem(normalAcao);
				addMenuItem(true, concatAcao);
				addMenuItem(true, copiarAcao);

				copiarAcao.setActionListener(e -> copiarComplemento());
				concatAcao.setActionListener(e -> processar(false));
				normalAcao.setActionListener(e -> processar(true));
			}

			private void copiarComplemento() {
				String string = txtComplemento.getText().trim();
				Util.setContentTransfered(string);
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

					actionListenerInner.actionPerformed(null);
				}
			}
		}

		private class ButtonAtualizar extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action sincronizarAcao = Action.actionMenu(Constantes.LABEL_SINCRONIZAR, Icones.SINCRONIZAR);
			private MenuItem itemAtualizarAuto = new MenuItem(Constantes.LABEL_ATUALIZAR_AUTO, Icones.ATUALIZAR);
			private Action atualizarAcao = Action.actionMenuAtualizar();

			private ButtonAtualizar() {
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

				atualizarAcao.setActionListener(e -> actionListenerInner.actionPerformed(null));

				sincronizarAcao.setActionListener(e -> {
					CabecalhoColuna temp = cabecalhoFiltro;
					processado.set(true);

					cabecalhoFiltro = null;
					actionListenerInner.actionPerformed(null);

					if (!processado.get()) {
						cabecalhoFiltro = temp;
					}
				});
			}
		}

		private class ButtonBuscaAuto extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private boolean habilitado;

			private ButtonBuscaAuto() {
				super("label.buscaAuto", Icones.FIELDS);
			}

			private void complemento(Objeto objeto) {
				List<GrupoBuscaAutoApos> listaGrupoApos = BuscaAutoApos
						.listaGrupoBuscaAutoApos(objeto.getBuscaAutomaticaApos());
				List<GrupoBuscaAuto> listaGrupo = BuscaAuto.listaGrupoBuscaAuto(objeto, objeto.getBuscaAutomatica());

				for (GrupoBuscaAuto grupo : listaGrupo) {
					GrupoBuscaAutoApos grupoApos = TabelaUtil.proximo(listaGrupoApos, grupo);
					addMenu(new MenuBuscaAuto(grupo, grupoApos));
				}

				habilitado = !listaGrupo.isEmpty();
				setEnabled(habilitado);
			}

			private void habilitar(boolean b) {
				setEnabled(habilitado && b);
			}

			private class MenuBuscaAuto extends MenuPadrao2 {
				private static final long serialVersionUID = 1L;
				private final transient GrupoBuscaAutoApos grupoApos;
				private final transient GrupoBuscaAuto grupo;

				private MenuBuscaAuto(GrupoBuscaAuto grupo, GrupoBuscaAutoApos grupoApos) {
					super(grupo.getNomeGrupoCampo(), Icones.CONFIG2, "nao_chave");

					this.grupoApos = grupoApos;
					this.grupo = grupo;

					semAspasAcao.setActionListener(e -> processar(false));
					comAspasAcao.setActionListener(e -> processar(true));
				}

				private void processar(boolean apostrofes) {
					if (buscaAutomaticaListener == null) {
						return;
					}

					int coluna = TabelaUtil.getIndiceColuna(tabela, grupo.getCampo());

					if (coluna == -1) {
						return;
					}

					List<String> lista = TabelaUtil.getValoresColuna(tabela, coluna);

					if (lista.isEmpty()) {
						Util.mensagem(ObjetoContainer.this, grupo.getCampo() + " vazio.");
						return;
					}

					grupo.setProcessado(false);
					grupo.setNumeroColetores(lista);
					buscaAutomaticaListener.buscaAutomatica(grupo, Util.getStringLista(lista, apostrofes, false));
					setEnabled(grupo.isProcessado());

					if (grupo.isProcessado() && grupoApos != null && buscaAutomaticaAposListener != null) {
						buscaAutomaticaAposListener.buscaAutomaticaApos(grupoApos);
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

		private class ButtonUpdate extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action dadosAcao = Action.actionMenu("label.dados", Icones.TABELA);

			private ButtonUpdate() {
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

			private class MenuUpdate extends MenuPadrao3 {
				private static final long serialVersionUID = 1L;

				private MenuUpdate() {
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

			private class MenuDelete extends MenuPadrao3 {
				private static final long serialVersionUID = 1L;

				private MenuDelete() {
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

			private class MenuInsert extends MenuPadrao3 {
				private static final long serialVersionUID = 1L;

				private MenuInsert() {
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

			private void complemento(Objeto objeto) {
				if (objeto == null || objeto.getInstrucoes().isEmpty()) {
					return;
				}

				objeto.ordenarInstrucoes();

				for (Instrucao i : objeto.getInstrucoes()) {
					if (!Util.estaVazio(i.getValor())) {
						addMenu(true, new MenuInstrucao(i));
					}
				}
			}

			private class MenuInstrucao extends MenuPadrao3 {
				private static final long serialVersionUID = 1L;
				private final transient Instrucao instrucao;

				private MenuInstrucao(Instrucao instrucao) {
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
						ConsultaFormulario form = ConsultaFormulario.criar(null, instrucao.getNome(), provedor, conexao,
								instrucao.getValor(), chaves, false);
						configLocationRelativeTo(form);
						form.setVisible(true);
					} else {
						ConsultaDialogo form = ConsultaDialogo.criar((Frame) null, null, provedor, conexao,
								instrucao.getValor(), chaves, false);
						form.setTitle(instrucao.getNome());
						configLocationRelativeTo(form);
						form.setVisible(true);
					}
				}

				private void abrirUpdate(boolean abrirEmForm, Conexao conexao, Map<String, String> chaves) {
					if (abrirEmForm) {
						UpdateFormulario form = UpdateFormulario.criar(null, instrucao.getNome(), provedor, conexao,
								instrucao.getValor(), chaves);
						configLocationRelativeTo(form);
						form.setVisible(true);
					} else {
						UpdateDialogo form = UpdateDialogo.criar((Frame) null, null, instrucao.getNome(), provedor,
								conexao, instrucao.getValor(), chaves);
						configLocationRelativeTo(form);
						form.setVisible(true);
					}
				}
			}
		}

		private class ButtonFuncoes extends ButtonPopup {
			private static final long serialVersionUID = 1L;

			private ButtonFuncoes() {
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

			private class MinimoMaximoAcao extends Action {
				private static final long serialVersionUID = 1L;
				private final boolean minimo;

				private MinimoMaximoAcao(boolean minimo) {
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

					actionListenerInner.actionPerformed(null);
				}
			}

			private class TotalizarRegistrosAcao extends Action {
				private static final long serialVersionUID = 1L;
				private final boolean complemento;

				private TotalizarRegistrosAcao(boolean complemento) {
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
								complemento ? txtComplemento.getText() : Constantes.VAZIO, conexao);
						toolbar.labelTotal.setText(Constantes.VAZIO + i);
					} catch (Exception ex) {
						Util.stackTraceAndMessage("TOTAL", ex, ObjetoContainer.this);
					}
				}
			}
		}

		private void abrir(boolean abrirEmForm, Conexao conexao, String instrucao) {
			if (abrirEmForm) {
				UpdateFormulario form = UpdateFormulario.criar(null, Mensagens.getString(Constantes.LABEL_ATUALIZAR),
						provedor, conexao, instrucao);
				configLocationRelativeTo(form);
				form.setVisible(true);
			} else {
				UpdateDialogo form = UpdateDialogo.criar((Frame) null, null, provedor, conexao, instrucao);
				configLocationRelativeTo(form);
				form.setVisible(true);
			}
		}

		private class ButtonInfo extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action apelidoAcao = Action.actionMenu("label.apelido", Icones.TAG2);

			private ButtonInfo() {
				super("label.meta_dados", Icones.INFO);

				addMenuItem(apelidoAcao);
				addMenuItem(true, new ChavesPrimariasAcao());
				addMenuItem(true, new ChavesExportadasAcao());
				addMenuItem(new ChavesImportadasAcao());
				addMenuItem(true, new MetaDadosAcao());
				addMenuItem(true, new InfoBancoAcao());
				addMenuItem(new EsquemaAcao());
				addMenu(true, new MenuDML());
				addMenu(true, new MenuCopiar());

				eventos();
			}

			private void eventos() {
				apelidoAcao.setActionListener(e -> {
					if (apelidoListener != null) {
						String apelido = apelidoListener.selecionarApelido();

						if (apelido != null) {
							apelidoListener.setApelido(apelido);
						}
					}
				});
			}

			private class MenuCopiar extends Menu {
				private static final long serialVersionUID = 1L;
				private Action transfAcao = Action.actionMenu("label.transferidor", null);
				private Action tabularAcao = Action.actionMenu("label.tabular", null);
				private Action htmlAcao = Action.actionMenu("label.html", null);

				private MenuCopiar() {
					super("label.copiar", Icones.COPIA);

					addMenuItem(htmlAcao);
					addSeparator();
					addMenuItem(tabularAcao);
					addSeparator();
					addMenuItem(transfAcao);

					transfAcao.setActionListener(e -> processar(0));
					tabularAcao.setActionListener(e -> processar(1));
					htmlAcao.setActionListener(e -> processar(2));
				}

				private void processar(int tipo) {
					List<Integer> indices = TabelaUtil.getIndices(tabela);
					TransferidorDados transferidor = TabelaUtil.getTransferidorDados(tabela, indices);

					if (transferidor != null) {
						if (tipo == 0) {
							Util.setTransfered(transferidor);
						} else if (tipo == 1) {
							Util.setContentTransfered(transferidor.getTabular());
						} else if (tipo == 2) {
							Util.setContentTransfered(transferidor.getHtml());
						}
					}
				}
			}

			private class MenuDML extends Menu {
				private static final long serialVersionUID = 1L;

				private MenuDML() {
					super("label.dml", Icones.EXECUTAR);
					add(false, new MenuInsert());
					add(true, new MenuUpdate());
					add(true, new MenuDelete());
					add(true, new MenuSelect());
					add(true, new MenuSelectColuna());
				}

				private class MenuInsert extends MenuPadrao3 {
					private static final long serialVersionUID = 1L;

					private MenuInsert() {
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

				private class MenuUpdate extends MenuPadrao3 {
					private static final long serialVersionUID = 1L;

					private MenuUpdate() {
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

				private class MenuDelete extends MenuPadrao3 {
					private static final long serialVersionUID = 1L;

					private MenuDelete() {
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

				private class MenuSelect extends MenuPadrao3 {
					private static final long serialVersionUID = 1L;

					private MenuSelect() {
						super("label.select", Icones.TABELA);

						formularioAcao.setActionListener(e -> abrirSelect(true));
						dialogoAcao.setActionListener(e -> abrirSelect(false));
					}

					private void abrirSelect(boolean abrirEmForm) {
						Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

						if (conexao == null) {
							return;
						}

						String instrucao = getConsulta(conexao, Constantes.VAZIO).toString();

						if (Util.estaVazio(instrucao)) {
							return;
						}

						abrir(abrirEmForm, conexao, instrucao);
					}

					private void abrir(boolean abrirEmForm, Conexao conexao, String instrucao) {
						if (abrirEmForm) {
							ConsultaFormulario form = ConsultaFormulario.criar(null,
									Mensagens.getString(Constantes.LABEL_CONSULTA), provedor, conexao, instrucao, null,
									false);
							configLocationRelativeTo(form);
							form.setVisible(true);
						} else {
							ConsultaDialogo form = ConsultaDialogo.criar((Frame) null, null, provedor, conexao,
									instrucao, null, false);
							configLocationRelativeTo(form);
							form.setVisible(true);
						}
					}
				}

				private class MenuSelectColuna extends MenuPadrao3 {
					private static final long serialVersionUID = 1L;

					private MenuSelectColuna() {
						super("label.select_colunas", Icones.TABELA);

						formularioAcao.setActionListener(e -> abrirSelect(true));
						dialogoAcao.setActionListener(e -> abrirSelect(false));
					}

					private void abrirSelect(boolean abrirEmForm) {
						Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

						if (conexao == null) {
							return;
						}

						String instrucao = getConsultaColuna(conexao, Constantes.VAZIO).toString();

						if (Util.estaVazio(instrucao)) {
							return;
						}

						abrir(abrirEmForm, conexao, instrucao);
					}

					private StringBuilder getConsultaColuna(Conexao conexao, String complemento) {
						String selectAlter = objeto.getSelectAlternativo();
						objeto.setSelectAlternativo("SELECT " + tabela.getNomeColunas());
						StringBuilder builder = new StringBuilder();
						objeto.select(builder, conexao);
						objeto.where(builder);
						builder.append(" " + txtComplemento.getText());
						builder.append(" " + complemento);
						builder.append(" " + objeto.getFinalConsulta());
						objeto.setSelectAlternativo(selectAlter);

						return builder;
					}

					private void abrir(boolean abrirEmForm, Conexao conexao, String instrucao) {
						if (abrirEmForm) {
							ConsultaFormulario form = ConsultaFormulario.criar(null,
									Mensagens.getString(Constantes.LABEL_CONSULTA), provedor, conexao, instrucao, null,
									false);
							configLocationRelativeTo(form);
							form.setVisible(true);
						} else {
							ConsultaDialogo form = ConsultaDialogo.criar((Frame) null, null, provedor, conexao,
									instrucao, null, false);
							configLocationRelativeTo(form);
							form.setVisible(true);
						}
					}
				}
			}

			private class ChavesPrimariasAcao extends Action {
				private static final long serialVersionUID = 1L;

				private ChavesPrimariasAcao() {
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

						if (tituloListener != null) {
							tituloListener.setTitulo(objeto.getTitle(modeloOrdenacao, "CHAVE-PRIMARIA"));
						}

						tabela.setModel(modeloOrdenacao);
						configCabecalhoColuna(modeloListagem);
						TabelaUtil.ajustar(tabela, ObjetoContainer.this.getGraphics());
					} catch (Exception ex) {
						Util.stackTraceAndMessage("CHAVE-PRIMARIA", ex, ObjetoContainer.this);
					}
				}
			}

			private class ChavesImportadasAcao extends Action {
				private static final long serialVersionUID = 1L;

				private ChavesImportadasAcao() {
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

						if (tituloListener != null) {
							tituloListener.setTitulo(objeto.getTitle(modeloOrdenacao, "CHAVES-IMPORTADAS"));
						}

						tabela.setModel(modeloOrdenacao);
						configCabecalhoColuna(modeloListagem);
						TabelaUtil.ajustar(tabela, ObjetoContainer.this.getGraphics());
					} catch (Exception ex) {
						Util.stackTraceAndMessage("CHAVES-IMPORTADAS", ex, ObjetoContainer.this);
					}
				}
			}

			private class ChavesExportadasAcao extends Action {
				private static final long serialVersionUID = 1L;

				private ChavesExportadasAcao() {
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

						if (tituloListener != null) {
							tituloListener.setTitulo(objeto.getTitle(modeloOrdenacao, "CHAVES-EXPORTADAS"));
						}

						tabela.setModel(modeloOrdenacao);
						configCabecalhoColuna(modeloListagem);
						TabelaUtil.ajustar(tabela, ObjetoContainer.this.getGraphics());
					} catch (Exception ex) {
						Util.stackTraceAndMessage("CHAVES-EXPORTADAS", ex, ObjetoContainer.this);
					}
				}
			}

			private class InfoBancoAcao extends Action {
				private static final long serialVersionUID = 1L;

				private InfoBancoAcao() {
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

						if (tituloListener != null) {
							tituloListener.setTitulo(objeto.getTitle(modeloOrdenacao, "INFO-BANCO"));
						}

						tabela.setModel(modeloOrdenacao);
						configCabecalhoColuna(modeloListagem);
						TabelaUtil.ajustar(tabela, ObjetoContainer.this.getGraphics());
					} catch (Exception ex) {
						Util.stackTraceAndMessage("INFO-BANCO", ex, ObjetoContainer.this);
					}
				}
			}

			private class MetaDadosAcao extends Action {
				private static final long serialVersionUID = 1L;

				private MetaDadosAcao() {
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

						if (tituloListener != null) {
							tituloListener.setTitulo(
									objeto.getTitle(modeloOrdenacao, Mensagens.getString(Constantes.LABEL_METADADOS)));
						}

						tabela.setModel(modeloOrdenacao);
						configCabecalhoColuna(modeloListagem);
						TabelaUtil.ajustar(tabela, ObjetoContainer.this.getGraphics());
					} catch (Exception ex) {
						Util.stackTraceAndMessage("META-DADOS", ex, ObjetoContainer.this);
					}
				}
			}

			private class EsquemaAcao extends Action {
				private static final long serialVersionUID = 1L;

				private EsquemaAcao() {
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

						if (tituloListener != null) {
							tituloListener.setTitulo(objeto.getTitle(modeloOrdenacao, "ESQUEMA"));
						}

						tabela.setModel(modeloOrdenacao);
						configCabecalhoColuna(modeloListagem);
						TabelaUtil.ajustar(tabela, ObjetoContainer.this.getGraphics());
					} catch (Exception ex) {
						Util.stackTraceAndMessage("ESQUEMA", ex, ObjetoContainer.this);
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

					CabecalhoColuna cabecalhoColuna = new CabecalhoColuna(ObjetoContainer.this, modeloOrdenacao, coluna,
							false);

					tableColumn.setHeaderRenderer(cabecalhoColuna);
				}

				toolbar.buscaAuto.habilitar(false);
				toolbar.excluirAtualizarEnable(false);
			}
		}
	}

	private Component getComponente() {
		Component resp = null;

		if (componenteListener != null && componenteListener.getComponente() != null) {
			resp = componenteListener.getComponente();

		} else if (suporte instanceof Component) {
			resp = suporte;
		}

		return resp;
	}

	private void configLocationRelativeTo(Window window) {
		Component componente = getComponente();

		if (componente != null) {
			window.setLocationRelativeTo(componente);
		}
	}

	private transient MouseListener mouseComplementoListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() >= Constantes.DOIS) {
				ComplementoDialogo form = ComplementoDialogo.criar((Dialog) null, objeto, txtComplemento,
						complementoListener);
				configLocationRelativeTo(form);
				form.setVisible(true);
			}
		}
	};

	private transient ComplementoListener complementoListener = new ComplementoListener() {
		@Override
		public void processarComplemento(String string) {
			txtComplemento.setText(string);
			actionListenerInner.actionPerformed(null);
		}

		@Override
		public void limparComplemento() {
			txtComplemento.setText(Constantes.VAZIO);
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
		StringBuilder builder = new StringBuilder();
		objeto.select(builder, conexao);
		objeto.joins(builder, conexao, objeto.getPrefixoNomeTabela());
		objeto.where(builder);
		builder.append(" " + txtComplemento.getText());
		builder.append(" " + complemento);
		builder.append(" " + objeto.getFinalConsulta());

		return builder;
	}

	private boolean continuar(String complemento) {
		if (!Util.estaVazio(txtComplemento.getText())) {
			return true;
		}

		if (!Util.estaVazio(complemento)) {
			return true;
		}

		if (!objeto.isCcsc()) {
			return true;
		}

		String msg = Mensagens.getString("msg.ccsc", objeto.getId() + " - " + objeto.getTabela2());
		return Util.confirmar(ObjetoContainer.this, msg, false);
	}

	public void processarObjeto(String complemento, Graphics g, CabecalhoColuna cabecalho) {
		Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

		if (conexao == null) {
			return;
		}

		if (!continuar(complemento)) {
			processado.set(false);
			return;
		}

		StringBuilder builder = getConsulta(conexao, complemento);

		try {
			Connection conn = Conexao.getConnection(conexao);
			RegistroModelo modeloRegistro = Persistencia.criarModeloRegistro(conn, builder.toString(),
					objeto.getChavesArray(), objeto, conexao);
			OrdenacaoModelo modeloOrdenacao = new OrdenacaoModelo(modeloRegistro);
			objeto.setComplemento(txtComplemento.getText());
			modeloRegistro.setConexao(conexao);
			tabela.setModel(modeloOrdenacao);
			threadTitulo(getTituloAtualizado());
			cabecalhoFiltro = null;
			atualizarTitulo();

			TableColumnModel columnModel = tabela.getColumnModel();
			List<Coluna> colunas = modeloRegistro.getColunas();

			for (int i = 0; i < colunas.size(); i++) {
				TableColumn tableColumn = columnModel.getColumn(i);
				Coluna coluna = colunas.get(i);
				configTableColumn(tableColumn, coluna);
				CabecalhoColuna cabecalhoColuna = new CabecalhoColuna(this, modeloOrdenacao, coluna,
						!coluna.isColunaInfo());

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
					TabelaUtil.checarColetores(tabela, coluna, tabelaBuscaAuto);
				}

				objeto.setTabelaBuscaAuto(null);
			}
		} catch (Exception ex) {
			mensagemException(ex);
		}

		toolbar.buscaAuto.habilitar(tabela.getModel().getRowCount() > 0 && buscaAuto);
		tabelaListener.tabelaMouseClick(tabela, -1);
		configAlturaAutomatica();
	}

	private void mensagemException(Exception ex) {
		if (Preferencias.isErroCriarConnection()) {
			if (!Preferencias.isExibiuMensagemConnection()) {
				Util.stackTraceAndMessage("PAINEL OBJETO: " + objeto.getId() + " -> " + objeto.getPrefixoNomeTabela()
						+ objeto.getTabela2(), ex, this);
				Preferencias.setExibiuMensagemConnection(true);
			}
		} else {
			Util.stackTraceAndMessage(
					"PAINEL OBJETO: " + objeto.getId() + " -> " + objeto.getPrefixoNomeTabela() + objeto.getTabela2(),
					ex, this);
		}
	}

	private void configTableColumn(TableColumn tableColumn, Coluna coluna) {
		if (coluna.isChave()) {
			tableColumn.setCellRenderer(new CellRenderer());
		}

		if (coluna.isColunaInfo()) {
			tableColumn.setCellRenderer(new CellInfoRenderer());
		}
	}

	private void configAlturaAutomatica() {
		if (objeto.isAjusteAutoForm() && tamanhoAutomatico && configAlturaAutomaticaListener != null) {
			configAlturaAutomaticaListener.configAlturaAutomatica(tabela.getModel().getRowCount());
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
				actionListenerInner.processar();
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
			actionListenerInner.actionPerformed(null);
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

		private void processar() {
			tamanhoAutomatico = true;
			ObjetoContainer.this.actionPerformed(null);
			tamanhoAutomatico = false;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		processarObjeto(cabecalhoFiltro == null ? Constantes.VAZIO : cabecalhoFiltro.getFiltroComplemento(), null,
				cabecalhoFiltro);
	}

	public void buscaAutomatica(String campo, String argumentos) {
		Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

		if (conexao == null) {
			return;
		}

		txtComplemento.setText("AND " + campo + " IN (" + argumentos + ")");
		destacarTitulo = true;
		actionListenerInner.processar();
	}

	public void aplicarConfigArquivo(ConfigArquivo config) {
		Conexao conexaoSel = null;

		if (!Util.estaVazio(config.getConexao())) {
			for (int i = 0; i < cmbConexao.getItemCount(); i++) {
				Conexao c = cmbConexao.getItemAt(i);

				if (config.getConexao().equalsIgnoreCase(c.getNome())) {
					conexaoSel = c;
					break;
				}
			}

			if (conexaoSel != null) {
				cmbConexao.setSelectedItem(conexaoSel);
			}
		}

		txtComplemento.setText(config.getComplemento());
		destacarTitulo = true;
		actionListenerInner.processar();
		TabelaUtil.ajustar(tabela, config.getGraphics());
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

	public String getTituloAtualizado() {
		OrdenacaoModelo modelo = (OrdenacaoModelo) tabela.getModel();
		return objeto.getTitle(modelo);
	}

	public void atualizarTitulo() {
		if (tituloListener != null) {
			String titulo = getTituloAtualizado();
			tituloListener.setTitulo(titulo);
		}
	}

	private String getComplementoChavesAux(Map<String, String> map) {
		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
		StringBuilder sb = new StringBuilder("(");

		if (it.hasNext()) {
			Entry<String, String> entry = it.next();
			sb.append(entry.getKey() + "=" + entry.getValue());
		}

		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			sb.append(" AND " + entry.getKey() + "=" + entry.getValue());
		}

		sb.append(")");

		return sb.toString();
	}

	private String[] getComplementoChave(Map<String, String> map) {
		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
		String[] array = new String[2];

		if (it.hasNext()) {
			Entry<String, String> entry = it.next();
			array[0] = entry.getKey();
			array[1] = entry.getValue();
		}

		return array;
	}

	public String getComplementoChaves() {
		StringBuilder sb = new StringBuilder();

		OrdenacaoModelo modelo = (OrdenacaoModelo) tabela.getModel();
		TableModel model = modelo.getModel();

		if (model instanceof RegistroModelo) {
			List<Integer> indices = TabelaUtil.getIndicesColuna(tabela);

			if (!indices.isEmpty()) {
				Map<String, String> chaves = modelo.getMapaChaves(indices.get(0));

				if (chaves.size() > 1) {
					sb.append("AND (");
					sb.append(getComplementoChavesAux(chaves));

					for (int i = 1; i < indices.size(); i++) {
						sb.append(" OR ");
						chaves = modelo.getMapaChaves(indices.get(i));
						sb.append(getComplementoChavesAux(chaves));
					}
					sb.append(")");
				} else if (chaves.size() == 1) {
					String[] array = getComplementoChave(chaves);
					String chave = array[0];

					sb.append("AND " + chave + " IN(" + array[1]);

					for (int i = 1; i < indices.size(); i++) {
						sb.append(", ");
						chaves = modelo.getMapaChaves(indices.get(i));
						sb.append(chaves.get(chave));
					}
					sb.append(")");
				}
			}
		}

		return sb.toString();
	}

	public void atualizarFormulario() {
		Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

		if (conexao == null) {
			return;
		}

		actionListenerInner.processar();
	}

	public void limpar() {
		toolbar.baixar.limpar2Acao.actionPerformed(null);
	}

	private transient TabelaListener tabelaListener = new TabelaListener() {
		@Override
		public void copiarNomeColuna(Tabela tabela, String nome, String anterior) {
			String string = Util.estaVazio(anterior) ? Constantes.VAZIO : anterior;
			txtComplemento.setText("AND " + nome + " = " + string);

			if (!Util.estaVazio(anterior) && Preferencias.isExecAposCopiarConcatenado()) {
				actionListenerInner.actionPerformed(null);
			}
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
					toolbar.labelTotal.setText(Constantes.VAZIO + linhas.length);
				} else {
					toolbar.excluirAtualizarEnable(false);
					toolbar.labelTotal.limpar();
				}

				if (colunaClick >= 0 && linhas != null && linhas.length == 1 && !listaLink.isEmpty()
						&& linkAutomaticoListener != null) {
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

					linkAutomaticoListener.linkAutomatico(listaLink.get(indiceLinkSelecionado), lista.get(0));
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

	private void threadTitulo(String titulo) {
		if (tituloListener == null || !destacarTitulo) {
			return;
		}

		new Thread(new DestaqueTitulo(titulo)).start();
	}

	private class DestaqueTitulo implements Runnable {
		private final String original;
		private int contador;

		public DestaqueTitulo(String original) {
			this.original = original;
		}

		@Override
		public void run() {
			while (destacarTitulo && contador < Constantes.DEZ && !Thread.currentThread().isInterrupted()) {
				try {
					destacarTitulo(original);
					Thread.sleep(300);
					contador++;
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}

			if (tituloListener != null) {
				tituloListener.setTitulo(original);
			}

			if (selecaoListener != null) {
				selecaoListener.selecionar(false);
			}

			destacarTitulo = false;
		}

		private String esq = "<<<<<<";
		private String dir = ">>>>>>";
		private int indice = esq.length() - 1;

		private void destacarTitulo(String titulo) {
			if (indice < 0) {
				indice = esq.length() - 1;
			}

			if (tituloListener != null) {
				tituloListener.setTitulo(esq.substring(indice) + titulo + dir.substring(indice));
			}

			if (selecaoListener != null) {
				selecaoListener.selecionar(indice % 2 == 0);
			}

			indice--;
		}
	}

	public ObjetoContainerListener.BuscaAutomatica getBuscaAutomaticaListener() {
		return buscaAutomaticaListener;
	}

	public void setBuscaAutomaticaListener(ObjetoContainerListener.BuscaAutomatica buscaAutomaticaListener) {
		this.buscaAutomaticaListener = buscaAutomaticaListener;
	}

	public ObjetoContainerListener.LinkAutomatico getLinkAutomaticoListener() {
		return linkAutomaticoListener;
	}

	public void setLinkAutomaticoListener(ObjetoContainerListener.LinkAutomatico linkAutomaticoListener) {
		this.linkAutomaticoListener = linkAutomaticoListener;
	}

	public ObjetoContainerListener.BuscaAutomaticaApos getBuscaAutomaticaAposListener() {
		return buscaAutomaticaAposListener;
	}

	public void setBuscaAutomaticaAposListener(
			ObjetoContainerListener.BuscaAutomaticaApos buscaAutomaticaAposListener) {
		this.buscaAutomaticaAposListener = buscaAutomaticaAposListener;
	}

	public ObjetoContainerListener.ConfigAlturaAutomatica getConfigAlturaAutomaticaListener() {
		return configAlturaAutomaticaListener;
	}

	public void setConfigAlturaAutomaticaListener(
			ObjetoContainerListener.ConfigAlturaAutomatica configAlturaAutomaticaListener) {
		this.configAlturaAutomaticaListener = configAlturaAutomaticaListener;
	}

	public ObjetoContainerListener.Titulo getTituloListener() {
		return tituloListener;
	}

	public void setTituloListener(ObjetoContainerListener.Titulo tituloListener) {
		this.tituloListener = tituloListener;

		if (tituloListener != null) {
			atualizarTitulo();
		}
	}

	public ObjetoContainerListener.Selecao getSelecaoListener() {
		return selecaoListener;
	}

	public void setSelecaoListener(ObjetoContainerListener.Selecao selecaoListener) {
		this.selecaoListener = selecaoListener;
	}

	public ObjetoContainerListener.Dimensao getDimensaoListener() {
		return dimensaoListener;
	}

	public void setDimensaoListener(ObjetoContainerListener.Dimensao dimensaoListener) {
		this.dimensaoListener = dimensaoListener;
	}

	public ObjetoContainerListener.Apelido getApelidoListener() {
		return apelidoListener;
	}

	public void setApelidoListener(ObjetoContainerListener.Apelido apelidoListener) {
		this.apelidoListener = apelidoListener;
	}

	public ObjetoContainerListener.Componente getComponenteListener() {
		return componenteListener;
	}

	public void setComponenteListener(ObjetoContainerListener.Componente componenteListener) {
		this.componenteListener = componenteListener;
	}
}