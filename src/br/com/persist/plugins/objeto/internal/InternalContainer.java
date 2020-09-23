package br.com.persist.plugins.objeto.internal;

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
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.abstrato.DesktopAlinhamento;
import br.com.persist.assistencia.CellRenderer;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.TransferidorTabular;
import br.com.persist.assistencia.Util;
import br.com.persist.complemento.ComplementoDialogo;
import br.com.persist.complemento.ComplementoListener;
import br.com.persist.componente.Acao;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Button;
import br.com.persist.componente.ButtonPopup;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Label;
import br.com.persist.componente.Menu;
import br.com.persist.componente.MenuItem;
import br.com.persist.componente.MenuPadrao2;
import br.com.persist.componente.MenuPadrao3;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextField;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.conexao.ConexaoProvedor;
import br.com.persist.plugins.consulta.ConsultaDialogo;
import br.com.persist.plugins.consulta.ConsultaFormulario;
import br.com.persist.plugins.fragmento.Fragmento;
import br.com.persist.plugins.fragmento.FragmentoDialogo;
import br.com.persist.plugins.fragmento.FragmentoListener;
import br.com.persist.plugins.objeto.Instrucao;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.ObjetoUtil;
import br.com.persist.plugins.objeto.auto.BuscaAutoUtil;
import br.com.persist.plugins.objeto.auto.GrupoBuscaAuto;
import br.com.persist.plugins.objeto.auto.GrupoLinkAuto;
import br.com.persist.plugins.objeto.auto.LinkAutoUtil;
import br.com.persist.plugins.objeto.auto.TabelaBuscaAuto;
import br.com.persist.plugins.persistencia.Coluna;
import br.com.persist.plugins.persistencia.IndiceValor;
import br.com.persist.plugins.persistencia.MemoriaModelo;
import br.com.persist.plugins.persistencia.OrdenacaoModelo;
import br.com.persist.plugins.persistencia.Persistencia;
import br.com.persist.plugins.persistencia.PersistenciaModelo;
import br.com.persist.plugins.persistencia.PersistenciaModelo.Parametros;
import br.com.persist.plugins.persistencia.tabela.CabecalhoColuna;
import br.com.persist.plugins.persistencia.tabela.CabecalhoColunaListener;
import br.com.persist.plugins.persistencia.tabela.TabelaDialogo;
import br.com.persist.plugins.persistencia.tabela.TabelaPersistencia;
import br.com.persist.plugins.persistencia.tabela.TabelaPersistenciaListener;
import br.com.persist.plugins.persistencia.tabela.TabelaPersistenciaUtil;
import br.com.persist.plugins.update.UpdateDialogo;
import br.com.persist.plugins.update.UpdateFormulario;
import br.com.persist.plugins.variaveis.Variavel;
import br.com.persist.plugins.variaveis.VariavelDialogo;
import br.com.persist.plugins.variaveis.VariavelProvedor;

public class InternalContainer extends Panel implements ActionListener, ItemListener, Pagina {
	private static final long serialVersionUID = 1L;
	private final transient ActionListenerInner actionListenerInner = new ActionListenerInner();
	private transient InternalListener.ConfigAlturaAutomatica configAlturaAutomaticaListener;
	private transient InternalListener.BuscaAutomaticaApos buscaAutomaticaAposListener;
	private final TabelaPersistencia tabelaPersistencia = new TabelaPersistencia();
	private transient InternalListener.BuscaAutomatica buscaAutomaticaListener;
	private final Button btnArrasto = new Button(Action.actionIconDestacar());
	private transient InternalListener.LinkAutomatico linkAutomaticoListener;
	private transient TabelaListener tabelaListener = new TabelaListener();
	private transient InternalListener.Visibilidade visibilidadeListener;
	private transient InternalListener.Alinhamento alinhamentoListener;
	private transient InternalListener.Componente componenteListener;
	private transient InternalListener.Dimensao dimensaoListener;
	private final AtomicBoolean processado = new AtomicBoolean();
	private transient InternalListener.Largura larguraListener;
	private transient InternalListener.Selecao selecaoListener;
	private transient InternalListener.Apelido apelidoListener;
	private final TextField txtComplemento = new TextField(33);
	private final transient List<GrupoLinkAuto> listaGrupoLink;
	private transient InternalListener.Titulo tituloListener;
	private static final Logger LOG = Logger.getGlobal();
	private final JComboBox<Conexao> comboConexao;
	private final Toolbar toolbar = new Toolbar();
	private CabecalhoColuna cabecalhoFiltro;
	private final transient Objeto objeto;
	private boolean tamanhoAutomatico;
	private final boolean buscaAuto;
	private boolean destacarTitulo;
	private Component suporte;
	private int contadorAuto;

	public InternalContainer(Janela janela, Conexao padrao, Objeto objeto, Graphics g, boolean buscaAuto) {
		tabelaPersistencia.setChaveamento(ObjetoUtil.criarMapaCampoNomes(objeto.getChaveamento()));
		tabelaPersistencia.setMapeamento(ObjetoUtil.criarMapaCampoChave(objeto.getMapeamento()));
		listaGrupoLink = LinkAutoUtil.listaGrupoLinkAuto(objeto, objeto.getLinkAutomatico());
		objeto.setMapaSequencias(ObjetoUtil.criarMapaSequencias(objeto.getSequencias()));
		tabelaPersistencia.setTabelaPersistenciaListener(tabelaListener);
		txtComplemento.addMouseListener(mouseComplementoListener);
		comboConexao = ConexaoProvedor.criarComboConexao(padrao);
		txtComplemento.addActionListener(actionListenerInner);
		txtComplemento.setText(objeto.getComplemento());
		comboConexao.addItemListener(this);
		toolbar.ini(janela, objeto);
		this.buscaAuto = buscaAuto;
		this.objeto = objeto;
		montarLayout();
		configurar();
		processarObjeto("", g, null);
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(tabelaPersistencia));
	}

	private void configurar() {
		DragSource dragSource = DragSource.getDefaultDragSource();

		dragSource.createDefaultDragGestureRecognizer(btnArrasto, DnDConstants.ACTION_COPY, dge -> {
			Conexao conexao = (Conexao) comboConexao.getSelectedItem();
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

			dge.startDrag(null, new InternalTransferidor(objeto, conexao, dimension, apelido), listenerArrasto);
		});

		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), Constantes.EXEC);
		getActionMap().put(Constantes.EXEC, toolbar.buttonSincronizar.atualizarAcao);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private final Button buttonExcluir = new Button(new ExcluirRegistrosAcao());
		private final ButtonSincronizar buttonSincronizar = new ButtonSincronizar();
		private final ButtonComplemento buttonComplemento = new ButtonComplemento();
		private final ButtonBuscaAuto buttonBuscaAuto = new ButtonBuscaAuto();
		private final ButtonFuncoes buttonFuncoes = new ButtonFuncoes();
		private final ButtonFragVar buttonFragVar = new ButtonFragVar();
		private final ButtonBaixar buttonBaixar = new ButtonBaixar();
		private final ButtonUpdate buttonUpdate = new ButtonUpdate();
		private final Label labelTotal = new Label(Color.BLUE);
		private final ButtonInfo buttonInfo = new ButtonInfo();
		private transient Thread thread;

		protected void ini(Janela janela, Objeto objeto) {
			super.ini(janela);
			add(btnArrasto);
			add(true, buttonInfo);
			add(true, buttonExcluir);
			add(true, buttonFragVar);
			add(buttonBuscaAuto);
			add(true, buttonUpdate);
			add(buttonSincronizar);
			add(true, buttonComplemento);
			add(txtComplemento);
			add(labelTotal);
			add(buttonBaixar);
			add(buttonFuncoes);
			add(true, comboConexao);
			buttonBuscaAuto.complemento(objeto);
			buttonUpdate.complemento(objeto);
		}

		private void excluirAtualizarEnable(boolean b) {
			buttonExcluir.setEnabled(b);
			buttonUpdate.setEnabled(b);
		}

		private class ButtonFragVar extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action fragmentoAcao = Action.actionMenu(Constantes.LABEL_FRAGMENTO, Icones.FRAGMENTO);
			private Action variaveisAcao = Action.actionMenu(Constantes.LABEL_VARIAVEIS, Icones.VAR);

			private ButtonFragVar() {
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
					VariavelDialogo form = VariavelDialogo.criar((Frame) null, null);
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
				conexaoAcao.setActionListener(e -> limparUsandoConexao());
				limpar2Acao.setActionListener(e -> limpar2());
			}

			private void limparUsandoConexao() {
				Conexao conexao = (Conexao) comboConexao.getSelectedItem();
				String string = Constantes.VAZIO;

				if (conexao != null) {
					string = conexao.getFinalComplemento();
				}

				txtComplemento.setText(string);
			}

			private void limpar2() {
				boolean salvar = false;

				Variavel cv = VariavelProvedor.getVariavel("LIMPAR2");

				if (cv == null) {
					cv = new Variavel("LIMPAR2", "AND 1 > 2");
					VariavelProvedor.adicionar(cv);
					salvar = true;
				}

				if (salvar) {
					VariavelProvedor.salvar();
					VariavelProvedor.inicializar();
				}

				txtComplemento.setText(cv.getValor());
				actionListenerInner.actionPerformed(null);
			}
		}

		private class ExcluirRegistrosAcao extends Acao {
			private static final long serialVersionUID = 1L;

			private ExcluirRegistrosAcao() {
				super(false, "label.excluir_registro", Icones.EXCLUIR);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				int[] linhas = tabelaPersistencia.getSelectedRows();

				if (linhas != null && linhas.length > 0 && Util.confirmaExclusao(InternalContainer.this, false)) {
					OrdenacaoModelo modelo = tabelaPersistencia.getModelo();

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
					tabelaListener.tabelaMouseClick(tabelaPersistencia, -1);
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
				Conexao conexao = (Conexao) comboConexao.getSelectedItem();

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

					if (Preferencias.isExecAposBaixarParaComplemento()) {
						actionListenerInner.actionPerformed(null);
					}
				}
			}
		}

		private class ButtonSincronizar extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action sincronizarAcao = Action.actionMenu(Constantes.LABEL_SINCRONIZAR, Icones.SINCRONIZAR);
			private MenuItem itemAtualizarAuto = new MenuItem(Constantes.LABEL_ATUALIZAR_AUTO, Icones.ATUALIZAR);
			private Action atualizarAcao = Action.actionMenuAtualizar();

			private ButtonSincronizar() {
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
							thread = new Thread(new Trabalho());
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

			private class Trabalho implements Runnable {
				private final String titulo = Mensagens.getString(Constantes.LABEL_ATUALIZAR_AUTO);

				@Override
				public void run() {
					while (!Thread.currentThread().isInterrupted() && itemAtualizarAuto.isDisplayable()) {
						try {
							Thread.sleep(Preferencias.getIntervaloPesquisaAuto());
							contadorAuto++;
							itemAtualizarAuto.setText(titulo + " " + contadorAuto);
							SwingUtilities.invokeLater(actionListenerInner::processar);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					}

					itemAtualizarAuto.setText(titulo);
					contadorAuto = 0;
					thread = null;
				}
			}
		}

		private class ButtonBuscaAuto extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private boolean habilitado;

			private ButtonBuscaAuto() {
				super("label.buscaAuto", Icones.FIELDS);
			}

			private void complemento(Objeto objeto) {
				List<GrupoBuscaAuto> listaGrupo = BuscaAutoUtil.listaGrupoBuscaAuto(objeto.getBuscaAutomatica());

				for (GrupoBuscaAuto grupo : listaGrupo) {
					listaGrupoLink.add(grupo.getGrupoLinkAuto());
					addMenu(new MenuBuscaAuto(grupo));
				}

				habilitado = !listaGrupo.isEmpty();
				setEnabled(habilitado);
			}

			private void habilitar(boolean b) {
				setEnabled(habilitado && b);
			}

			private class MenuBuscaAuto extends MenuPadrao2 {
				private static final long serialVersionUID = 1L;
				private final transient GrupoBuscaAuto grupo;

				private MenuBuscaAuto(GrupoBuscaAuto grupo) {
					super(grupo.getNome() + "." + grupo.getCampo(), Icones.CONFIG2, "nao_chave");
					this.grupo = grupo;
					semAspasAcao.setActionListener(e -> processar(false));
					comAspasAcao.setActionListener(e -> processar(true));
				}

				private void processar(boolean apostrofes) {
					if (buscaAutomaticaListener == null) {
						return;
					}

					int coluna = TabelaPersistenciaUtil.getIndiceColuna(tabelaPersistencia, grupo.getCampo());
					if (coluna == -1) {
						return;
					}

					List<String> lista = TabelaPersistenciaUtil.getValoresLinhaPelaColuna(tabelaPersistencia, coluna);
					if (lista.isEmpty()) {
						Util.mensagem(InternalContainer.this, grupo.getCampo() + " vazio.");
						return;
					}

					grupo.setProcessado(false);
					grupo.inicializarColetores(lista);
					buscaAutomaticaListener.buscaAutomatica(grupo, Util.getStringLista(lista, apostrofes, false));
					setEnabled(grupo.isProcessado());

					if (grupo.isProcessado() && buscaAutomaticaAposListener != null) {
						buscaAutomaticaAposListener.buscaAutomaticaApos(InternalContainer.this,
								grupo.getGrupoBuscaAutoApos());
					}

					processarColunaInfo(coluna);
				}

				private void processarColunaInfo(int coluna) {
					if (objeto.isColunaInfo()) {
						List<Integer> indices = Util.getIndicesLinha(tabelaPersistencia);
						for (int linha : indices) {
							InternalUtil.consolidarNoRegistroUsandoColetores(tabelaPersistencia, linha, coluna, grupo);
						}
						Util.ajustar(tabelaPersistencia, InternalContainer.this.getGraphics());
					}
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
					OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
					TableModel model = modelo.getModelo();

					if (model instanceof PersistenciaModelo) {
						int[] linhas = tabelaPersistencia.getSelectedRows();

						if (linhas != null && linhas.length == 1) {
							StringBuilder sb = new StringBuilder(objeto.getTabela2());
							sb.append(Constantes.QL);
							modelo.getDados(linhas[0], sb);
							Util.mensagem(InternalContainer.this, sb.toString());
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
					Conexao conexao = (Conexao) comboConexao.getSelectedItem();

					if (conexao == null) {
						return;
					}

					OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
					TableModel model = modelo.getModelo();

					if (model instanceof PersistenciaModelo) {
						int[] linhas = tabelaPersistencia.getSelectedRows();

						if (linhas != null && linhas.length == 1) {
							List<IndiceValor> chaves = modelo.getValoresChaves(linhas[0]);

							if (chaves.isEmpty()) {
								return;
							}

							String instrucao = modelo.getUpdate(linhas[0], objeto.getPrefixoNomeTabela());

							if (Util.estaVazio(instrucao)) {
								return;
							}

							updateFormDialog(abrirEmForm, conexao, instrucao, "Update");
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
					Conexao conexao = (Conexao) comboConexao.getSelectedItem();

					if (conexao == null) {
						return;
					}

					OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
					TableModel model = modelo.getModelo();

					if (model instanceof PersistenciaModelo) {
						int[] linhas = tabelaPersistencia.getSelectedRows();

						if (linhas != null && linhas.length == 1) {
							List<IndiceValor> chaves = modelo.getValoresChaves(linhas[0]);

							if (chaves.isEmpty()) {
								return;
							}

							String instrucao = modelo.getDelete(linhas[0], objeto.getPrefixoNomeTabela());

							if (Util.estaVazio(instrucao)) {
								return;
							}

							updateFormDialog(abrirEmForm, conexao, instrucao, "Delete");
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
					Conexao conexao = (Conexao) comboConexao.getSelectedItem();

					if (conexao == null) {
						return;
					}

					OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
					TableModel model = modelo.getModelo();

					if (model instanceof PersistenciaModelo) {
						int[] linhas = tabelaPersistencia.getSelectedRows();

						if (linhas != null && linhas.length == 1) {
							String instrucao = modelo.getInsert(linhas[0], objeto.getPrefixoNomeTabela());

							if (Util.estaVazio(instrucao)) {
								return;
							}

							updateFormDialog(abrirEmForm, conexao, instrucao, "Insert");
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
					Conexao conexao = (Conexao) comboConexao.getSelectedItem();

					if (conexao == null) {
						return;
					}

					OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
					TableModel model = modelo.getModelo();

					if (model instanceof PersistenciaModelo) {
						int[] linhas = tabelaPersistencia.getSelectedRows();

						if (linhas != null && linhas.length == 1) {
							Map<String, String> chaves = modelo.getMapaChaves(linhas[0]);

							if (chaves.isEmpty()) {
								return;
							}

							if (Util.estaVazio(instrucao.getValor())) {
								return;
							}

							String conteudo = ObjetoUtil.substituir(instrucao.getValor(), chaves);

							if (instrucao.isSelect()) {
								selectFormDialog(abrirEmForm, conexao, conteudo, instrucao.getNome());
							} else {
								updateFormDialog(abrirEmForm, conexao, conteudo, instrucao.getNome());
							}
						}
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
					Conexao conexao = (Conexao) comboConexao.getSelectedItem();

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
					super(true, complemento ? "label.total_com_filtro" : "label.total_sem_filtro", Icones.SOMA);
					this.complemento = complemento;
				}

				@Override
				public void actionPerformed(ActionEvent e) {
					Conexao conexao = (Conexao) comboConexao.getSelectedItem();

					if (conexao != null) {
						try {
							Connection conn = ConexaoProvedor.getConnection(conexao);
							String esquemaTabela = objeto.getTabelaEsquema(conexao.getEsquema());
							String complementar = complemento ? txtComplemento.getText() : Constantes.VAZIO;
							String aposFROM = esquemaTabela
									+ (!Util.estaVazio(complementar) ? " WHERE 1=1 " + complementar : Constantes.VAZIO);
							int i = Persistencia.getTotalRegistros(conn, aposFROM);
							toolbar.labelTotal.setText(Constantes.VAZIO + i);
						} catch (Exception ex) {
							Util.stackTraceAndMessage("TOTAL", ex, InternalContainer.this);
						}
					}
				}
			}
		}

		private class ButtonInfo extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action apelidoAcao = Action.actionMenu("label.apelido", Icones.TAG2);
			private MenuAlinhamento menuAlinhamento = new MenuAlinhamento();

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
				addMenu(true, menuAlinhamento);

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

			private class MenuAlinhamento extends Menu {
				private static final long serialVersionUID = 1L;
				private Action somenteDireitoAcao = Action.actionMenu("label.somente_direito", Icones.ALINHA_DIREITO);
				private Action mesmaLarguraAcao = Action.actionMenu("label.mesma_largura", Icones.LARGURA);
				private Action esquerdoAcao = Action.actionMenu("label.esquerdo", Icones.ALINHA_ESQUERDO);
				private Action direitoAcao = Action.actionMenu("label.direito", Icones.ALINHA_DIREITO);

				private MenuAlinhamento() {
					super("label.alinhamento", Icones.LARGURA);
					addMenuItem(direitoAcao);
					addMenuItem(esquerdoAcao);
					addMenuItem(mesmaLarguraAcao);
					addMenuItem(somenteDireitoAcao);

					somenteDireitoAcao.setActionListener(e -> alinhar(DesktopAlinhamento.COMPLETAR_DIREITO));
					esquerdoAcao.setActionListener(e -> alinhar(DesktopAlinhamento.ESQUERDO));
					direitoAcao.setActionListener(e -> alinhar(DesktopAlinhamento.DIREITO));
					mesmaLarguraAcao.setActionListener(e -> mesma());
				}

				void habilitar(boolean b) {
					somenteDireitoAcao.setEnabled(b);
					mesmaLarguraAcao.setEnabled(b);
					esquerdoAcao.setEnabled(b);
					direitoAcao.setEnabled(b);
					setEnabled(b);
				}

				private void alinhar(DesktopAlinhamento opcao) {
					if (alinhamentoListener != null) {
						alinhamentoListener.alinhar(opcao);
					}
				}

				private void mesma() {
					if (larguraListener != null) {
						larguraListener.mesma();
					}
				}
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
					List<Integer> indices = Util.getIndicesLinha(tabelaPersistencia);
					TransferidorTabular transferidor = Util.criarTransferidorTabular(tabelaPersistencia, indices);

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
						Conexao conexao = (Conexao) comboConexao.getSelectedItem();

						if (conexao == null) {
							return;
						}

						OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
						TableModel model = modelo.getModelo();

						if (model instanceof PersistenciaModelo) {
							String instrucao = modelo.getInsert(objeto.getPrefixoNomeTabela());

							if (Util.estaVazio(instrucao)) {
								return;
							}

							updateFormDialog(abrirEmForm, conexao, instrucao, "Insert");
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
						Conexao conexao = (Conexao) comboConexao.getSelectedItem();

						if (conexao == null) {
							return;
						}

						OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
						TableModel model = modelo.getModelo();

						if (model instanceof PersistenciaModelo) {
							String instrucao = modelo.getUpdate(objeto.getPrefixoNomeTabela());

							if (Util.estaVazio(instrucao)) {
								return;
							}

							updateFormDialog(abrirEmForm, conexao, instrucao, "Update");
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
						Conexao conexao = (Conexao) comboConexao.getSelectedItem();

						if (conexao == null) {
							return;
						}

						OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
						TableModel model = modelo.getModelo();

						if (model instanceof PersistenciaModelo) {
							String instrucao = modelo.getDelete(objeto.getPrefixoNomeTabela());

							if (Util.estaVazio(instrucao)) {
								return;
							}

							updateFormDialog(abrirEmForm, conexao, instrucao, "Delete");
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
						Conexao conexao = (Conexao) comboConexao.getSelectedItem();

						if (conexao == null) {
							return;
						}

						String instrucao = getConsulta(conexao, Constantes.VAZIO).toString();

						if (Util.estaVazio(instrucao)) {
							return;
						}

						selectFormDialog(abrirEmForm, conexao, instrucao, "Select");
					}
				}

				private class MenuSelectColuna extends MenuPadrao3 {
					private static final long serialVersionUID = 1L;

					private MenuSelectColuna() {
						super("label.select_colunas", Icones.TABELA);

						formularioAcao.setActionListener(e -> abrirSelect(true));
						dialogoAcao.setActionListener(e -> abrirSelect(false));
					}

					private StringBuilder getConsultaColuna(Conexao conexao, String complemento) {
						String selectAlter = objeto.getSelectAlternativo();
						objeto.setSelectAlternativo("SELECT " + tabelaPersistencia.getNomeColunas());
						StringBuilder builder = new StringBuilder();
						objeto.select(builder, conexao);
						objeto.where(builder);
						builder.append(" " + txtComplemento.getText());
						builder.append(" " + complemento);
						builder.append(" " + objeto.getFinalConsulta());
						objeto.setSelectAlternativo(selectAlter);

						return builder;
					}

					private void abrirSelect(boolean abrirEmForm) {
						Conexao conexao = (Conexao) comboConexao.getSelectedItem();

						if (conexao == null) {
							return;
						}

						String instrucao = getConsultaColuna(conexao, Constantes.VAZIO).toString();

						if (Util.estaVazio(instrucao)) {
							return;
						}

						selectFormDialog(abrirEmForm, conexao, instrucao, "Select");
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
					Conexao conexao = (Conexao) comboConexao.getSelectedItem();

					if (conexao != null) {
						try {
							Connection conn = ConexaoProvedor.getConnection(conexao);
							MemoriaModelo modelo = Persistencia.criarModeloInfoBanco(conn);
							TabelaDialogo.criar((Frame) null, "INFO-BANCO", modelo);
						} catch (Exception ex) {
							Util.stackTraceAndMessage("INFO-BANCO", ex, InternalContainer.this);
						}
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
					Conexao conexao = (Conexao) comboConexao.getSelectedItem();

					if (conexao != null) {
						try {
							Connection conn = ConexaoProvedor.getConnection(conexao);
							MemoriaModelo modelo = Persistencia.criarModeloEsquema(conn);
							TabelaDialogo.criar((Frame) null, "ESQUEMA", modelo);
						} catch (Exception ex) {
							Util.stackTraceAndMessage("ESQUEMA", ex, InternalContainer.this);
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
					Conexao conexao = (Conexao) comboConexao.getSelectedItem();

					if (conexao != null) {
						try {
							Connection conn = ConexaoProvedor.getConnection(conexao);
							MemoriaModelo modelo = Persistencia.criarModeloChavePrimaria(conn, conexao,
									objeto.getTabela2());
							TabelaDialogo.criar((Frame) null, objeto.getTitle("CHAVE-PRIMARIA"), modelo);
						} catch (Exception ex) {
							Util.stackTraceAndMessage("CHAVE-PRIMARIA", ex, InternalContainer.this);
						}
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
					Conexao conexao = (Conexao) comboConexao.getSelectedItem();

					if (conexao != null) {
						try {
							Connection conn = ConexaoProvedor.getConnection(conexao);
							MemoriaModelo modelo = Persistencia.criarModeloChavesImportadas(conn, conexao,
									objeto.getTabela2());
							TabelaDialogo.criar((Frame) null, objeto.getTitle("CHAVES-IMPORTADAS"), modelo);
						} catch (Exception ex) {
							Util.stackTraceAndMessage("CHAVES-IMPORTADAS", ex, InternalContainer.this);
						}
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
					Conexao conexao = (Conexao) comboConexao.getSelectedItem();

					if (conexao != null) {
						try {
							Connection conn = ConexaoProvedor.getConnection(conexao);
							MemoriaModelo modelo = Persistencia.criarModeloChavesExportadas(conn, conexao,
									objeto.getTabela2());
							TabelaDialogo.criar((Frame) null, objeto.getTitle("CHAVES-EXPORTADAS"), modelo);
						} catch (Exception ex) {
							Util.stackTraceAndMessage("CHAVES-EXPORTADAS", ex, InternalContainer.this);
						}
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
					Conexao conexao = (Conexao) comboConexao.getSelectedItem();

					if (conexao != null) {
						try {
							Connection conn = ConexaoProvedor.getConnection(conexao);
							MemoriaModelo modelo = Persistencia.criarModeloMetaDados(conn, conexao,
									objeto.getTabela2());
							TabelaDialogo.criar((Frame) null,
									objeto.getTitle(Mensagens.getString(Constantes.LABEL_METADADOS)), modelo);
						} catch (Exception ex) {
							Util.stackTraceAndMessage("META-DADOS", ex, InternalContainer.this);
						}
					}
				}
			}
		}

		private void selectFormDialog(boolean abrirEmForm, Conexao conexao, String instrucao, String titulo) {
			if (abrirEmForm) {
				ConsultaFormulario form = ConsultaFormulario.criar2(null, conexao, instrucao);
				configLocationRelativeTo(form);
				form.setTitle(titulo);
				form.setVisible(true);
			} else {
				ConsultaDialogo form = ConsultaDialogo.criar2(null, conexao, instrucao);
				configLocationRelativeTo(form);
				form.setTitle(titulo);
				form.setVisible(true);
			}
		}

		private void updateFormDialog(boolean abrirEmForm, Conexao conexao, String instrucao, String titulo) {
			if (abrirEmForm) {
				UpdateFormulario form = UpdateFormulario.criar2(null, conexao, instrucao);
				configLocationRelativeTo(form);
				form.setTitle(titulo);
				form.setVisible(true);
			} else {
				UpdateDialogo form = UpdateDialogo.criar2(null, conexao, instrucao);
				configLocationRelativeTo(form);
				form.setTitle(titulo);
				form.setVisible(true);
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
				ComplementoDialogo form = ComplementoDialogo.criar((Dialog) null, complementoListener);
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
		public Set<String> getColecaoComplemento() {
			return objeto.getComplementos();
		}

		@Override
		public String getComplementoPadrao() {
			return txtComplemento.getText();
		}

		@Override
		public String getTitle() {
			return objeto.getId();
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

		Conexao conexao = (Conexao) comboConexao.getSelectedItem();

		if (conexao != null) {
			OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
			TableModel model = modelo.getModelo();

			if (model instanceof PersistenciaModelo) {
				((PersistenciaModelo) model).setConexao(conexao);
			}
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
		return Util.confirmar(InternalContainer.this, msg, false);
	}

	private PersistenciaModelo.Parametros criarParametros(Connection conn, Conexao conexao, String consulta) {
		Parametros param = new Parametros(conn, conexao, consulta);
		param.setPrefixoNomeTabela(objeto.getPrefixoNomeTabela());
		param.setMapaSequencia(objeto.getMapaSequencias());
		param.setColunasChave(objeto.getChavesArray());
		param.setComColunaInfo(objeto.isColunaInfo());
		param.setTabela(objeto.getTabela2());
		return param;
	}

	public void processarObjeto(String complemento, Graphics g, CabecalhoColuna cabecalho) {
		Conexao conexao = (Conexao) comboConexao.getSelectedItem();

		if (conexao == null) {
			return;
		}

		if (!continuar(complemento)) {
			processado.set(false);
			return;
		}

		StringBuilder consulta = getConsulta(conexao, complemento);

		try {
			Connection conn = ConexaoProvedor.getConnection(conexao);
			Parametros param = criarParametros(conn, conexao, consulta.toString());
			OrdenacaoModelo modeloOrdenacao = new OrdenacaoModelo(Persistencia.criarPersistenciaModelo(param));
			modeloOrdenacao.getModelo().setConexao(conexao);
			objeto.setComplemento(txtComplemento.getText());
			tabelaPersistencia.setModel(modeloOrdenacao);
			threadTitulo(getTituloAtualizado());
			cabecalhoFiltro = null;
			atualizarTitulo();
			configurarCabecalhoTabela(modeloOrdenacao, cabecalho);
			Util.ajustar(tabelaPersistencia, g == null ? getGraphics() : g);
			processarTabelaBuscaAuto();
		} catch (Exception ex) {
			mensagemException(ex);
		}

		toolbar.buttonBuscaAuto.habilitar(tabelaPersistencia.getModel().getRowCount() > 0 && buscaAuto);
		tabelaListener.tabelaMouseClick(tabelaPersistencia, -1);
		configurarAlturaAutomatica();
	}

	private void configurarCabecalhoTabela(OrdenacaoModelo modeloOrdenacao, CabecalhoColuna cabecalho) {
		TableColumnModel columnModel = tabelaPersistencia.getColumnModel();
		List<Coluna> colunas = modeloOrdenacao.getModelo().getColunas();

		for (int i = 0; i < colunas.size(); i++) {
			TableColumn tableColumn = columnModel.getColumn(i);
			Coluna coluna = colunas.get(i);
			configTableColumn(tableColumn, coluna);
			CabecalhoColuna cabecalhoColuna = new CabecalhoColuna(cabecalhoColunaListener, modeloOrdenacao, coluna,
					!coluna.isColunaInfo());

			if (cabecalhoColuna.equals(cabecalho)) {
				cabecalhoColuna.copiar(cabecalho);
				cabecalhoFiltro = cabecalhoColuna;
			}

			tableColumn.setHeaderRenderer(cabecalhoColuna);
		}
	}

	private void processarTabelaBuscaAuto() {
		TabelaBuscaAuto tabelaBuscaAuto = objeto.getTabelaBuscaAuto();
		if (tabelaBuscaAuto != null) {
			OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
			int coluna = TabelaPersistenciaUtil.getIndiceColuna(tabelaPersistencia, tabelaBuscaAuto.getCampo());
			if (coluna != -1) {
				InternalUtil.atualizarColetores(tabelaPersistencia, coluna, tabelaBuscaAuto);
			}
			objeto.setTabelaBuscaAuto(null);
			if (visibilidadeListener != null) {
				boolean invisivel = modelo.getRowCount() == 0 && tabelaBuscaAuto.isVazioInvisivel();
				boolean visivel = objeto.isVisivel();
				objeto.setVisivel(!invisivel);
				visibilidadeListener.setVisible(!invisivel);
				setBackground(!visivel && objeto.isVisivel() ? Color.RED : null);
			}
		}
	}

	private transient CabecalhoColunaListener cabecalhoColunaListener = (cabecalho, string) -> processarObjeto(string,
			null, cabecalho);

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
			tableColumn.setCellRenderer(new CellRenderer(Color.GRAY, Color.WHITE));
		}
		if (coluna.isColunaInfo()) {
			tableColumn.setCellRenderer(new InternalRenderer());
		}
	}

	private void configurarAlturaAutomatica() {
		if (objeto.isAjusteAutoForm() && tamanhoAutomatico && configAlturaAutomaticaListener != null) {
			configAlturaAutomaticaListener.configAlturaAutomatica(tabelaPersistencia.getModel().getRowCount());
		}
	}

	private transient FragmentoListener fragmentoListener = new FragmentoListener() {
		@Override
		public void aplicarFragmento(Fragmento f) {
			txtComplemento.setText(f.getValor());
			actionListenerInner.actionPerformed(null);
		}

		@Override
		public List<String> getGrupoFiltro() {
			OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
			List<String> colunas = new ArrayList<>();
			TableModel model = modelo.getModelo();

			for (int i = 0; i < model.getColumnCount(); i++) {
				colunas.add(model.getColumnName(i));
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

			InternalContainer.this.actionPerformed(null);

			if (objeto.isAjusteAutoEnter()) {
				tamanhoAutomatico = false;
			}
		}

		private void processar() {
			tamanhoAutomatico = true;
			InternalContainer.this.actionPerformed(null);
			tamanhoAutomatico = false;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		processarObjeto(cabecalhoFiltro == null ? Constantes.VAZIO : cabecalhoFiltro.getFiltroComplemento(), null,
				cabecalhoFiltro);
	}

	public void buscaAutomatica(String campo, String argumentos) {
		Conexao conexao = (Conexao) comboConexao.getSelectedItem();

		if (conexao != null) {
			txtComplemento.setText("AND " + campo + " IN (" + argumentos + ")");
			destacarTitulo = true;
			actionListenerInner.processar();
		}
	}

	public void aplicarConfigArquivo(InternalConfig config) {
		Conexao conexaoSel = null;

		if (!Util.estaVazio(config.getConexao())) {
			for (int i = 0; i < comboConexao.getItemCount(); i++) {
				Conexao c = comboConexao.getItemAt(i);

				if (config.getConexao().equalsIgnoreCase(c.getNome())) {
					conexaoSel = c;
					break;
				}
			}

			if (conexaoSel != null) {
				comboConexao.setSelectedItem(conexaoSel);
			}
		}

		txtComplemento.setText(config.getComplemento());
		destacarTitulo = true;
		actionListenerInner.processar();
		Util.ajustar(tabelaPersistencia, config.getGraphics());
	}

	public void buscaAutomaticaApos() {
		toolbar.buttonBaixar.limpar2Acao.actionPerformed(null);
	}

	public void linkAutomatico(String campo, String argumento) {
		if (!objeto.isLinkAuto() || argumento == null) {
			return;
		}

		OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
		TableModel model = modelo.getModelo();
		tabelaPersistencia.clearSelection();

		if (model instanceof PersistenciaModelo) {
			int coluna = TabelaPersistenciaUtil.getIndiceColuna(tabelaPersistencia, campo);

			if (coluna != -1) {
				for (int i = 0; i < modelo.getRowCount(); i++) {
					if (argumento.equals(modelo.getValueAt(i, coluna))) {
						tabelaPersistencia.addRowSelectionInterval(i, i);
					}
				}
			}
		}
	}

	public String getTituloAtualizado() {
		OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
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

		OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
		TableModel model = modelo.getModelo();

		if (model instanceof PersistenciaModelo) {
			List<Integer> indices = Util.getIndicesLinha(tabelaPersistencia);

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
		Conexao conexao = (Conexao) comboConexao.getSelectedItem();

		if (conexao != null) {
			actionListenerInner.processar();
		}
	}

	public void limpar2() {
		toolbar.buttonBaixar.limpar2Acao.actionPerformed(null);
	}

	private class TabelaListener implements TabelaPersistenciaListener {
		@Override
		public void copiarNomeColuna(TabelaPersistencia tabela, String nome, String anterior) {
			String string = Util.estaVazio(anterior) ? Constantes.VAZIO : anterior;
			txtComplemento.setText("AND " + nome + " = " + string);

			if (!Util.estaVazio(anterior) && Preferencias.isExecAposCopiarColunaConcatenado()) {
				actionListenerInner.actionPerformed(null);
			}
		}

		public void concatenarNomeColuna(TabelaPersistencia tabela, String nome) {
			String complemento = txtComplemento.getText();
			txtComplemento.setText(complemento + " AND " + nome + " = ");
		}

		@Override
		public void tabelaMouseClick(TabelaPersistencia tabela, int colunaClick) {
			OrdenacaoModelo modelo = tabela.getModelo();
			TableModel model = modelo.getModelo();

			if (model instanceof PersistenciaModelo) {
				int[] linhas = tabela.getSelectedRows();

				if (linhas != null && linhas.length > 0) {
					String[] chaves = objeto.getChavesArray();

					toolbar.buttonUpdate.setEnabled(chaves.length > 0 && linhas.length == 1);
					toolbar.buttonExcluir.setEnabled(chaves.length > 0);
					toolbar.labelTotal.setText(Constantes.VAZIO + linhas.length);
				} else {
					toolbar.excluirAtualizarEnable(false);
					toolbar.labelTotal.limpar();
				}

				if (colunaClick >= 0 && linhas != null && linhas.length == 1 && !listaGrupoLink.isEmpty()
						&& linkAutomaticoListener != null) {
					mouseClick(tabela, colunaClick);
				}
			} else {
				toolbar.excluirAtualizarEnable(false);
			}
		}

		private void mouseClick(TabelaPersistencia tabela, int colunaClick) {
			int indiceLinkSelecionado = -1;

			for (int i = 0; i < listaGrupoLink.size(); i++) {
				GrupoLinkAuto link = listaGrupoLink.get(i);

				if (TabelaPersistenciaUtil.getIndiceColuna(tabela, link.getCampo()) == colunaClick) {
					indiceLinkSelecionado = i;
				}
			}

			if (indiceLinkSelecionado == -1) {
				return;
			}

			List<String> lista = TabelaPersistenciaUtil.getValoresLinhaPelaColuna(tabela, colunaClick);
			if (lista.size() != 1) {
				return;
			}

			linkAutomaticoListener.linkAutomatico(listaGrupoLink.get(indiceLinkSelecionado), lista.get(0));
		}
	}

	public Component getSuporte() {
		return suporte;
	}

	public void setSuporte(Component suporte) {
		this.suporte = suporte;
	}

	public void selecionarConexao(Conexao conexao) {
		if (conexao != null) {
			comboConexao.setSelectedItem(conexao);
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

	public InternalListener.BuscaAutomatica getBuscaAutomaticaListener() {
		return buscaAutomaticaListener;
	}

	public void setBuscaAutomaticaListener(InternalListener.BuscaAutomatica buscaAutomaticaListener) {
		this.buscaAutomaticaListener = buscaAutomaticaListener;
	}

	public InternalListener.LinkAutomatico getLinkAutomaticoListener() {
		return linkAutomaticoListener;
	}

	public void setLinkAutomaticoListener(InternalListener.LinkAutomatico linkAutomaticoListener) {
		this.linkAutomaticoListener = linkAutomaticoListener;
	}

	public InternalListener.BuscaAutomaticaApos getBuscaAutomaticaAposListener() {
		return buscaAutomaticaAposListener;
	}

	public void setBuscaAutomaticaAposListener(InternalListener.BuscaAutomaticaApos buscaAutomaticaAposListener) {
		this.buscaAutomaticaAposListener = buscaAutomaticaAposListener;
	}

	public InternalListener.ConfigAlturaAutomatica getConfigAlturaAutomaticaListener() {
		return configAlturaAutomaticaListener;
	}

	public void setConfigAlturaAutomaticaListener(
			InternalListener.ConfigAlturaAutomatica configAlturaAutomaticaListener) {
		this.configAlturaAutomaticaListener = configAlturaAutomaticaListener;
	}

	public InternalListener.Titulo getTituloListener() {
		return tituloListener;
	}

	public void setTituloListener(InternalListener.Titulo tituloListener) {
		this.tituloListener = tituloListener;

		if (tituloListener != null) {
			atualizarTitulo();
		}
	}

	public InternalListener.Selecao getSelecaoListener() {
		return selecaoListener;
	}

	public void setSelecaoListener(InternalListener.Selecao selecaoListener) {
		this.selecaoListener = selecaoListener;
	}

	public InternalListener.Dimensao getDimensaoListener() {
		return dimensaoListener;
	}

	public void setDimensaoListener(InternalListener.Dimensao dimensaoListener) {
		this.dimensaoListener = dimensaoListener;
	}

	public InternalListener.Apelido getApelidoListener() {
		return apelidoListener;
	}

	public void setApelidoListener(InternalListener.Apelido apelidoListener) {
		this.apelidoListener = apelidoListener;
	}

	public InternalListener.Componente getComponenteListener() {
		return componenteListener;
	}

	public void setComponenteListener(InternalListener.Componente componenteListener) {
		this.componenteListener = componenteListener;
	}

	public InternalListener.Visibilidade getVisibilidadeListener() {
		return visibilidadeListener;
	}

	public void setVisibilidadeListener(InternalListener.Visibilidade visibilidadeListener) {
		this.visibilidadeListener = visibilidadeListener;
	}

	public InternalListener.Alinhamento getAlinhamentoListener() {
		return alinhamentoListener;
	}

	public void setAlinhamentoListener(InternalListener.Alinhamento alinhamentoListener) {
		this.alinhamentoListener = alinhamentoListener;
		toolbar.buttonInfo.menuAlinhamento.habilitar(alinhamentoListener != null);
	}

	public InternalListener.Largura getLarguraListener() {
		return larguraListener;
	}

	public void setLarguraListener(InternalListener.Largura larguraListener) {
		this.larguraListener = larguraListener;
		toolbar.buttonInfo.menuAlinhamento.habilitar(larguraListener != null);
	}

	@Override
	public void processar(Formulario formulario, Map<String, Object> args) {
		LOG.log(Level.FINEST, "processar");
	}

	public void formularioVisivel() {
		Util.ajustar(tabelaPersistencia, getGraphics());
	}

	@Override
	public String getStringPersistencia() {
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return InternalFabrica.class;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public Titulo getTitulo() {
		return new AbstratoTitulo() {
			@Override
			public String getTituloMin() {
				return objeto.getId();
			}

			@Override
			public String getTitulo() {
				return objeto.getId();
			}

			@Override
			public String getHint() {
				return objeto.getId();
			}

			@Override
			public Icon getIcone() {
				return Icones.CUBO;
			}
		};
	}

	@Override
	public File getFile() {
		return null;
	}

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
		LOG.log(Level.FINEST, "adicionadoAoFichario");
	}

	@Override
	public void excluindoDoFichario(Fichario fichario) {
		LOG.log(Level.FINEST, "excluindoDoFichario");
	}
}