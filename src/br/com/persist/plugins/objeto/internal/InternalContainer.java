package br.com.persist.plugins.objeto.internal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagLayout;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.Icon;
import javax.swing.JColorChooser;
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
import br.com.persist.assistencia.Imagens;
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
import br.com.persist.componente.SetLista;
import br.com.persist.componente.SetLista.Coletor;
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
import br.com.persist.plugins.objeto.Desktop;
import br.com.persist.plugins.objeto.Instrucao;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.ObjetoConstantes;
import br.com.persist.plugins.objeto.ObjetoMensagens;
import br.com.persist.plugins.objeto.ObjetoPreferencia;
import br.com.persist.plugins.objeto.ObjetoUtil;
import br.com.persist.plugins.objeto.Relacao;
import br.com.persist.plugins.objeto.vinculo.Pesquisa;
import br.com.persist.plugins.objeto.vinculo.Referencia;
import br.com.persist.plugins.persistencia.Coluna;
import br.com.persist.plugins.persistencia.IndiceValor;
import br.com.persist.plugins.persistencia.MemoriaModelo;
import br.com.persist.plugins.persistencia.OrdenacaoModelo;
import br.com.persist.plugins.persistencia.Persistencia;
import br.com.persist.plugins.persistencia.PersistenciaException;
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

public class InternalContainer extends Panel implements ItemListener, Pagina {
	private static final long serialVersionUID = 1L;
	private final transient ActionListenerInner actionListenerInner = new ActionListenerInner();
	private static final String CHAVE_MSG_CONCAT_COMPLEMENTO = "msg.concatenar_complemento";
	private final TabelaPersistencia tabelaPersistencia = new TabelaPersistencia();
	private transient InternalListener.ConfiguraAltura configuraAlturaListener;
	private final Button btnArrasto = new Button(Action.actionIconDestacar());
	private transient TabelaListener tabelaListener = new TabelaListener();
	private transient InternalListener.RelacaoObjeto relacaoObjetoListener;
	private transient InternalListener.Visibilidade visibilidadeListener;
	private transient InternalListener.Alinhamento alinhamentoListener;
	private transient InternalListener.Componente componenteListener;
	private Panel panelAguardando = new Panel(new GridBagLayout());
	private transient InternalListener.Dimensao dimensaoListener;
	private final AtomicBoolean processado = new AtomicBoolean();
	private transient InternalListener.Vinculo vinculoListener;
	private transient InternalListener.Largura larguraListener;
	private transient InternalListener.Selecao selecaoListener;
	private final TextField txtComplemento = new TextField(33);
	private transient InternalListener.Titulo tituloListener;
	private static final Logger LOG = Logger.getGlobal();
	private ScrollPane scrollPane = new ScrollPane();
	private final JComboBox<Conexao> comboConexao;
	private final Toolbar toolbar = new Toolbar();
	private CabecalhoColuna cabecalhoFiltro;
	private final transient Objeto objeto;
	private boolean destacarTitulo;
	private boolean buscaAuto;
	private int contadorAuto;

	public InternalContainer(Janela janela, Conexao padrao, Objeto objeto, Graphics g, boolean buscaAuto) {
		tabelaPersistencia.setChaveamento(ObjetoUtil.criarMapaCampoNomes(objeto.getChaveamento()));
		tabelaPersistencia.setMapeamento(ObjetoUtil.criarMapaCampoChave(objeto.getMapeamento()));
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
		SwingUtilities.invokeLater(() -> processar(g));
	}

	private void processar(Graphics g) {
		processar("", g, null);
	}

	private void atualizar() {
		Container parent = this;
		Desktop desktop = null;
		while (parent != null) {
			if (parent instanceof Desktop) {
				desktop = (Desktop) parent;
				break;
			}
			parent = parent.getParent();
		}
		if (desktop != null) {
			SwingUtilities.updateComponentTreeUI(desktop);
			desktop.repaint();
		}
	}

	static Action actionMenu(String chave, Icon icon) {
		return Action.acaoMenu(ObjetoMensagens.getString(chave), icon);
	}

	static Action actionMenu(String chave) {
		return actionMenu(chave, null);
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(tabelaPersistencia));
		panelAguardando.add(Preferencias.isDesconectado() ? new Label(Mensagens.getTituloAplicacao(), false)
				: new Label("label.aguardando"));
		add(BorderLayout.CENTER, panelAguardando);
	}

	private void configurar() {
		DragSource dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(btnArrasto, DnDConstants.ACTION_COPY, dge -> {
			Conexao conexao = getConexao();
			if (conexao == null) {
				return;
			}
			Dimension dimension = null;
			if (dimensaoListener != null) {
				dimension = dimensaoListener.getDimensoes();
			}
			if (dimension == null) {
				dimension = Constantes.SIZE;
			}
			dge.startDrag(null, new InternalTransferidor(objeto, conexao, dimension), listenerArrasto);
		});
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), Constantes.EXEC);
		getActionMap().put(Constantes.EXEC, toolbar.buttonSincronizar.atualizarAcao);
	}

	public void processar(String complemento, Graphics g, CabecalhoColuna cabecalho) {
		antesProcessar();
		if (Preferencias.isDesconectado()) {
			processado.set(false);
			return;
		}
		Conexao conexao = getConexao();
		if (conexao != null) {
			if (continuar(complemento, conexao)) {
				processar(complemento, g, cabecalho, conexao);
			} else {
				processado.set(false);
			}
		}
	}

	private void processar(String complemento, Graphics g, CabecalhoColuna cabecalho, Conexao conexao) {
		StringBuilder consulta = getConsulta(conexao, complemento);
		try {
			Connection conn = ConexaoProvedor.getConnection(conexao);
			Parametros param = criarParametros(conn, conexao, consulta.toString());
			OrdenacaoModelo modeloOrdenacao = consultarEModeloOrdenacao(conexao, param);
			threadTitulo(getTituloAtualizado());
			cabecalhoFiltro = null;
			atualizarTitulo();
			configurarCabecalhoTabela(modeloOrdenacao, cabecalho);
			Util.ajustar(tabelaPersistencia, g == null ? getGraphics() : g);
			processarReferencia();
		} catch (Exception ex) {
			mensagemException(ex);
		}
		toolbar.buttonPesquisa.habilitar(tabelaPersistencia.getModel().getRowCount() > 0 && buscaAuto);
		tabelaListener.tabelaMouseClick(tabelaPersistencia, -1);
		configurarAltura();
	}

	private OrdenacaoModelo consultarEModeloOrdenacao(Conexao conexao, Parametros param) throws PersistenciaException {
		PersistenciaModelo persistenciaModelo = Persistencia.criarPersistenciaModelo(param);
		OrdenacaoModelo modeloOrdenacao = new OrdenacaoModelo(persistenciaModelo);
		persistenciaModelo.setPrefixoNomeTabela(objeto.getPrefixoNomeTabela());
		objeto.setComplemento(txtComplemento.getText());
		tabelaPersistencia.setModel(modeloOrdenacao);
		persistenciaModelo.setConexao(conexao);
		persistenciaModelo.setComponente(this);
		checarScrollPane();
		return modeloOrdenacao;
	}

	private void checarScrollPane() {
		if (scrollPane.getViewport().getView() == null) {
			remove(panelAguardando);
			scrollPane.getViewport().setView(tabelaPersistencia);
			add(BorderLayout.CENTER, scrollPane);
			SwingUtilities.updateComponentTreeUI(InternalContainer.this);
		}
	}

	private StringBuilder getConsulta(Conexao conexao, String complemento) {
		StringBuilder builder = new StringBuilder();
		objeto.select(builder, conexao);
		objeto.joins(builder, conexao, objeto.getPrefixoNomeTabela());
		objeto.where(builder, txtComplemento.getText(), complemento);
		objeto.orderBy(builder);
		Objeto.concatenar(builder, objeto.getFinalConsulta());
		return builder;
	}

	private boolean continuar(String complemento, Conexao conexao) {
		if (objeto.isSane() && todosVazio(complemento, conexao)) {
			String msg = ObjetoMensagens.getString("msg.sane", objeto.getId() + " - " + objeto.getTabela());
			Util.mensagem(InternalContainer.this, msg);
			return false;
		}
		if (!Util.estaVazio(txtComplemento.getText()) || !Util.estaVazio(complemento)
				|| !Util.estaVazio(objeto.getFinalConsulta()) || !objeto.isCcsc()) {
			return true;
		}
		String msg = ObjetoMensagens.getString("msg.ccsc", objeto.getId() + " - " + objeto.getTabela());
		return Util.confirmar(InternalContainer.this, msg, false);
	}

	private boolean todosVazio(String filtro, Conexao conexao) {
		if (!Util.estaVazio(conexao.getFiltro())) {
			return Util.estaVazio(txtComplemento.getText()) && Util.estaVazio(filtro);
		} else if (!Util.estaVazio(conexao.getFinalConsulta())) {
			return Util.estaVazio(txtComplemento.getText()) && Util.estaVazio(filtro)
					&& Util.estaVazio(objeto.getFinalConsulta());
		}
		return Util.estaVazio(txtComplemento.getText()) && Util.estaVazio(filtro)
				&& Util.estaVazio(objeto.getFinalConsulta());
	}

	private PersistenciaModelo.Parametros criarParametros(Connection conn, Conexao conexao, String consulta) {
		Parametros param = new Parametros(conn, conexao, consulta);
		param.setMapaSequencia(objeto.getMapaSequencias());
		param.setColunasChave(objeto.getChavesArray());
		param.setComColunaInfo(objeto.isColunaInfo());
		param.setTabela(objeto.getTabela());
		return param;
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

	private void processarReferencia() {
		Referencia referencia = objeto.getReferenciaPesquisa();
		if (referencia != null) {
			OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
			int coluna = TabelaPersistenciaUtil.getIndiceColuna(tabelaPersistencia, referencia.getCampo());
			if (coluna != -1) {
				InternalUtil.atualizarColetores(tabelaPersistencia, coluna, referencia);
			}
			objeto.setReferenciaPesquisa(null);
			processarReferenciaVisibilidade(referencia, modelo);
		}
	}

	private void processarReferenciaVisibilidade(Referencia referencia, OrdenacaoModelo modelo) {
		if (visibilidadeListener != null && referencia.isValidoInvisibilidade()) {
			boolean invisivel = modelo.getRowCount() == 0 && referencia.isVazioInvisivel();
			boolean visivel = objeto.isVisivel();
			objeto.setVisivel(!invisivel);
			visibilidadeListener.setVisible(!invisivel);
			setBackground(!visivel && objeto.isVisivel() ? Color.RED : null);
			if (!visivel && objeto.isVisivel()) {
				visibilidadeListener.checarLargura(InternalContainer.this);
			}
		}
	}

	private void antesProcessar() {
		if (visibilidadeListener != null && objeto.isChecarLargura()) {
			objeto.setChecarLargura(false);
			visibilidadeListener.checarLargura(InternalContainer.this);
		}
		if (objeto.getCorTmp() != null) {
			setBackground(objeto.getCorTmp());
			objeto.setCorTmp(null);
		}
	}

	private transient CabecalhoColunaListener cabecalhoColunaListener = (cabecalho, string) -> processar(string, null,
			cabecalho);

	private void mensagemException(Exception ex) {
		if (Preferencias.isErroCriarConnection()) {
			if (!Preferencias.isExibiuMensagemConnection()) {
				Util.stackTraceAndMessage("PAINEL OBJETO: " + objeto.getId() + " -> " + objeto.getPrefixoNomeTabela()
						+ objeto.getTabela(), ex, this);
				Preferencias.setExibiuMensagemConnection(true);
			}
		} else {
			Util.stackTraceAndMessage(
					"PAINEL OBJETO: " + objeto.getId() + " -> " + objeto.getPrefixoNomeTabela() + objeto.getTabela(),
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

	private void configurarAltura() {
		if (objeto.isAjusteAutoForm() && configuraAlturaListener != null) {
			configuraAlturaListener.configurarAltura(tabelaPersistencia.getModel().getRowCount());
		}
	}

	public void pesquisar(Conexao conexao, Referencia referencia, String argumentos) {
		if (conexao != null) {
			selecionarConexao(conexao);
			txtComplemento.setText("AND " + referencia.getCampo() + " IN (" + argumentos + ")");
			destacarTitulo = true;
			actionListenerInner.actionPerformed(null);
		}
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private final Button buttonExcluir = new Button(new ExcluirRegistrosAcao());
		private final ButtonSincronizar buttonSincronizar = new ButtonSincronizar();
		private final ButtonComplemento buttonComplemento = new ButtonComplemento();
		private final ButtonPesquisa buttonPesquisa = new ButtonPesquisa();
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
			add(buttonPesquisa);
			add(true, buttonUpdate);
			add(buttonSincronizar);
			add(true, buttonComplemento);
			add(txtComplemento);
			add(labelTotal);
			add(buttonBaixar);
			add(buttonFuncoes);
			add(true, comboConexao);
			buttonInfo.ini(objeto);
			buttonUpdate.complemento(objeto);
			buttonPesquisa.complemento(objeto);
		}

		private void habilitarUpdateExcluir(boolean b) {
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
			private Action limparOutrosAcao = actionMenu("label.limpar_outros", Icones.NOVO);
			private Action conexaoAcao = Action.actionMenu(Constantes.LABEL_CONEXAO2, null);
			private Action objetoAcao = Action.actionMenu(Constantes.LABEL_OBJETO, null);

			private ButtonBaixar() {
				super("label.baixar", Icones.BAIXAR);
				addMenuItem(conexaoAcao);
				addMenuItem(true, objetoAcao);
				addMenuItem(true, limparAcao);
				addMenuItem(limpar2Acao);
				addMenuItem(limparOutrosAcao);
				eventos();
			}

			private void eventos() {
				objetoAcao.setActionListener(e -> limparPeloObjeto());
				limparAcao.setActionListener(e -> limpar());
				conexaoAcao.setActionListener(e -> limparUsandoConexao());
				limparOutrosAcao.setActionListener(e -> limparOutros());
				limpar2Acao.setActionListener(e -> limpar2());
			}

			private void limparPeloObjeto() {
				String string = "";
				if (!Util.estaVazio(txtComplemento.getText())) {
					String[] simNao = getArraySimNao();
					String opcao = Util.getValorInputDialog(InternalContainer.this,
							ObjetoMensagens.getString(CHAVE_MSG_CONCAT_COMPLEMENTO), simNao);
					if (Util.estaVazio(opcao)) {
						return;
					}
					if (simNao[0].equals(opcao)) {
						string = txtComplemento.getText() + " ";
					}
				}
				txtComplemento.setText(ltrim(string + objeto.getComplemento()));
				if (Util.confirmar(InternalContainer.this, Constantes.LABEL_EXECUTAR)) {
					actionListenerInner.actionPerformed(null);
				}
			}

			private void limpar() {
				txtComplemento.limpar();
				if (Util.confirmar(InternalContainer.this, Constantes.LABEL_EXECUTAR)) {
					actionListenerInner.actionPerformed(null);
				}
			}

			private void limparUsandoConexao() {
				Conexao conexao = getConexao();
				String filtro = Constantes.VAZIO;
				if (conexao != null && !Util.estaVazio(conexao.getFiltro())) {
					filtro = conexao.getFiltro();
				} else if (conexao != null && !Util.estaVazio(conexao.getFinalConsulta())) {
					filtro = conexao.getFinalConsulta();
				}
				String string = "";
				if (!Util.estaVazio(txtComplemento.getText())) {
					String[] simNao = getArraySimNao();
					String opcao = Util.getValorInputDialog(InternalContainer.this,
							ObjetoMensagens.getString(CHAVE_MSG_CONCAT_COMPLEMENTO), simNao);
					if (Util.estaVazio(opcao)) {
						return;
					}
					if (simNao[0].equals(opcao)) {
						string = txtComplemento.getText() + " ";
					}
				}
				txtComplemento.setText(ltrim(string + filtro));
				if (Util.confirmar(InternalContainer.this, Constantes.LABEL_EXECUTAR)) {
					actionListenerInner.actionPerformed(null);
				}
			}

			private void limpar2() {
				boolean salvar = false;
				Variavel cv = VariavelProvedor.getVariavel("LIMPAR2");
				if (cv == null) {
					cv = new Variavel("LIMPAR2", "AND 1 > 2");
					VariavelProvedor.adicionar(cv);
					salvar = true;
				}
				checarSalvarVariavelProvedor(salvar);
				txtComplemento.setText(cv.getValor());
				actionListenerInner.actionPerformed(null);
			}

			private void limparOutros() {
				if (visibilidadeListener != null) {
					visibilidadeListener.limparOutros(InternalContainer.this);
				}
			}

			private void checarSalvarVariavelProvedor(boolean salvar) {
				if (salvar) {
					try {
						VariavelProvedor.salvar();
						VariavelProvedor.inicializar();
					} catch (Exception e) {
						LOG.log(Level.SEVERE, Constantes.ERRO, e);
					}
				}
			}
		}

		private class ExcluirRegistrosAcao extends Acao {
			private static final long serialVersionUID = 1L;

			private ExcluirRegistrosAcao() {
				super(false, ObjetoMensagens.getString("label.excluir_registro"), false, Icones.EXCLUIR);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				int[] linhas = tabelaPersistencia.getSelectedRows();
				if (linhas != null && linhas.length > 0 && Util.confirmaExclusao(InternalContainer.this, false)) {
					OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
					List<List<IndiceValor>> listaValores = new ArrayList<>();
					for (int linha : linhas) {
						int excluido = modelo.excluirRegistro(linha, objeto.getPrefixoNomeTabela(), true);
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
			private Action copiarNomeTabAcao = actionMenu("label.copiar_nome_tabela");
			private Action copiarAcao = actionMenu("label.copiar_complemento");
			private Action concatAcao = actionMenu("label.baixar_concatenado");
			private Action normalAcao = actionMenu("label.baixar_normal");

			private ButtonComplemento() {
				super("label.complemento", Icones.BAIXAR2);
				addMenuItem(normalAcao);
				addMenuItem(true, concatAcao);
				addMenuItem(true, copiarAcao);
				addMenuItem(true, copiarNomeTabAcao);
				copiarNomeTabAcao.setActionListener(e -> copiarNomeTabela());
				copiarAcao.setActionListener(e -> copiarComplemento());
				concatAcao.setActionListener(e -> processar(false));
				normalAcao.setActionListener(e -> processar(true));
			}

			private void copiarNomeTabela() {
				Util.setContentTransfered(objeto.getTabelaEsquema(getConexao()));
			}

			private void copiarComplemento() {
				String string = txtComplemento.getText().trim();
				Util.setContentTransfered(string);
			}

			private void processar(boolean normal) {
				Conexao conexao = getConexao();
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
					if (Util.confirmar(InternalContainer.this, Constantes.LABEL_EXECUTAR)) {
						actionListenerInner.actionPerformed(null);
					}
				}
			}
		}

		private class ButtonSincronizar extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action sincronizarAcao = Action.actionMenu(Constantes.LABEL_SINCRONIZAR, Icones.SINCRONIZAR);
			private MenuItem itemAtualizarAuto = new MenuItem(
					ObjetoMensagens.getString(ObjetoConstantes.LABEL_ATUALIZAR_AUTO), false, Icones.ATUALIZAR);
			private Action atualizarAcao = Action.actionMenuAtualizar();

			private ButtonSincronizar() {
				super(Constantes.LABEL_ATUALIZAR, Icones.ATUALIZAR);
				addMenuItem(atualizarAcao);
				addMenuItem(true, sincronizarAcao);
				addMenuItem(true, itemAtualizarAuto);
				itemAtualizarAuto.setText(itemAtualizarAuto.getText() + "   ");
				itemAtualizarAuto.setToolTipText(ObjetoMensagens.getString("hint.atualizar_auto"));
				eventos();
			}

			private void eventos() {
				itemAtualizarAuto.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent e) {
						if (thread == null) {
							itemAtualizarAuto.setText(ObjetoMensagens.getString(ObjetoConstantes.LABEL_ATUALIZAR_AUTO));
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
				private final String titulo = ObjetoMensagens.getString(ObjetoConstantes.LABEL_ATUALIZAR_AUTO);

				@Override
				public void run() {
					while (!Thread.currentThread().isInterrupted() && itemAtualizarAuto.isDisplayable()) {
						try {
							Thread.sleep(ObjetoPreferencia.getIntervaloPesquisaAuto());
							contadorAuto++;
							itemAtualizarAuto.setText(titulo + " " + contadorAuto);
							SwingUtilities.invokeLater(() -> actionListenerInner.actionPerformed(null));
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

		private class ButtonPesquisa extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private boolean habilitado;

			private ButtonPesquisa() {
				super("label.buscaAuto", Icones.FIELDS);
			}

			private void complemento(Objeto objeto) {
				List<Pesquisa> pesquisas = objeto.getPesquisas();
				for (Pesquisa p : pesquisas) {
					addMenu(new MenuPesquisa(p));
				}
				habilitado = !pesquisas.isEmpty();
				setEnabled(habilitado);
			}

			private void habilitar(boolean b) {
				setEnabled(habilitado && b);
			}

			private class MenuPesquisa extends MenuPadrao2 {
				private static final long serialVersionUID = 1L;
				private Action elementosAcao = Action.actionMenu("label.elementos", null);
				private final transient Pesquisa pesquisa;

				private MenuPesquisa(Pesquisa pesquisa) {
					super(pesquisa.getNomeParaMenuItem(), false, iconePesquisa(pesquisa));
					addMenuItem(true, elementosAcao);
					this.pesquisa = pesquisa;
					semAspasAcao.setActionListener(e -> processar(false));
					comAspasAcao.setActionListener(e -> processar(true));
					elementosAcao.setActionListener(e -> elementos());
				}

				private void elementos() {
					StringBuilder sb = new StringBuilder();
					sb.append(Mensagens.getString("label.total") + ": " + pesquisa.getReferencias().size());
					sb.append(Constantes.QL + "-----------------");
					for (Referencia ref : pesquisa.getReferencias()) {
						sb.append(Constantes.QL + ref.toString2());
					}
					Util.mensagem(InternalContainer.this, sb.toString());
				}

				private void processar(boolean apostrofes) {
					int coluna = -1;
					if (vinculoListener != null) {
						coluna = TabelaPersistenciaUtil.getIndiceColuna(tabelaPersistencia,
								pesquisa.getReferencia().getCampo());
					}
					if (coluna != -1) {
						List<String> lista = TabelaPersistenciaUtil.getValoresLinha(tabelaPersistencia, coluna);
						if (lista.isEmpty()) {
							Util.mensagem(InternalContainer.this, pesquisa.getReferencia().getCampo() + " vazio.");
						} else {
							pesquisar(lista, apostrofes, coluna);
						}
					}
				}

				private void pesquisar(List<String> lista, boolean apostrofes, int coluna) {
					pesquisa.setProcessado(false);
					pesquisa.inicializarColetores(lista);
					pesquisa.validoInvisibilidade(vinculoListener.validoInvisibilidade());
					vinculoListener.pesquisar(getConexao(), pesquisa, Util.getStringLista(lista, apostrofes, false));
					pesquisarFinal(coluna);
				}

				private void pesquisarFinal(int coluna) {
					setEnabled(pesquisa.isProcessado());
					if (pesquisa.isProcessado()) {
						vinculoListener.pesquisarApos(pesquisa);
					}
					SwingUtilities.invokeLater(() -> processarColunaInfo(coluna));
					SwingUtilities.invokeLater(InternalContainer.this::atualizar);
				}

				private void processarColunaInfo(int coluna) {
					if (objeto.isColunaInfo()) {
						List<Integer> indices = Util.getIndicesLinha(tabelaPersistencia);
						for (int linha : indices) {
							InternalUtil.consolidarColetores(tabelaPersistencia, linha, coluna, pesquisa);
						}
						Util.ajustar(tabelaPersistencia, InternalContainer.this.getGraphics());
					}
				}
			}
		}

		private class ButtonUpdate extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action dadosAcao = Action.actionMenu("label.dados", Icones.TABELA);
			private List<MenuInstrucao> listaMenuInstrucao = new ArrayList<>();
			private MenuUpdateMul menuUpdateMul = new MenuUpdateMul();
			private MenuDeleteMul menuDeleteMul = new MenuDeleteMul();
			private MenuUpdate menuUpdate = new MenuUpdate();
			private MenuDelete menuDelete = new MenuDelete();
			private MenuInsert menuInsert = new MenuInsert();

			private ButtonUpdate() {
				super(Constantes.LABEL_UPDATE, Icones.UPDATE);
				addMenuItem(dadosAcao);
				addMenu(true, menuUpdate);
				addMenu(menuUpdateMul);
				addMenu(true, menuDelete);
				addMenu(menuDeleteMul);
				addMenu(true, menuInsert);
				eventos();
			}

			private void complemento(Objeto objeto) {
				if (objeto != null) {
					objeto.ordenarInstrucoes();
					for (Instrucao i : objeto.getInstrucoes()) {
						if (!Util.estaVazio(i.getValor())) {
							MenuInstrucao menu = new MenuInstrucao(i);
							listaMenuInstrucao.add(menu);
							addMenu(true, menu);
						}
					}
				}
			}

			private void eventos() {
				dadosAcao.setActionListener(e -> {
					OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
					int[] linhas = tabelaPersistencia.getSelectedRows();
					if (linhas != null && linhas.length == 1) {
						StringBuilder sb = new StringBuilder(objeto.getTabela());
						sb.append(Constantes.QL);
						Coletor coletor = new Coletor();
						SetLista.view(objeto.getId(), tabelaPersistencia.getListaNomeColunas(true), coletor,
								InternalContainer.this);
						if (!coletor.estaVazio()) {
							modelo.getDados(linhas[0], sb, coletor);
							Util.mensagem(InternalContainer.this, sb.toString());
						}
					}
				});
			}

			private void setHabilitado(int[] linhas) {
				boolean umaLinhaSel = linhas.length == 1;
				dadosAcao.setEnabled(umaLinhaSel);
				menuUpdate.setEnabled(umaLinhaSel);
				menuDelete.setEnabled(umaLinhaSel);
				menuUpdateMul.setHabilitado(linhas);
				menuDeleteMul.setHabilitado(linhas);
				menuInsert.setEnabled(umaLinhaSel);
				for (MenuInstrucao menu : listaMenuInstrucao) {
					menu.setHabilitado(linhas);
				}
			}

			private class MenuUpdateMul extends MenuPadrao3 {
				private static final long serialVersionUID = 1L;

				private MenuUpdateMul() {
					super(Constantes.LABEL_UPDATE, Icones.UPDATE);
					formularioAcao.setActionListener(e -> abrirUpdate(true));
					dialogoAcao.setActionListener(e -> abrirUpdate(false));
				}

				private void abrirUpdate(boolean abrirEmForm) {
					Conexao conexao = getConexao();
					if (conexao == null) {
						return;
					}
					OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
					int[] linhas = tabelaPersistencia.getSelectedRows();
					if (linhas != null && linhas.length > 0) {
						List<IndiceValor> chaves = modelo.getValoresChaves(linhas[0]);
						if (chaves.isEmpty()) {
							return;
						}
						Coletor coletor = new Coletor();
						SetLista.view(objeto.getId(), tabelaPersistencia.getListaNomeColunas(false), coletor,
								InternalContainer.this);
						if (!coletor.estaVazio()) {
							String instrucao = modelo.getUpdate(linhas[0], objeto.getPrefixoNomeTabela(), coletor,
									false);
							instrucao += Constantes.QL + " WHERE " + getComplementoChaves(false);
							if (!Util.estaVazio(instrucao)) {
								updateFormDialog(abrirEmForm, conexao, instrucao, "Atualizar");
							}
						}
					}
				}

				private void setHabilitado(int[] linhas) {
					setEnabled(linhas.length >= 1);
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
					Conexao conexao = getConexao();
					if (conexao == null) {
						return;
					}
					OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
					int[] linhas = tabelaPersistencia.getSelectedRows();
					if (linhas != null && linhas.length == 1) {
						List<IndiceValor> chaves = modelo.getValoresChaves(linhas[0]);
						if (chaves.isEmpty()) {
							return;
						}
						Coletor coletor = new Coletor();
						SetLista.view(objeto.getId(), tabelaPersistencia.getListaNomeColunas(false), coletor,
								InternalContainer.this);
						if (!coletor.estaVazio()) {
							String instrucao = modelo.getUpdate(linhas[0], objeto.getPrefixoNomeTabela(), coletor,
									true);
							if (!Util.estaVazio(instrucao)) {
								updateFormDialog(abrirEmForm, conexao, instrucao, "Update");
							}
						}
					}
				}
			}

			private class MenuDeleteMul extends MenuPadrao3 {
				private static final long serialVersionUID = 1L;

				private MenuDeleteMul() {
					super(Constantes.LABEL_DELETE, Icones.EXCLUIR);
					formularioAcao.setActionListener(e -> abrirUpdate(true));
					dialogoAcao.setActionListener(e -> abrirUpdate(false));
				}

				private void abrirUpdate(boolean abrirEmForm) {
					Conexao conexao = getConexao();
					if (conexao == null) {
						return;
					}
					OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
					int[] linhas = tabelaPersistencia.getSelectedRows();
					if (linhas != null && linhas.length > 0) {
						List<IndiceValor> chaves = modelo.getValoresChaves(linhas[0]);
						if (chaves.isEmpty()) {
							return;
						}
						String instrucao = modelo.getDelete(linhas[0], objeto.getPrefixoNomeTabela(), false);
						instrucao += Constantes.QL + " WHERE " + getComplementoChaves(false);
						if (!Util.estaVazio(instrucao)) {
							updateFormDialog(abrirEmForm, conexao, instrucao, "Excluir");
						}
					}
				}

				private void setHabilitado(int[] linhas) {
					setEnabled(linhas.length >= 1);
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
					Conexao conexao = getConexao();
					if (conexao == null) {
						return;
					}
					OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
					int[] linhas = tabelaPersistencia.getSelectedRows();
					if (linhas != null && linhas.length == 1) {
						List<IndiceValor> chaves = modelo.getValoresChaves(linhas[0]);
						if (chaves.isEmpty()) {
							return;
						}
						String instrucao = modelo.getDelete(linhas[0], objeto.getPrefixoNomeTabela(), true);
						if (!Util.estaVazio(instrucao)) {
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
					Conexao conexao = getConexao();
					if (conexao != null) {
						OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
						int[] linhas = tabelaPersistencia.getSelectedRows();
						if (linhas != null && linhas.length == 1) {
							Coletor coletor = new Coletor();
							SetLista.view(objeto.getId(), tabelaPersistencia.getListaNomeColunas(true), coletor,
									InternalContainer.this);
							if (!coletor.estaVazio()) {
								String instrucao = modelo.getInsert(linhas[0], objeto.getPrefixoNomeTabela(), coletor);
								if (!Util.estaVazio(instrucao)) {
									updateFormDialog(abrirEmForm, conexao, instrucao, "Insert");
								}
							}
						}
					}
				}
			}

			private class MenuInstrucao extends MenuPadrao3 {
				private static final long serialVersionUID = 1L;
				private final transient Instrucao instrucao;

				private MenuInstrucao(Instrucao instrucao) {
					super(instrucao.getNome(), false, instrucao.isSelect() ? Icones.ATUALIZAR : Icones.CALC);
					this.instrucao = instrucao;
					formularioAcao.setActionListener(e -> abrirInstrucao(true));
					dialogoAcao.setActionListener(e -> abrirInstrucao(false));
				}

				private void abrirInstrucao(boolean abrirEmForm) {
					Conexao conexao = getConexao();
					if (conexao == null) {
						return;
					}
					OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
					int[] linhas = tabelaPersistencia.getSelectedRows();
					if (linhas != null && linhas.length > 0) {
						Map<String, String> chaves = modelo.getMapaChaves(linhas[0]);
						if (chaves.isEmpty() || Util.estaVazio(instrucao.getValor())) {
							return;
						}
						Map<String, List<String>> mapaChaves = criar(chaves);
						for (int i = 1; i < linhas.length; i++) {
							chaves = modelo.getMapaChaves(linhas[i]);
							mergear(mapaChaves, chaves);
						}
						String conteudo = ObjetoUtil.substituir(instrucao.getValor(), mapaChaves);
						if (instrucao.isSelect()) {
							selectFormDialog(abrirEmForm, conexao, conteudo, instrucao.getNome());
						} else {
							updateFormDialog(abrirEmForm, conexao, conteudo, instrucao.getNome());
						}
					}
				}

				private void setHabilitado(int[] linhas) {
					setEnabled(instrucao.isSelecaoMultipla() ? linhas.length >= 1 : linhas.length == 1);
				}

				private void mergear(Map<String, List<String>> mapaChaves, Map<String, String> chaves) {
					Iterator<Map.Entry<String, List<String>>> it = mapaChaves.entrySet().iterator();
					while (it.hasNext()) {
						Entry<String, List<String>> entry = it.next();
						String valor = chaves.get(entry.getKey());
						List<String> lista = entry.getValue();
						lista.add(valor);
					}
				}

				private Map<String, List<String>> criar(Map<String, String> chaves) {
					Iterator<Map.Entry<String, String>> it = chaves.entrySet().iterator();
					Map<String, List<String>> mapa = new HashMap<>();
					while (it.hasNext()) {
						Entry<String, String> entry = it.next();
						mapa.put(entry.getKey(), criarArrayList(entry.getValue()));
					}
					return mapa;
				}

				private List<String> criarArrayList(String string) {
					List<String> lista = new ArrayList<>();
					lista.add(string);
					return lista;
				}
			}
		}

		private class ButtonFuncoes extends ButtonPopup {
			private static final long serialVersionUID = 1L;

			private ButtonFuncoes() {
				super("label.funcoes", Icones.SOMA);
				MenuItem maximo = new MenuItem(new MinimoMaximoAcao(false));
				MenuItem minimo = new MenuItem(new MinimoMaximoAcao(true));
				maximo.setToolTipText(ObjetoMensagens.getString("msg.maximo_minimo"));
				minimo.setToolTipText(ObjetoMensagens.getString("msg.maximo_minimo"));
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
					Conexao conexao = getConexao();
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
								+ objeto.getTabelaEsquema(conexao) + ")");
					} else {
						txtComplemento.setText("AND " + chaves[0] + " = (SELECT MAX(" + chaves[0] + ") FROM "
								+ objeto.getTabelaEsquema(conexao) + ")");
					}
					actionListenerInner.actionPerformed(null);
				}
			}

			private class TotalizarRegistrosAcao extends Action {
				private static final long serialVersionUID = 1L;
				private final boolean complemento;

				private TotalizarRegistrosAcao(boolean complemento) {
					super(true, complemento ? ObjetoMensagens.getString("label.total_com_filtro")
							: ObjetoMensagens.getString("label.total_sem_filtro"), false, Icones.SOMA);
					this.complemento = complemento;
				}

				@Override
				public void actionPerformed(ActionEvent e) {
					Conexao conexao = getConexao();
					if (conexao != null) {
						try {
							Connection conn = ConexaoProvedor.getConnection(conexao);
							String complementar = complemento ? txtComplemento.getText() : Constantes.VAZIO;
							String filtro = Util.estaVazio(complementar) ? Constantes.VAZIO
									: " WHERE 1=1 " + complementar;
							int i = Persistencia.getTotalRegistros(conn, objeto.getTabelaEsquema(conexao) + filtro);
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
			private Action scriptAdicaoHierAcao = actionMenu("label.meu_script_adicao_hierarq", Icones.HIERARQUIA);
			private MenuAlinhamento menuAlinhamento = new MenuAlinhamento();
			private MenuTemp menuTemp = new MenuTemp();

			private ButtonInfo() {
				super(Constantes.LABEL_METADADOS, Icones.INFO);
			}

			private void ini(Objeto objeto) {
				if (!Util.estaVazio(objeto.getScriptAdicaoHierarquico())) {
					addMenuItem(scriptAdicaoHierAcao);
				}
				addMenuItem(new AdicionarHierarquicoAcao());
				addMenuItem(true, new ChavesPrimariasAcao());
				addMenuItem(true, new ChavesExportadasAcao());
				addMenuItem(new ChavesImportadasAcao());
				addMenuItem(true, new MetaDadosAcao());
				addMenuItem(true, new InfoBancoAcao());
				addMenuItem(new EsquemaAcao());
				addMenu(true, new MenuDML());
				addMenu(true, new MenuCopiar());
				addMenu(true, menuAlinhamento);
				addMenu(true, menuTemp);
				scriptAdicaoHierAcao.setActionListener(
						e -> Util.mensagem(InternalContainer.this, objeto.getScriptAdicaoHierarquico()));
			}

			private class MenuTemp extends Menu {
				private static final long serialVersionUID = 1L;
				private Action tabelasRepetidasAcao = actionMenu("label.tabelas_repetidas");
				private Action larTitTodosAcao = actionMenu("label.largura_titulo_todos");
				private Action colunasComplAcao = actionMenu("label.colunas_complemento");
				private Action destacarColunaAcao = actionMenu("label.destacar_coluna");
				private Action corAcao = Action.actionMenu("label.cor", Icones.COR);

				private MenuTemp() {
					super("label.temp");
					addMenuItem(corAcao);
					addMenuItem(true, larTitTodosAcao);
					addMenuItem(true, destacarColunaAcao);
					addMenuItem(true, tabelasRepetidasAcao);
					addMenuItem(true, colunasComplAcao);
					larTitTodosAcao.setActionListener(e -> tabelaPersistencia.larguraTituloTodos());
					tabelasRepetidasAcao.setActionListener(e -> tabelasRepetidas());
					colunasComplAcao.setActionListener(e -> totalColunasCompl());
					destacarColunaAcao.setActionListener(e -> destacarColuna());
					corAcao.setActionListener(e -> configCor());
				}

				private void totalColunasCompl() {
					int atual = txtComplemento.getColumns();
					Object resp = Util.showInputDialog(InternalContainer.this, objeto.getId(),
							ObjetoMensagens.getString("label.colunas_complemento"), String.valueOf(atual));
					if (resp != null && !Util.estaVazio(resp.toString())) {
						try {
							int colunas = Util.getInt(resp.toString(), atual);
							txtComplemento.setColumns(colunas);
							SwingUtilities.updateComponentTreeUI(InternalContainer.this);
						} catch (Exception e) {
							LOG.log(Level.SEVERE, Constantes.ERRO, e);
						}
					}
				}

				private void tabelasRepetidas() {
					Util.mensagem(InternalContainer.this, objeto.getTabelasRepetidas().toString());
				}

				private void configCor() {
					Color cor = InternalContainer.this.getBackground();
					cor = JColorChooser.showDialog(InternalContainer.this, "Cor", cor);
					InternalContainer.this.setBackground(cor);
					SwingUtilities.updateComponentTreeUI(InternalContainer.this);
				}

				private void destacarColuna() {
					Object resp = Util.getValorInputDialog(InternalContainer.this, "label.coluna",
							Mensagens.getString("label.coluna"), Constantes.VAZIO);
					if (resp != null && !Util.estaVazio(resp.toString())) {
						destacarColunaTabela(resp.toString());
					}
				}

				private void destacarColunaTabela(String nome) {
					if (!Util.estaVazio(nome)) {
						int coluna = TabelaPersistenciaUtil.getIndiceColuna(tabelaPersistencia, nome);
						if (coluna != -1) {
							tabelaPersistencia.destacarColuna(coluna);
						}
					}
				}
			}

			private class MenuAlinhamento extends Menu {
				private static final long serialVersionUID = 1L;
				private Action somenteDireitoAcao = actionMenu("label.somente_direito", Icones.ALINHA_DIREITO);
				private Action esquerdoAcao = Action.actionMenu("label.esquerdo", Icones.ALINHA_ESQUERDO);
				private Action direitoAcao = Action.actionMenu("label.direito", Icones.ALINHA_DIREITO);
				private Action mesmaLarguraAcao = actionMenu("label.mesma_largura", Icones.LARGURA);

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
					habilitar(false);
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
				private Action umaColunaSemAcao = Action.actionMenu("label.uma_coluna_sem_aspas", null);
				private Action umaColunaComAcao = Action.actionMenu("label.uma_coluna_com_aspas", null);
				private Action transferidorAcao = Action.actionMenu("label.transferidor", null);
				private Action tabularAcao = Action.actionMenu("label.tabular", null);
				private Action htmlAcao = Action.actionMenu("label.html", null);

				private MenuCopiar() {
					super("label.copiar", Icones.TABLE2);
					setToolTipText(Mensagens.getString("label.copiar_tabela"));
					addMenuItem(htmlAcao);
					addMenuItem(true, tabularAcao);
					addMenuItem(true, transferidorAcao);
					addMenuItem(true, umaColunaSemAcao);
					addMenuItem(umaColunaComAcao);
					umaColunaSemAcao.setActionListener(e -> umaColuna(false));
					umaColunaComAcao.setActionListener(e -> umaColuna(true));
					transferidorAcao.setActionListener(e -> processar(0));
					tabularAcao.setActionListener(e -> processar(1));
					htmlAcao.setActionListener(e -> processar(2));
				}

				private void umaColuna(boolean comAspas) {
					List<Integer> indices = Util.getIndicesLinha(tabelaPersistencia);
					String string = Util.copiarColunaUnicaString(tabelaPersistencia, indices, comAspas,
							comAspas ? Mensagens.getString("label.uma_coluna_com_aspas")
									: Mensagens.getString("label.uma_coluna_sem_aspas"));
					Util.setContentTransfered(string);
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
					add(true, new MenuInnerJoin());
				}

				private class MenuInsert extends MenuPadrao3 {
					private static final long serialVersionUID = 1L;

					private MenuInsert() {
						super(Constantes.LABEL_INSERT, Icones.CRIAR);
						formularioAcao.setActionListener(e -> abrirUpdate(true));
						dialogoAcao.setActionListener(e -> abrirUpdate(false));
					}

					private void abrirUpdate(boolean abrirEmForm) {
						Conexao conexao = getConexao();
						if (conexao != null) {
							OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
							Coletor coletor = new Coletor();
							SetLista.view(objeto.getId(), tabelaPersistencia.getListaNomeColunas(true), coletor,
									InternalContainer.this);
							if (!coletor.estaVazio()) {
								String instrucao = modelo.getInsert(objeto.getPrefixoNomeTabela(), coletor);
								if (!Util.estaVazio(instrucao)) {
									updateFormDialog(abrirEmForm, conexao, instrucao, "Insert");
								}
							}
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
						Conexao conexao = getConexao();
						if (conexao != null) {
							OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
							Coletor coletor = new Coletor();
							SetLista.view(objeto.getId(), tabelaPersistencia.getListaNomeColunas(false), coletor,
									InternalContainer.this);
							if (!coletor.estaVazio()) {
								String instrucao = modelo.getUpdate(objeto.getPrefixoNomeTabela(), coletor, true);
								if (!Util.estaVazio(instrucao)) {
									updateFormDialog(abrirEmForm, conexao, instrucao, "Update");
								}
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
						Conexao conexao = getConexao();
						if (conexao != null) {
							OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
							String instrucao = modelo.getDelete(objeto.getPrefixoNomeTabela(), true);
							if (!Util.estaVazio(instrucao)) {
								updateFormDialog(abrirEmForm, conexao, instrucao, "Delete");
							}
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
						Conexao conexao = getConexao();
						if (conexao != null) {
							String instrucao = getConsulta(conexao, Constantes.VAZIO).toString();
							if (!Util.estaVazio(instrucao)) {
								selectFormDialog(abrirEmForm, conexao, instrucao, "Select");
							}
						}
					}
				}

				private class MenuInnerJoin extends Menu {
					private static final long serialVersionUID = 1L;
					private Action disponivelAcao = Action.actionMenu("label.disponivel", null);

					private MenuInnerJoin() {
						super(ObjetoMensagens.getString("label.inner_join"), false, Icones.TABELA);
						addMenuItem(disponivelAcao);
						disponivelAcao.setActionListener(e -> exibir());
					}

					private void exibir() {
						if (relacaoObjetoListener != null) {
							exibir(relacaoObjetoListener.listar(objeto));
						}
					}

					private void exibir(List<Relacao> relacoes) {
						StringBuilder sb = new StringBuilder("INNER JOIN " + objeto.getTabelaEsquema(getConexao()));
						StringBuilder bd = new StringBuilder();
						boolean quebrar = false;
						for (Relacao relacao : relacoes) {
							String frag = relacao.montarJoin();
							if (!Util.estaVazio(frag)) {
								if (bd.length() > 0) {
									bd.append(Constantes.QL);
									quebrar = true;
								}
								bd.append(frag);
							}
						}
						sb.append((quebrar ? Constantes.QL : "") + bd.toString());
						Util.mensagem(InternalContainer.this, sb.toString());
					}
				}

				private class MenuSelectColuna extends MenuPadrao3 {
					private static final long serialVersionUID = 1L;

					private MenuSelectColuna() {
						super(ObjetoMensagens.getString("label.select_colunas"), false, Icones.TABELA);
						formularioAcao.setActionListener(e -> abrirSelect(true));
						dialogoAcao.setActionListener(e -> abrirSelect(false));
					}

					private StringBuilder getConsultaColuna(Conexao conexao, String complemento) {
						String selectAlter = objeto.getSelectAlternativo();
						Coletor coletor = new Coletor();
						SetLista.view(objeto.getId(), tabelaPersistencia.getListaNomeColunas(true), coletor,
								InternalContainer.this);
						objeto.setSelectAlternativo("SELECT " + getNomeColunas(coletor));
						StringBuilder builder = new StringBuilder();
						montarSelect(conexao, complemento, builder);
						objeto.setSelectAlternativo(selectAlter);
						return builder;
					}

					private void montarSelect(Conexao conexao, String complemento, StringBuilder builder) {
						objeto.select(builder, conexao);
						objeto.where(builder, txtComplemento.getText(), complemento);
						objeto.orderBy(builder);
						Objeto.concatenar(builder, objeto.getFinalConsulta());
					}

					private String getNomeColunas(Coletor coletor) {
						if (coletor.estaVazio()) {
							return tabelaPersistencia.getNomeColunas(objeto.getApelidoParaJoins());
						}
						return getNomeColunas(coletor.getLista());
					}

					private String getNomeColunas(List<String> lista) {
						StringBuilder sb = new StringBuilder();
						for (String string : lista) {
							if (sb.length() > 0) {
								sb.append(", ");
							}
							if (!Util.estaVazio(objeto.getApelidoParaJoins())) {
								sb.append(objeto.getApelidoParaJoins() + ".");
							}
							sb.append(string);
						}
						return sb.toString();
					}

					private void abrirSelect(boolean abrirEmForm) {
						Conexao conexao = getConexao();
						if (conexao != null) {
							String instrucao = getConsultaColuna(conexao, Constantes.VAZIO).toString();
							if (!Util.estaVazio(instrucao)) {
								selectFormDialog(abrirEmForm, conexao, instrucao, "Select");
							}
						}
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
					Conexao conexao = getConexao();
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
					Conexao conexao = getConexao();
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
					super(true, ObjetoMensagens.getString("label.chaves_primarias"), false, Icones.PKEY);
				}

				@Override
				public void actionPerformed(ActionEvent e) {
					Conexao conexao = getConexao();
					if (conexao != null) {
						try {
							Connection conn = ConexaoProvedor.getConnection(conexao);
							MemoriaModelo modelo = Persistencia.criarModeloChavePrimaria(conn, conexao,
									objeto.getTabela());
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
					Conexao conexao = getConexao();
					if (conexao != null) {
						try {
							Connection conn = ConexaoProvedor.getConnection(conexao);
							MemoriaModelo modelo = Persistencia.criarModeloChavesImportadas(conn, conexao,
									objeto.getTabela());
							TabelaDialogo.criar((Frame) null, objeto.getTitle("CHAVES-IMPORTADAS"), modelo);
						} catch (Exception ex) {
							Util.stackTraceAndMessage("CHAVES-IMPORTADAS", ex, InternalContainer.this);
						}
					}
				}
			}

			private class AdicionarHierarquicoAcao extends Action {
				private static final long serialVersionUID = 1L;

				private AdicionarHierarquicoAcao() {
					super(true, ObjetoMensagens.getString("label.adicionar_hierarquico"), false, Icones.HIERARQUIA);
				}

				@Override
				public void actionPerformed(ActionEvent e) {
					if (vinculoListener != null) {
						Map<String, Object> mapaRef = new HashMap<>();
						mapaRef.put(ObjetoConstantes.ERROR, Boolean.FALSE);
						vinculoListener.adicionarHierarquico(getConexao(), objeto, mapaRef);
						processarMapaReferencia(mapaRef);
					}
				}

				private void processarMapaReferencia(Map<String, Object> mapaRef) {
					Boolean erro = (Boolean) mapaRef.get(ObjetoConstantes.ERROR);
					if (erro || mapaRef.get("pesquisa") == null || mapaRef.get("ref") == null) {
						return;
					}
					checarListaPesquisa(mapaRef);
					List<Pesquisa> pesquisas = objeto.getPesquisas();
					List<String> nomes = pesquisas.stream().map(Pesquisa::getNome).collect(Collectors.toList());
					Coletor coletor = new Coletor();
					SetLista.view(objeto.getId() + ObjetoMensagens.getString("msg.adicionar_hierarquico"), nomes,
							coletor, InternalContainer.this);
					for (Pesquisa pesquisa : pesquisas) {
						if (selecionado(pesquisa, coletor.getLista())) {
							Referencia ref = (Referencia) mapaRef.get("ref");
							objeto.addReferencia(ref);
							pesquisa.add(ref);
							buscaAuto = true;
						}
					}
				}

				private void checarListaPesquisa(Map<String, Object> mapaRef) {
					if (objeto.getPesquisas().isEmpty()) {
						Pesquisa pesquisa = (Pesquisa) mapaRef.get("pesquisa");
						objeto.getPesquisas().add(pesquisa);
						objeto.addReferencias(pesquisa.getReferencias());
						buttonPesquisa.complemento(objeto);
						buscaAuto = true;
					}
				}

				private boolean selecionado(Pesquisa pesquisa, List<String> lista) {
					for (String string : lista) {
						if (string.equals(pesquisa.getNome())) {
							return true;
						}
					}
					return false;
				}
			}

			private class ChavesExportadasAcao extends Action {
				private static final long serialVersionUID = 1L;

				private ChavesExportadasAcao() {
					super(true, "label.chaves_exportadas", Icones.KEY);
				}

				@Override
				public void actionPerformed(ActionEvent e) {
					Conexao conexao = getConexao();
					if (conexao != null) {
						try {
							Connection conn = ConexaoProvedor.getConnection(conexao);
							MemoriaModelo modelo = Persistencia.criarModeloChavesExportadas(conn, conexao,
									objeto.getTabela());
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
					super(true, Constantes.LABEL_METADADOS, null);
				}

				@Override
				public void actionPerformed(ActionEvent e) {
					Conexao conexao = getConexao();
					if (conexao != null) {
						try {
							Connection conn = ConexaoProvedor.getConnection(conexao);
							MemoriaModelo modelo = Persistencia.criarModeloMetaDados(conn, conexao, objeto.getTabela());
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
				ConsultaFormulario form = ConsultaFormulario.criar2(getFormulario(), conexao, instrucao);
				configLocationRelativeTo(form);
				form.setTitle(titulo);
				form.setVisible(true);
			} else {
				ConsultaDialogo form = ConsultaDialogo.criar2(getFormulario(), conexao, instrucao);
				configLocationRelativeTo(form);
				form.setTitle(titulo);
				form.setVisible(true);
			}
		}

		private void updateFormDialog(boolean abrirEmForm, Conexao conexao, String instrucao, String titulo) {
			if (abrirEmForm) {
				UpdateFormulario form = UpdateFormulario.criar2(getFormulario(), conexao, instrucao);
				configLocationRelativeTo(form);
				form.setTitle(titulo);
				form.setVisible(true);
			} else {
				UpdateDialogo form = UpdateDialogo.criar2(getFormulario(), conexao, instrucao);
				configLocationRelativeTo(form);
				form.setTitle(titulo);
				form.setVisible(true);
			}
		}

		private Formulario getFormulario() {
			if (componenteListener != null) {
				AtomicReference<Formulario> ref = new AtomicReference<>();
				componenteListener.getFormulario(ref);
				return ref.get();
			}
			return null;
		}
	}

	private Component getComponente() {
		Component resp = null;
		if (componenteListener != null && componenteListener.getComponente() != null) {
			resp = componenteListener.getComponente();
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
			if (dsde.getDropSuccess() && Util.confirmar(InternalContainer.this,
					ObjetoMensagens.getString("msg.fechar_origem_apos_soltar"), false)) {
				toolbar.fechar();
			}
		}
	};

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (ItemEvent.SELECTED == e.getStateChange()) {
			setConexaoModelo();
		}
	}

	private void setConexaoModelo() {
		Conexao conexao = getConexao();
		if (conexao != null) {
			setConexaoModelo(conexao);
		}
	}

	private void setConexaoModelo(Conexao conexao) {
		OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
		modelo.getModelo().setConexao(conexao);
	}

	public Conexao getConexao() {
		if (Preferencias.isDesconectado()) {
			return null;
		}
		return (Conexao) comboConexao.getSelectedItem();
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
			processar(cabecalhoFiltro == null ? Constantes.VAZIO : cabecalhoFiltro.getFiltroComplemento(), null,
					cabecalhoFiltro);
		}
	}

	public void aplicarConfig(InternalConfig config) {
		if (!Util.estaVazio(config.getConexao())) {
			Conexao conexaoSel = getConexaoSel(config.getConexao());
			if (conexaoSel != null) {
				comboConexao.setSelectedItem(conexaoSel);
			}
		}
		aplicarInternalConfig(config);
	}

	private void aplicarInternalConfig(InternalConfig config) {
		String complemento = txtComplemento.getText();
		txtComplemento.setText(config.getComplemento());
		processado.set(true);
		destacarTitulo = true;
		actionListenerInner.actionPerformed(null);
		Util.ajustar(tabelaPersistencia, config.getGraphics());
		if (!processado.get()) {
			txtComplemento.setText(complemento);
		}
	}

	private Conexao getConexaoSel(String nome) {
		for (int i = 0; i < comboConexao.getItemCount(); i++) {
			Conexao c = comboConexao.getItemAt(i);
			if (nome.equalsIgnoreCase(c.getNome())) {
				return c;
			}
		}
		return null;
	}

	public void pesquisarApos() {
		toolbar.buttonBaixar.limpar2Acao.actionPerformed(null);
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

	public String getComplementoChaves(boolean and) {
		StringBuilder sb = new StringBuilder();
		OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
		List<Integer> indices = Util.getIndicesLinha(tabelaPersistencia);
		if (!indices.isEmpty()) {
			Map<String, String> chaves = modelo.getMapaChaves(indices.get(0));
			if (chaves.size() == 1) {
				sb.append(umaChave(modelo, indices, chaves, and));
			} else if (chaves.size() > 1) {
				sb.append(multiplasChaves(modelo, indices, chaves, and));
			}
		}
		return sb.toString();
	}

	private String umaChave(OrdenacaoModelo modelo, List<Integer> indices, Map<String, String> chaves, boolean and) {
		String[] array = criarArray(chaves);
		String chave = array[0];
		StringBuilder sb = new StringBuilder((and ? "AND " : "") + chave + " IN(" + array[1]);
		for (int i = 1; i < indices.size(); i++) {
			sb.append(", ");
			chaves = modelo.getMapaChaves(indices.get(i));
			sb.append(chaves.get(chave));
		}
		sb.append(")");
		return sb.toString();
	}

	private String[] criarArray(Map<String, String> map) {
		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
		Entry<String, String> entry = it.next();
		String[] array = new String[2];
		array[0] = entry.getKey();
		array[1] = entry.getValue();
		return array;
	}

	private String multiplasChaves(OrdenacaoModelo modelo, List<Integer> indices, Map<String, String> chaves,
			boolean and) {
		StringBuilder sb = new StringBuilder();
		if (indices.size() > 1) {
			sb.append(and ? "AND (" : "(");
		} else {
			sb.append(and ? "AND " : "");
		}
		sb.append(andChaves(chaves));
		for (int i = 1; i < indices.size(); i++) {
			sb.append(" OR ");
			chaves = modelo.getMapaChaves(indices.get(i));
			sb.append(andChaves(chaves));
		}
		if (indices.size() > 1) {
			sb.append(")");
		}
		return sb.toString();
	}

	private String andChaves(Map<String, String> map) {
		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
		Entry<String, String> entry = it.next();
		StringBuilder sb = new StringBuilder("(" + entry.getKey() + " = " + entry.getValue());
		while (it.hasNext()) {
			entry = it.next();
			sb.append(" AND " + entry.getKey() + " = " + entry.getValue());
		}
		sb.append(")");
		return sb.toString();
	}

	public void atualizarFormulario() {
		Conexao conexao = getConexao();
		if (conexao != null) {
			actionListenerInner.actionPerformed(null);
		}
	}

	public void limpar2() {
		toolbar.buttonBaixar.limpar2Acao.actionPerformed(null);
	}

	public void limparOutros(InternalContainer invocador) {
		if (invocador != this) {
			limpar2();
		}
	}

	private String[] getArraySimNao() {
		String sim = Mensagens.getString("label.sim");
		String nao = Mensagens.getString("label.nao");
		return new String[] { sim, nao };
	}

	private class TabelaListener implements TabelaPersistenciaListener {
		@Override
		public void colocarColunaComMemoria(TabelaPersistencia tabela, String nome, String memoria) {
			String string = "";
			if (!Util.estaVazio(txtComplemento.getText())) {
				String[] simNao = getArraySimNao();
				String opcao = Util.getValorInputDialog(InternalContainer.this,
						ObjetoMensagens.getString(CHAVE_MSG_CONCAT_COMPLEMENTO), simNao);
				if (Util.estaVazio(opcao)) {
					return;
				}
				if (simNao[0].equals(opcao)) {
					string = txtComplemento.getText();
				}
			}
			String prefixo = getPrefixo();
			if (Util.estaVazio(prefixo)) {
				return;
			}
			String opcao = getOpcao();
			if (Util.estaVazio(opcao)) {
				return;
			}
			txtComplemento.setText(ltrim(string + prefixo + nome + getValor(opcao, memoria)));
			if (Util.confirmar(InternalContainer.this, Constantes.LABEL_EXECUTAR)) {
				actionListenerInner.actionPerformed(null);
			}
		}

		public void concatenarNomeColuna(TabelaPersistencia tabela, String nome) {
			String complemento = txtComplemento.getText();
			String prefixo = getPrefixo();
			if (Util.estaVazio(prefixo)) {
				return;
			}
			String opcao = getOpcao();
			if (Util.estaVazio(opcao)) {
				return;
			}
			txtComplemento.setText(ltrim(complemento + prefixo + nome + getValor(opcao, Constantes.VAZIO)));
		}

		public void colocarNomeColuna(TabelaPersistencia tabela, String nome) {
			String prefixo = getPrefixo();
			if (Util.estaVazio(prefixo)) {
				return;
			}
			String opcao = getOpcao();
			if (Util.estaVazio(opcao)) {
				return;
			}
			txtComplemento.setText(ltrim(prefixo + nome + getValor(opcao, Constantes.VAZIO)));
		}

		private String getPrefixo() {
			return Util.getValorInputDialog(InternalContainer.this, Mensagens.getString("label.prefixo"),
					new String[] { " AND ", " OR " });
		}

		private String getOpcao() {
			return Util.getValorInputDialog(InternalContainer.this, Mensagens.getString("label.operador"),
					new String[] { "=", "IN", "LIKE" });
		}

		private String getValor(String opcao, String string) {
			if ("=".equals(opcao)) {
				return " = " + string;
			} else if ("IN".equals(opcao)) {
				return " IN (" + string + ")";
			} else if ("LIKE".equals(opcao)) {
				return " LIKE '%" + string + "%'";
			}
			return Constantes.VAZIO;
		}

		@Override
		public void tabelaMouseClick(TabelaPersistencia tabela, int colunaClick) {
			int[] linhas = tabela.getSelectedRows();
			if (linhas != null && linhas.length > 0) {
				habilitarUpdateExcluir(linhas);
			} else {
				toolbar.habilitarUpdateExcluir(false);
				toolbar.labelTotal.limpar();
			}
			boolean link = !objeto.getReferencias().isEmpty() && vinculoListener != null;
			if (colunaClick >= 0 && linhas != null && linhas.length == 1 && link) {
				mouseClick(tabela, colunaClick);
			}
		}

		private void habilitarUpdateExcluir(int[] linhas) {
			String[] chaves = objeto.getChavesArray();
			toolbar.buttonUpdate.setEnabled(chaves.length > 0);
			toolbar.buttonUpdate.setHabilitado(linhas);
			toolbar.buttonExcluir.setEnabled(chaves.length > 0);
			toolbar.labelTotal.setText(Constantes.VAZIO + linhas.length);
		}

		private void mouseClick(TabelaPersistencia tabela, int colunaClick) {
			List<String> lista = TabelaPersistenciaUtil.getValoresLinha(tabela, colunaClick);
			if (lista.size() == 1) {
				vinculoListener.pesquisarLink(objeto.getReferencias(), lista.get(0));
			}
		}
	}

	public void pesquisarLink(Referencia referencia, String argumentos) {
		if (objeto.isLinkAuto() && argumentos != null) {
			OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
			tabelaPersistencia.clearSelection();
			selecionarRegistros(referencia, argumentos, modelo);
		}
	}

	private void selecionarRegistros(Referencia referencia, String argumentos, OrdenacaoModelo modelo) {
		int coluna = TabelaPersistenciaUtil.getIndiceColuna(tabelaPersistencia, referencia.getCampo());
		if (coluna != -1) {
			for (int i = 0; i < modelo.getRowCount(); i++) {
				if (argumentos.equals(modelo.getValueAt(i, coluna))) {
					tabelaPersistencia.addRowSelectionInterval(i, i);
					tabelaPersistencia.tornarVisivel(i, coluna);
				}
			}
			tabelaListener.tabelaMouseClick(tabelaPersistencia, -1);
		}
	}

	public void selecionarConexao(Conexao conexao) {
		comboConexao.setSelectedItem(conexao);
	}

	private void threadTitulo(String titulo) {
		if (tituloListener == null || !destacarTitulo) {
			return;
		}
		new Thread(new DestaqueTitulo(titulo)).start();
	}

	private class DestaqueTitulo implements Runnable {
		private final String original;
		private String esq = "<<<<<<";
		private String dir = ">>>>>>";
		private int indice = esq.length() - 1;
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

	public InternalListener.ConfiguraAltura getConfiguraAlturaListener() {
		return configuraAlturaListener;
	}

	public void setConfiguraAlturaListener(InternalListener.ConfiguraAltura configuraAlturaListener) {
		this.configuraAlturaListener = configuraAlturaListener;
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

	public InternalListener.Vinculo getVinculoListener() {
		return vinculoListener;
	}

	public void setVinculoListener(InternalListener.Vinculo vinculoListener) {
		this.vinculoListener = vinculoListener;
	}

	public InternalListener.RelacaoObjeto getRelacaoObjetoListener() {
		return relacaoObjetoListener;
	}

	public void setRelacaoObjetoListener(InternalListener.RelacaoObjeto relacaoObjetoListener) {
		this.relacaoObjetoListener = relacaoObjetoListener;
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

	static Icon iconePesquisa(Pesquisa pesquisa) {
		Referencia referencia = pesquisa.getReferencia();
		String iconeGrupo = referencia.getIconeGrupo();
		return Util.estaVazio(iconeGrupo) ? Icones.CONFIG2 : Imagens.getIcon(iconeGrupo);
	}

	private String ltrim(String s) {
		if (Util.estaVazio(s)) {
			return s;
		}
		int i = 0;
		while (s.charAt(i) <= ' ') {
			i++;
		}
		return s.substring(i);
	}
}