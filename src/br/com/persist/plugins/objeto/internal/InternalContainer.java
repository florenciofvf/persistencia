package br.com.persist.plugins.objeto.internal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.abstrato.DesktopAlinhamento;
import br.com.persist.abstrato.WindowHandler;
import br.com.persist.abstrato.WindowInternalHandler;
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
import br.com.persist.componente.SetLista.Config;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.icone.IconeDialogo;
import br.com.persist.icone.IconeListener;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.conexao.ConexaoProvedor;
import br.com.persist.plugins.consulta.ConsultaDialogo;
import br.com.persist.plugins.consulta.ConsultaFormulario;
import br.com.persist.plugins.fragmento.Fragmento;
import br.com.persist.plugins.fragmento.FragmentoDialogo;
import br.com.persist.plugins.fragmento.FragmentoListener;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.Biblioteca;
import br.com.persist.plugins.instrucao.pro.Processador;
import br.com.persist.plugins.metadado.Metadado;
import br.com.persist.plugins.objeto.Desktop;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.ObjetoConstantes;
import br.com.persist.plugins.objeto.ObjetoMensagens;
import br.com.persist.plugins.objeto.ObjetoPreferencia;
import br.com.persist.plugins.objeto.ObjetoUtil;
import br.com.persist.plugins.objeto.Relacao;
import br.com.persist.plugins.objeto.alter.Alternativo;
import br.com.persist.plugins.objeto.alter.AlternativoDialogo;
import br.com.persist.plugins.objeto.alter.AlternativoListener;
import br.com.persist.plugins.objeto.vinculo.Filtro;
import br.com.persist.plugins.objeto.vinculo.Instrucao;
import br.com.persist.plugins.objeto.vinculo.Pesquisa;
import br.com.persist.plugins.objeto.vinculo.Referencia;
import br.com.persist.plugins.objeto.vinculo.Vinculacao;
import br.com.persist.plugins.objeto.vinculo.VinculoHandler;
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

public class InternalContainer extends Panel implements ItemListener, Pagina, WindowHandler, WindowInternalHandler {
	private final transient ActionListenerInner actionListenerInner = new ActionListenerInner();
	private final TabelaPersistencia tabelaPersistencia = new TabelaPersistencia();
	private transient InternalListener.ConfiguraAltura configuraAlturaListener;
	private final Button btnArrasto = new Button(Action.actionIconDestacar());
	private static final String LABEL_NOME_PESQUISA = "label.nome_pesquisa";
	private transient TabelaListener tabelaListener = new TabelaListener();
	private transient InternalListener.RelacaoObjeto relacaoObjetoListener;
	private transient InternalListener.Visibilidade visibilidadeListener;
	private final TxtComplemento txtComplemento = new TxtComplemento();
	private transient InternalListener.Alinhamento alinhamentoListener;
	private transient InternalListener.Componente componenteListener;
	private Panel panelAguardando = new Panel(new GridBagLayout());
	private transient InternalListener.Dimensao dimensaoListener;
	private final AtomicBoolean processado = new AtomicBoolean();
	private transient InternalListener.Vinculo vinculoListener;
	private transient InternalListener.Largura larguraListener;
	private transient InternalListener.Selecao selecaoListener;
	private transient InternalListener.Titulo tituloListener;
	private static final Logger LOG = Logger.getGlobal();
	private static final String DESCRICAO = "DESCRICAO";
	private ScrollPane scrollPane = new ScrollPane();
	private static final long serialVersionUID = 1L;
	private transient InternalConfig internalConfig;
	private final JComboBox<Conexao> comboConexao;
	private final Toolbar toolbar = new Toolbar();
	private CabecalhoColuna cabecalhoFiltro;
	private final transient Objeto objeto;
	private boolean destacarTitulo;
	private String ultimaConsulta;
	private boolean buscaAuto;
	private int contadorAuto;

	public InternalContainer(Janela janela, Conexao padrao, Objeto objeto, boolean buscaAuto) {
		tabelaPersistencia.setTabelaPersistenciaListener(tabelaListener);
		txtComplemento.addMouseListener(mouseComplementoListener);
		comboConexao = ConexaoProvedor.criarComboConexao(padrao);
		txtComplemento.setText(objeto.getComplemento());
		comboConexao.addItemListener(this);
		configuracaoDinamica(objeto);
		toolbar.ini(janela, objeto);
		this.buscaAuto = buscaAuto;
		this.objeto = objeto;
		montarLayout();
		configurar();
	}

	public void processar(Graphics g) {
		processar("", g, null, null);
	}

	protected void atualizar() {
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

	static Action acaoMenu(String chave, Icon icon) {
		return Action.acaoMenu(ObjetoMensagens.getString(chave), icon);
	}

	static Action acaoMenu(String chave) {
		return acaoMenu(chave, null);
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
		txtComplemento.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
					getConexao();
					actionListenerInner.actionPerformed(null);
					e.consume();
				}
			}
		});
		txtComplemento.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (txtComplemento.ajustarAltura() && configuraAlturaListener != null) {
					configuraAlturaListener.configurarAltura(getTotalRegistros(), false, false);
				}
			}
		});
	}

	private class TxtComplemento extends JTextArea {
		private static final long serialVersionUID = 1L;
		private int ultimaAltura;

		private boolean ajustarAltura() {
			int altura = getHeight();
			if (ultimaAltura == 0) {
				ultimaAltura = altura;
				return false;
			}
			boolean resp = ultimaAltura != altura;
			ultimaAltura = altura;
			return resp;
		}

		@Override
		public void setText(String string) {
			super.setText(Util.ltrim(string));
		}

		private void setTextAnd(String campo, String string) {
			setText(objeto.comApelido("AND", campo) + string);
		}

		private void focus() {
			SwingUtilities.invokeLater(this::requestFocus);
		}
	}

	public int getAlturaToolbar() {
		return toolbar.getHeight();
	}

	public int getAlturaTableRegistro() {
		return tabelaPersistencia.getRowHeight();
	}

	public int getTotalRegistros() {
		return tabelaPersistencia.getModel().getRowCount();
	}

	public int getAlturaTableHeader() {
		return tabelaPersistencia.getTableHeader().getHeight();
	}

	public boolean scrollVisivel() {
		return tabelaPersistencia.getPreferredSize().width > scrollPane.getViewport().getWidth();
	}

	public void processar(String complemento, Graphics g, CabecalhoColuna cabecalho, String consultaAlter) {
		antesProcessar();
		if (Preferencias.isDesconectado()) {
			toolbar.exceptionEnable(Constantes.DESCONECTADO);
			processado.set(false);
			return;
		}
		Conexao conexao = getConexao();
		if (conexao != null) {
			if (continuar(complemento, conexao)) {
				processar(complemento, g, cabecalho, conexao, consultaAlter);
			} else {
				processado.set(false);
			}
		}
	}

	private void processar(String complemento, Graphics g, CabecalhoColuna cabecalho, Conexao conexao,
			String consultaAlter) {
		StringBuilder consulta = !Util.estaVazio(consultaAlter) ? new StringBuilder(consultaAlter)
				: getConsulta(conexao, complemento);
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
			destacarColunas();
			larguraRotulos();
		} catch (Exception ex) {
			mensagemException(ex);
		}
		if (objeto.isBuscaAutoTemp()) {
			objeto.setBuscaAutoTemp(false);
			buscaAuto = true;
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
		ultimaConsulta = param.getConsulta();
		checarAtributosObjeto();
		checarScrollPane();
		return modeloOrdenacao;
	}

	private void checarAtributosObjeto() {
		if (objeto.isChaveamentoAlterado()) {
			objeto.setChaveamentoAlterado(false);
			tabelaPersistencia.setChaveamento(ObjetoUtil.criarMapaCampoNomes(objeto.getChaveamento()));
		}
		if (objeto.isMapeamentoAlterado()) {
			objeto.setMapeamentoAlterado(false);
			tabelaPersistencia.setMapeamento(ObjetoUtil.criarMapaCampoChave(objeto.getMapeamento()));
		}
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
			destacarTitulo = false;
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
		if (objeto.isSequenciasAlterado()) {
			objeto.setSequenciasAlterado(false);
			objeto.setMapaSequencias(ObjetoUtil.criarMapaSequencias(objeto.getSequencias()));
		}
		param.setMapaFuncoes(conexao.getMapaTiposFuncoes());
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
			int coluna = TabelaPersistenciaUtil.getIndiceColuna(tabelaPersistencia, referencia.getCampo(), false);
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
			visibilidadeListener.checarRedimensionamento();
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
			cabecalho, null);

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
			tableColumn.setCellRenderer(new CellRenderer());
		}
		if (coluna.isColunaInfo()) {
			tableColumn.setCellRenderer(new InternalRenderer());
		}
	}

	private void configurarAltura() {
		if (objeto.isAjusteAutoForm() && configuraAlturaListener != null) {
			configuraAlturaListener.configurarAltura(getTotalRegistros(), true, false);
		}
	}

	public void pesquisar(Conexao conexao, Referencia referencia, String argumentos) {
		if (conexao != null) {
			selecionarConexao(conexao);
			txtComplemento.setTextAnd(referencia.getCampo(), " IN (" + argumentos + ")" + referencia.getConcatenar());
			destacarTitulo = true;
			actionListenerInner.actionPerformed(null);
		} else {
			Util.mensagem(InternalContainer.this, Constantes.CONEXAO_NULA);
		}
	}

	private class Toolbar extends BarraButton {
		private Action exceptionAcao = actionIcon("label.exception", Icones.EXCEPTION);
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
		private static final long serialVersionUID = 1L;
		private transient Thread thread;
		private String msgException;

		public void ini(Janela janela, Objeto objeto) {
			super.ini(janela);
			add(btnArrasto);
			addButton(true, exceptionAcao);
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
			buttonComplemento.complemento(objeto);
			setFloatable(false);
			exceptionAcao.setActionListener(e -> exceptionMsg());
			labelTotal.modoCopiar();
		}

		private void exceptionDisable() {
			msgException = Constantes.VAZIO;
			exceptionAcao.setEnabled(false);
		}

		private void exceptionEnable(String string) {
			msgException = string;
			exceptionAcao.setEnabled(true);
		}

		private void exceptionMsg() {
			Util.mensagem(InternalContainer.this, msgException);
		}

		private void habilitarUpdateExcluir(boolean b) {
			buttonExcluir.setEnabled(b);
			buttonUpdate.setEnabled(b);
		}

		private class ButtonFragVar extends ButtonPopup {
			private Action fragmentoAcao = actionMenu(Constantes.LABEL_FRAGMENTO, Icones.FRAGMENTO);
			private Action variaveisAcao = actionMenu(Constantes.LABEL_VARIAVEIS, Icones.VAR);
			private static final long serialVersionUID = 1L;

			private ButtonFragVar() {
				super("label.util", Icones.FRAGMENTO);
				addMenuItem(fragmentoAcao);
				addMenuItem(true, variaveisAcao);
				eventos();
			}

			private void eventos() {
				fragmentoAcao.setActionListener(e -> {
					Frame frame = Util.getViewParentFrame(InternalContainer.this);
					FragmentoDialogo form = FragmentoDialogo.criar(frame, getFormulario(), fragmentoListener);
					config(frame, form);
					form.setVisible(true);
				});
				variaveisAcao.setActionListener(e -> {
					Frame frame = Util.getViewParentFrame(InternalContainer.this);
					VariavelDialogo form = VariavelDialogo.criar(frame, getFormulario(), null);
					config(frame, form);
					form.setVisible(true);
				});
			}
		}

		private class ButtonBaixar extends ButtonPopup {
			private Action limpar2Acao = actionMenu(Constantes.LABEL_LIMPAR2, Icones.NOVO);
			private Action limparOutrosAcao = acaoMenu("label.limpar_outros", Icones.NOVO);
			private Action limparAcao = actionMenu(Constantes.LABEL_LIMPAR, Icones.NOVO);
			private Action conexaoAcao = actionMenu(Constantes.LABEL_CONEXAO2);
			private Action objetoAcao = actionMenu(Constantes.LABEL_OBJETO);
			private static final long serialVersionUID = 1L;

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
				String string = Constantes.VAZIO;
				if (!Util.estaVazio(txtComplemento.getText())) {
					String[] simNao = getArraySimNao();
					String opcao = opcaoConcatenar(simNao);
					if (Util.estaVazio(opcao)) {
						return;
					}
					if (simNao[0].equals(opcao)) {
						string = txtComplemento.getText();
					}
				}
				txtComplemento.setText(Util.concatenar(string, objeto.getComplemento()));
				if (Util.confirmar(InternalContainer.this, Constantes.LABEL_EXECUTAR)) {
					actionListenerInner.actionPerformed(null);
				}
			}

			private void limpar() {
				txtComplemento.setText(Constantes.VAZIO);
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
				String string = Constantes.VAZIO;
				if (!Util.estaVazio(txtComplemento.getText())) {
					String[] simNao = getArraySimNao();
					String opcao = opcaoConcatenar(simNao);
					if (Util.estaVazio(opcao)) {
						return;
					}
					if (simNao[0].equals(opcao)) {
						string = txtComplemento.getText();
					}
				}
				txtComplemento.setText(Util.concatenar(string, filtro));
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
				Conexao conexao = getConexao();
				if (conexao == null) {
					return;
				}
				int[] linhas = tabelaPersistencia.getSelectedRows();
				if (linhas != null && linhas.length > 0 && Util.confirmaExclusao(InternalContainer.this, false)) {
					OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
					List<List<IndiceValor>> listaValores = new ArrayList<>();
					AtomicBoolean atom = null;
					if (linhas.length > 1) {
						atom = new AtomicBoolean(true);
					}
					for (int linha : linhas) {
						int excluido = modelo.excluirRegistro(linha, objeto.getPrefixoNomeTabela(), true, conexao,
								atom);
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
			private Action copiarNomeTabAcao = acaoMenu("label.copiar_nome_tabela");
			private Action copiarAcao = acaoMenu("label.copiar_complemento");
			private Action concatAcao = acaoMenu("label.baixar_concatenado");
			private Action normalAcao = acaoMenu("label.baixar_normal");
			private static final long serialVersionUID = 1L;

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

			private void complemento(Objeto objeto) {
				if (objeto != null) {
					processarFiltro(objeto);
					processarInstrucao(objeto);
				}
			}

			private void processarFiltro(Objeto objeto) {
				int i = 0;
				objeto.ordenarFiltros();
				for (Filtro f : objeto.getFiltros()) {
					if (!Util.estaVazio(f.getValor())) {
						MenuFiltro menu = new MenuFiltro(f);
						if (++i == 1) {
							addSeparator();
						}
						addMenuItem(menu);
					}
				}
			}

			private void processarInstrucao(Objeto objeto) {
				objeto.ordenarInstrucoes();
				for (Instrucao inst : objeto.getInstrucoes()) {
					if (!Util.estaVazio(inst.getValor()) && inst.isComoFiltro()) {
						MenuInstrucao menu = new MenuInstrucao(inst);
						addMenu(true, menu);
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
					String conteudo = instrucao.getValor();
					if (instrucao.isSelect()) {
						selectFormDialog(abrirEmForm, conexao, conteudo, instrucao.getNome());
					} else {
						updateFormDialog(abrirEmForm, conexao, conteudo, instrucao.getNome());
					}
				}
			}

			private class MenuFiltro extends MenuItem {
				private static final long serialVersionUID = 1L;

				private MenuFiltro(Filtro filtro) {
					super(filtro.getNome(), false, null);
					addActionListener(e -> filtrar(filtro));
				}

				private void filtrar(Filtro filtro) {
					txtComplemento.setText(filtro.getValor());
					if (Util.confirmar(InternalContainer.this, Constantes.LABEL_EXECUTAR)) {
						actionListenerInner.actionPerformed(null);
					}
				}
			}

			private void copiarNomeTabela() {
				Conexao conexao = getConexao();
				if (conexao != null && !Util.estaVazio(conexao.getEsquema())
						|| !Util.estaVazio(objeto.getApelidoParaJoins())) {
					String[] array = new String[] { objeto.getTabelaEsquema(conexao), objeto.getTabela() };
					String opcao = Util.getValorInputDialog2(InternalContainer.this, null, array);
					if (!Util.estaVazio(opcao)) {
						Util.setContentTransfered(opcao);
					}
				} else {
					Util.setContentTransfered(objeto.getTabelaEsquema(conexao));
				}
			}

			private void copiarComplemento() {
				String string = txtComplemento.getText().trim();
				Util.setContentTransfered(objeto.semApelido(string));
			}

			private void processar(boolean normal) {
				Conexao conexao = getConexao();
				if (conexao == null) {
					return;
				}
				String complemento = Util.getContentTransfered();
				if (Util.estaVazio(complemento)) {
					txtComplemento.setText(objeto.getComplemento());
				} else {
					complemento = complemento.trim();
					if (normal) {
						txtComplemento.setText(complemento);
					} else {
						String s = txtComplemento.getText().trim();
						txtComplemento.setText(Util.concatenar(s, complemento));
					}
					if (Util.confirmar(InternalContainer.this, Constantes.LABEL_EXECUTAR)) {
						actionListenerInner.actionPerformed(null);
					}
				}
			}
		}

		private class ButtonSincronizar extends ButtonPopup {
			private Action sincronizarAcao = actionMenu(Constantes.LABEL_SINCRONIZAR, Icones.SINCRONIZAR);
			private MenuItem itemAtualizarAuto = new MenuItem(
					ObjetoMensagens.getString(ObjetoConstantes.LABEL_ATUALIZAR_AUTO), false, Icones.ATUALIZAR);
			private Action atualizarAcao = Action.actionMenuAtualizar();
			private static final long serialVersionUID = 1L;

			private ButtonSincronizar() {
				super(Constantes.LABEL_ATUALIZAR, Icones.ATUALIZAR);
				addMenuItem(atualizarAcao);
				addMenuItem(true, sincronizarAcao);
				addMenuItem(true, itemAtualizarAuto);
				itemAtualizarAuto.setText(itemAtualizarAuto.getText() + "   ");
				itemAtualizarAuto.setToolTipText(ObjetoMensagens.getString("hint.item_atualizacao_auto"));
				sincronizarAcao.hint(ObjetoMensagens.getString("hint.item_sincronizacao"));
				atualizarAcao.hint(ObjetoMensagens.getString("hint.item_atualizacao"));
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

			private ButtonPesquisa() {
				super("label.buscaAuto", Icones.FIELDS);
			}

			private void complemento(Objeto objeto) {
				limparPopup();
				List<Pesquisa> pesquisas = objeto.getPesquisas();
				for (Pesquisa p : pesquisas) {
					addMenu(new MenuPesquisa(p));
				}
				setEnabled(!pesquisas.isEmpty());
			}

			private void habilitar(boolean b) {
				for (Component c : getComponentes()) {
					if (c instanceof MenuPesquisa) {
						((MenuPesquisa) c).habilitar(b);
					}
				}
			}

			private class MenuPesquisa extends MenuPadrao2 {
				private Action renomearAcao = actionMenu("label.renomear");
				private Action excluirAcao = actionMenu("label.excluir");
				private static final long serialVersionUID = 1L;
				private MenuInfo menuInfo = new MenuInfo();
				private MenuUtil menuUtil = new MenuUtil();
				private final transient Pesquisa pesquisa;

				private MenuPesquisa(Pesquisa pesquisa) {
					super(pesquisa.getNomeParaMenuItem(), false, iconePesquisa(pesquisa));
					addMenuItem(true, renomearAcao);
					addMenuItem(true, excluirAcao);
					addSeparator();
					add(menuInfo);
					addSeparator();
					add(menuUtil);
					this.pesquisa = pesquisa;
					semAspasAcao.setActionListener(e -> processar(false));
					comAspasAcao.setActionListener(e -> processar(true));
					renomearAcao.setActionListener(e -> renomear());
					excluirAcao.setActionListener(e -> excluir());
					addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							if (comAspasAcao.isEnabled()) {
								processar(true);
							}
						}
					});
				}

				private class MenuInfo extends Menu {
					private Action elementosAcao = actionMenu("label.elementos");
					private Action descricaoAcao = actionMenu("label.descricao");
					private Action consultaAcao = actionMenu("label.consulta");
					private static final long serialVersionUID = 1L;

					private MenuInfo() {
						super("label.info");
						addMenuItem(elementosAcao);
						addMenuItem(true, descricaoAcao);
						addMenuItem(true, consultaAcao);
						elementosAcao.setActionListener(e -> elementos());
						descricaoAcao.setActionListener(e -> descricao());
						consultaAcao.setActionListener(e -> consulta());
					}

					private void elementos() {
						try {
							Util.mensagem(InternalContainer.this, ObjetoUtil.getDescricao(pesquisa));
						} catch (Exception ex) {
							Util.stackTraceAndMessage(DESCRICAO, ex, InternalContainer.this);
						}
					}

					private void descricao() {
						StringBuilder sb = new StringBuilder();
						sb.append(Mensagens.getString("label.total") + ": " + pesquisa.getReferencias().size());
						sb.append(Constantes.QL + "-----------------");
						for (Referencia ref : pesquisa.getReferencias()) {
							sb.append(Constantes.QL + ref.toString2());
						}
						Util.mensagem(InternalContainer.this, sb.toString());
					}

					private void consulta() {
						try {
							StringBuilder sb = new StringBuilder();
							sb.append(pesquisa.getConsulta());
							sb.append(Constantes.QL);
							sb.append("-------------");
							sb.append(Constantes.QL2);
							sb.append(pesquisa.getConsultaReversa());
							Util.mensagem(InternalContainer.this, sb.toString());
						} catch (Exception ex) {
							Util.stackTraceAndMessage("CONSULTA", ex, InternalContainer.this);
						}
					}
				}

				private class MenuUtil extends Menu {
					private Action addLimparRestoAcao = acaoMenu("label.add_limpar_resto");
					private Action excLimparRestoAcao = acaoMenu("label.exc_limpar_resto");
					private Action vazioInvisivelAcao = acaoMenu("label.vazio_invisivel");
					private Action vazioVisivelAcao = acaoMenu("label.vazio_visivel");
					private Action iconeAcao = actionMenu("label.icone");
					private static final long serialVersionUID = 1L;

					private MenuUtil() {
						super("label.util");
						addMenuItem(addLimparRestoAcao);
						addMenuItem(excLimparRestoAcao);
						addMenuItem(true, vazioInvisivelAcao);
						addMenuItem(vazioVisivelAcao);
						addMenuItem(true, iconeAcao);
						iconeAcao.hint(ObjetoMensagens.getString("hint.pesquisa.icone.excluir"));
						addLimparRestoAcao.setActionListener(e -> processar(true));
						excLimparRestoAcao.setActionListener(e -> processar(false));
						vazioInvisivelAcao.setActionListener(e -> vazio(true));
						vazioVisivelAcao.setActionListener(e -> vazio(false));
						iconeAcao.setActionListener(e -> icone());
					}

					private void icone() {
						IconeDialogo.criar(pesquisa.getNomeParaMenuItem(), new ListenerIcone(),
								pesquisa.getReferencia().getIconeGrupo());
					}

					private class ListenerIcone implements IconeListener {
						@Override
						public void setIcone(String nome) {
							if (vinculoListener == null) {
								return;
							}
							Vinculacao vinculacao = new Vinculacao();
							try {
								vinculoListener.preencherVinculacao(vinculacao);
							} catch (Exception ex) {
								Util.stackTraceAndMessage(DESCRICAO, ex, InternalContainer.this);
								return;
							}
							Pesquisa pesq = vinculacao.getPesquisa(pesquisa);
							if (pesq != null) {
								MenuPesquisa.this.setIcon(Imagens.getIcon(nome));
								pesquisa.getReferencia().setIconeGrupo(nome);
								pesq.getReferencia().setIconeGrupo(nome);
								vinculoListener.salvarVinculacao(vinculacao);
							}
						}

						@Override
						public void limparIcone() {
							if (vinculoListener == null) {
								return;
							}
							Vinculacao vinculacao = new Vinculacao();
							try {
								vinculoListener.preencherVinculacao(vinculacao);
							} catch (Exception ex) {
								Util.stackTraceAndMessage(DESCRICAO, ex, InternalContainer.this);
								return;
							}
							Pesquisa pesq = vinculacao.getPesquisa(pesquisa);
							if (pesq != null) {
								MenuPesquisa.this.setIcon(null);
								pesquisa.getReferencia().setIconeGrupo(Constantes.VAZIO);
								pesq.getReferencia().setIconeGrupo(Constantes.VAZIO);
								vinculoListener.salvarVinculacao(vinculacao);
							}
						}
					}

					private void processar(boolean adicionar) {
						if (vinculoListener == null) {
							return;
						}
						Vinculacao vinculacao = new Vinculacao();
						try {
							vinculoListener.preencherVinculacao(vinculacao);
						} catch (Exception ex) {
							Util.stackTraceAndMessage(DESCRICAO, ex, InternalContainer.this);
							return;
						}
						Pesquisa pesq = vinculacao.getPesquisa(pesquisa);
						if (pesq != null) {
							if (adicionar && !pesq.contemLimparResto()) {
								pesquisa.addLimparResto();
								pesq.addLimparResto();
								vinculoListener.salvarVinculacao(vinculacao);
							} else if (!adicionar && pesq.contemLimparResto()) {
								pesquisa.excluirLimparResto();
								pesq.excluirLimparResto();
								vinculoListener.salvarVinculacao(vinculacao);
							}
						}
					}

					private void vazio(boolean invisivel) {
						if (vinculoListener == null) {
							return;
						}
						Vinculacao vinculacao = new Vinculacao();
						try {
							vinculoListener.preencherVinculacao(vinculacao);
						} catch (Exception ex) {
							Util.stackTraceAndMessage(DESCRICAO, ex, InternalContainer.this);
							return;
						}
						Pesquisa pesq = vinculacao.getPesquisa(pesquisa);
						if (pesq != null) {
							if (invisivel) {
								pesquisa.setVazioInvisivel();
								pesq.setVazioInvisivel();
								vinculoListener.salvarVinculacao(vinculacao);
							} else {
								pesquisa.setVazioVisivel();
								pesq.setVazioVisivel();
								vinculoListener.salvarVinculacao(vinculacao);
							}
						}
					}
				}

				private void excluir() {
					if (vinculoListener == null) {
						return;
					}
					Vinculacao vinculacao = new Vinculacao();
					try {
						vinculoListener.preencherVinculacao(vinculacao);
					} catch (Exception ex) {
						Util.stackTraceAndMessage(DESCRICAO, ex, InternalContainer.this);
						return;
					}
					Pesquisa pesq = vinculacao.getPesquisa(pesquisa);
					if (pesq != null
							&& Util.confirmar(InternalContainer.this,
									ObjetoMensagens.getString("msg.confirmar_exclusao_pesquisa", pesq.getNome()), false)
							&& vinculacao.excluir(pesq) && objeto.excluir(pesquisa)) {
						vinculoListener.salvarVinculacao(vinculacao);
						toolbar.buttonPesquisa.complemento(objeto);
					}
				}

				private void renomear() {
					if (vinculoListener == null) {
						return;
					}
					Vinculacao vinculacao = new Vinculacao();
					try {
						vinculoListener.preencherVinculacao(vinculacao);
					} catch (Exception ex) {
						Util.stackTraceAndMessage(DESCRICAO, ex, InternalContainer.this);
						return;
					}
					Pesquisa pesq = vinculacao.getPesquisa(pesquisa);
					if (pesq != null) {
						renomear(pesq, vinculacao);
					}
				}

				private void renomear(Pesquisa pesq, Vinculacao vinculacao) {
					Object resp = Util.getValorInputDialog(InternalContainer.this, "label.renomear", objeto.getId(),
							pesq.getNome());
					if (resp != null && !Util.estaVazio(resp.toString())) {
						String nomeBkp = pesquisa.getNome();
						String nome = resp.toString();
						if (nome.equalsIgnoreCase(pesquisa.getNome())) {
							return;
						}
						pesquisa.setNome(nome);
						if (vinculacao.getPesquisa(pesquisa) != null) {
							Util.mensagem(InternalContainer.this,
									ObjetoMensagens.getString("msg.nome_pesquisa_existente", nome));
							pesquisa.setNome(nomeBkp);
						} else {
							pesq.setNome(nome);
							setText(pesq.getNomeParaMenuItem());
							vinculoListener.salvarVinculacao(vinculacao);
						}
					}
				}

				private void processar(boolean apostrofes) {
					int coluna = -1;
					if (vinculoListener != null) {
						coluna = TabelaPersistenciaUtil.getIndiceColuna(tabelaPersistencia,
								pesquisa.getReferencia().getCampo(), false);
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
					vinculoListener.pesquisar(getConexao(), pesquisa,
							Util.getStringLista(lista, ", ", false, apostrofes));
					pesquisarFinal(coluna);
				}

				private void pesquisarFinal(int coluna) {
					super.habilitar(pesquisa.isProcessado());
					if (pesquisa.isProcessado()) {
						vinculoListener.pesquisarApos(objeto, pesquisa);
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
						if (configuraAlturaListener != null) {
							configuraAlturaListener.configurarAltura(getTotalRegistros(), false, false);
						}
					}
				}
			}
		}

		private class ButtonUpdate extends ButtonPopup {
			private Action dadosAcao = actionMenu("label.dados", Icones.TABELA);
			private List<MenuInstrucao> listaMenuInstrucao = new ArrayList<>();
			private MenuUpdateMul menuUpdateMul = new MenuUpdateMul();
			private MenuDeleteMul menuDeleteMul = new MenuDeleteMul();
			private MenuUpdate menuUpdate = new MenuUpdate();
			private MenuDelete menuDelete = new MenuDelete();
			private MenuInsert menuInsert = new MenuInsert();
			private static final long serialVersionUID = 1L;

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
						if (!Util.estaVazio(i.getValor()) && !i.isComoFiltro()) {
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
								InternalContainer.this, null);
						if (!coletor.estaVazio()) {
							modelo.getDados(linhas[0], sb, coletor, null);
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
								InternalContainer.this, new SetLista.Config(true, false));
						if (!coletor.estaVazio()) {
							String instrucao = modelo.getUpdate(linhas[0], objeto.getPrefixoNomeTabela(), coletor,
									false, conexao);
							instrucao += Constantes.QL + " WHERE " + getComplementoChaves(false, conexao);
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
						List<String> nomeColunas = getNomeColunas(linhas);
						SetLista.view(objeto.getId(), nomeColunas, coletor, InternalContainer.this,
								new SetLista.Config(true, false));
						if (!coletor.estaVazio()) {
							String instrucao = modelo.getUpdate(linhas[0], objeto.getPrefixoNomeTabela(), coletor, true,
									conexao);
							if (!Util.estaVazio(instrucao)) {
								updateFormDialog(abrirEmForm, conexao, instrucao, "Update");
							}
						}
					}
				}

				private List<String> getNomeColunas(int[] linhas) {
					if (tabelaPersistencia.contemCampoVazio(false, linhas[0]) && Util.confirmar(InternalContainer.this,
							ObjetoMensagens.getString("msg.somente_colunas_preenchidas"), false)) {
						return tabelaPersistencia.getListaNomeColunasPreenchidas(false, linhas[0]);
					} else {
						return tabelaPersistencia.getListaNomeColunas(false);
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
						String instrucao = modelo.getDelete(linhas[0], objeto.getPrefixoNomeTabela(), false, conexao);
						instrucao += Constantes.QL + " WHERE " + getComplementoChaves(false, conexao);
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
						String instrucao = modelo.getDelete(linhas[0], objeto.getPrefixoNomeTabela(), true, conexao);
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
							List<String> nomeColunas = getNomeColunas(linhas);
							SetLista.view(objeto.getId(), nomeColunas, coletor, InternalContainer.this,
									new SetLista.Config(true, false));
							if (!coletor.estaVazio()) {
								checarColunaInsertAlternativo(modelo, coletor);
								String instrucao = modelo.getInsert(linhas[0], objeto.getPrefixoNomeTabela(), coletor);
								if (!Util.estaVazio(instrucao)) {
									updateFormDialog(abrirEmForm, conexao, instrucao, "Insert");
								}
							}
						}
					}
				}

				private List<String> getNomeColunas(int[] linhas) {
					if (tabelaPersistencia.contemCampoVazio(true, linhas[0]) && Util.confirmar(InternalContainer.this,
							ObjetoMensagens.getString("msg.somente_colunas_preenchidas"), false)) {
						return tabelaPersistencia.getListaNomeColunasPreenchidas(true, linhas[0]);
					} else {
						return tabelaPersistencia.getListaNomeColunas(true);
					}
				}

				private void checarColunaInsertAlternativo(OrdenacaoModelo modelo, Coletor coletor) {
					for (String col : coletor.getLista()) {
						Variavel var = VariavelProvedor.getVariavel("INSERT_" + objeto.getTabela() + "_" + col);
						if (var != null) {
							Coluna coluna = modelo.getColuna(col);
							if (coluna != null) {
								coluna.setValorAlternativoInsert(var.getValor());
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
						Map<String, String> chaves = modelo.getMapaChaves(linhas[0], conexao);
						if (chaves.isEmpty() || Util.estaVazio(instrucao.getValor())) {
							return;
						}
						Map<String, List<String>> mapaChaves = criar(chaves);
						for (int i = 1; i < linhas.length; i++) {
							chaves = modelo.getMapaChaves(linhas[i], conexao);
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
			private JCheckBoxMenuItem chkExibirInstrucao = new JCheckBoxMenuItem(
					ObjetoMensagens.getString("label.exibir_instrucao"));

			private ButtonFuncoes() {
				super("label.funcoes", Icones.SOMA);
				addMenuItem(new MinimoMaximoAcao(true));
				addMenuItem(new MinimoMaximoAcao(false));
				addSeparator();
				addItem(chkExibirInstrucao);
				addMenuItem(new TotalizarRegistrosAcao(false));
				addMenuItem(new TotalizarRegistrosAcao(true));
				addMenuItem(true, new AlternativoAcao());
			}

			private class AlternativoAcao extends Action {
				private static final long serialVersionUID = 1L;

				private AlternativoAcao() {
					super(true, "label.alternativo", Icones.VAR);
				}

				@Override
				public void actionPerformed(ActionEvent e) {
					Conexao conexao = getConexao();
					if (conexao == null) {
						return;
					}
					Frame frame = Util.getViewParentFrame(InternalContainer.this);
					AlternativoDialogo form = AlternativoDialogo.criar(frame, getFormulario(), alternativoListener);
					config(frame, form);
					form.setVisible(true);
				}
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
					if (chaves.length < 1) {
						txtComplemento.setText(Constantes.VAZIO);
						return;
					}
					if (minimo) {
						txtComplemento.setText(montar(chaves, "MIN", conexao));
					} else {
						txtComplemento.setText(montar(chaves, "MAX", conexao));
					}
					actionListenerInner.actionPerformed(null);
				}

				private String montar(String[] chaves, String funcao, Conexao conexao) {
					StringBuilder sb = new StringBuilder();
					for (String chave : chaves) {
						if (sb.length() > 0) {
							sb.append(" ");
						}
						sb.append(objeto.comApelido("AND", chave));
						sb.append(" = (SELECT " + funcao + "(" + chave + ")");
						sb.append(" FROM ");
						sb.append(objeto.getTabelaEsquema(conexao) + ")");
					}
					return sb.toString();
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
							String[] array = Persistencia.getTotalRegistros(conn,
									objeto.getTabelaEsquema(conexao) + filtro);
							toolbar.labelTotal.setText(Constantes.VAZIO + array[1]);
							if (chkExibirInstrucao.isSelected()) {
								Util.mensagem(InternalContainer.this, array[0]);
							}
						} catch (Exception ex) {
							Util.stackTraceAndMessage("TOTAL", ex, InternalContainer.this);
						}
					}
				}
			}
		}

		private class ButtonInfo extends ButtonPopup {
			private Action scriptAdicaoHierAcao = acaoMenu("label.meu_script_adicao_hierarq", Icones.HIERARQUIA);
			private AdicionaHierarquicoAcao adicionaHierarquicoAcao = new AdicionaHierarquicoAcao();
			private Action checagemAcao = acaoMenu("label.checar_registro", Icones.SUCESSO);
			private MenuAlinhamento menuAlinhamento = new MenuAlinhamento();
			private static final long serialVersionUID = 1L;
			private MenuTemp menuTemp = new MenuTemp();

			private ButtonInfo() {
				super(Constantes.LABEL_METADADOS, Icones.INFO);
			}

			private void ini(Objeto objeto) {
				if (objeto.isChecarRegistro()) {
					addMenuItem(checagemAcao);
				}
				if (objeto.getPesquisaAdicaoHierarquico() != null) {
					addMenuItem(scriptAdicaoHierAcao);
				}
				addMenuItem(adicionaHierarquicoAcao);
				addMenuItem(true, new ChavesPrimariasAcao());
				addMenuItem(true, new ChavesExportadasAcao());
				addMenuItem(new ChavesImportadasAcao());
				addMenuItem(true, new MetaDadosAcao());
				addMenuItem(new InfoBancoAcao());
				addMenuItem(new EsquemaAcao());
				addMenu(true, new MenuDML());
				addMenu(true, new MenuCopiar());
				addMenu(true, menuAlinhamento);
				addMenu(true, menuTemp);
				scriptAdicaoHierAcao.setActionListener(e -> descrever(objeto));
				checagemAcao.setActionListener(e -> checarRegistro());
			}

			private void descrever(Objeto objeto) {
				if (objeto.getPesquisaAdicaoHierarquico() == null) {
					return;
				}
				try {
					String descricao = ObjetoUtil.getDescricao(objeto.getPesquisaAdicaoHierarquico());
					Util.mensagem(InternalContainer.this, descricao);
				} catch (Exception ex) {
					Util.stackTraceAndMessage(DESCRICAO, ex, InternalContainer.this);
				}
			}

			private void checarRegistro() {
				String nomeBiblio = objeto.getTabela().toLowerCase();
				Processador processador = new Processador();
				Biblioteca biblioteca = null;
				try {
					biblioteca = processador.getBiblioteca(nomeBiblio);
				} catch (Exception ex) {
					Util.stackTraceAndMessage(DESCRICAO, ex, InternalContainer.this);
					return;
				}
				Conexao conexao = getConexao();
				Connection conn = null;
				if (conexao != null) {
					try {
						conn = ConexaoProvedor.getConnection(conexao);
					} catch (Exception ex) {
						Util.stackTraceAndMessage(DESCRICAO, ex, InternalContainer.this);
						return;
					}
				}
				OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
				int[] linhas = null;
				if (modelo.getRowCount() < 1) {
					Util.mensagem(InternalContainer.this,
							ObjetoMensagens.getString("msg.nenhum_registro_para_checagem"));
					return;
				} else if (modelo.getRowCount() == 1) {
					linhas = new int[] { 0 };
				} else {
					int[] sel = tabelaPersistencia.getSelectedRows();
					if (sel == null || sel.length != 1) {
						Util.mensagem(InternalContainer.this,
								ObjetoMensagens.getString("msg.selecione_um_registro_para_checagem"));
						return;
					}
					linhas = sel;
				}
				Coletor coletor = new Coletor();
				SetLista.view(objeto.getId(), tabelaPersistencia.getListaNomeColunas(true), coletor,
						InternalContainer.this, null);
				Map<String, Object> map = modelo.getMap(linhas[0], coletor, null);
				try {
					setVariaveis(biblioteca, map, conn);
					List<Object> resp = processador.executar(nomeBiblio, "main");
					Util.mensagem(InternalContainer.this, getStringResposta(resp));
				} catch (Exception ex) {
					Util.stackTraceAndMessage(DESCRICAO, ex, InternalContainer.this);
				}
			}

			private void setVariaveis(Biblioteca biblioteca, Map<String, Object> map, Connection conn)
					throws InstrucaoException {
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					biblioteca.declararVariavel(entry.getKey(), entry.getValue());
				}
				biblioteca.declararVariavel("conexao", conn);
			}

			private String getStringResposta(List<Object> lista) {
				StringBuilder sb = new StringBuilder();
				for (Object obj : lista) {
					sb.append(obj == null ? "null\n" : obj.toString() + "\n");
				}
				return sb.toString();
			}

			private class MenuTemp extends Menu {
				private Action tabelasRepetidasAcao = acaoMenu("label.tabelas_repetidas");
				private Action larTitTodosAcao = acaoMenu("label.largura_titulo_todos");
				private Action colunasComplAcao = acaoMenu("label.colunas_complemento");
				private Action destacarColunaAcao = acaoMenu("label.destacar_coluna");
				private Action corAcao = actionMenu("label.cor", Icones.COR);
				private static final long serialVersionUID = 1L;

				private MenuTemp() {
					super("label.temp");
					addMenuItem(corAcao);
					addMenuItem(true, colunasComplAcao);
					addMenuItem(true, larTitTodosAcao);
					addMenuItem(true, tabelasRepetidasAcao);
					addMenuItem(true, destacarColunaAcao);
					larTitTodosAcao.setActionListener(e -> tabelaPersistencia.larguraTituloTodos());
					tabelasRepetidasAcao.hint(ObjetoMensagens.getString("hint.incon_link_auto"));
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
					Util.mensagem(InternalContainer.this, objeto.getInconsistencias());
				}

				private void configCor() {
					Color cor = InternalContainer.this.getBackground();
					cor = JColorChooser.showDialog(InternalContainer.this, "Cor", cor);
					InternalContainer.this.setBackground(cor);
					SwingUtilities.updateComponentTreeUI(InternalContainer.this);
				}

				private void destacarColuna() {
					Object resp = Util.getValorInputDialog(InternalContainer.this, "label.coluna",
							Mensagens.getString("label.coluna_outra_coluna"), Constantes.VAZIO);
					if (resp != null && !Util.estaVazio(resp.toString())) {
						destacarColuna(resp.toString(), true);
					}
				}

				private void destacarColuna(String string, boolean like) {
					String[] strings = string.split(",");
					for (String str : strings) {
						destacarColunaTabela(str, like);
					}
				}

				private void destacarColunaTabela(String nome, boolean like) {
					if (!Util.estaVazio(nome)) {
						int coluna = TabelaPersistenciaUtil.getIndiceColuna(tabelaPersistencia, nome.trim(), like);
						if (coluna != -1) {
							tabelaPersistencia.destacarColuna(coluna);
						}
					}
				}
			}

			private class MenuAlinhamento extends Menu {
				private Action somenteDireitoAcao = acaoMenu("label.somente_direito", Icones.ALINHA_DIREITO);
				private Action esquerdoAcao = actionMenu("label.esquerdo", Icones.ALINHA_ESQUERDO);
				private Action mesmaLarguraAcao = acaoMenu("label.mesma_largura", Icones.LARGURA);
				private Action direitoAcao = actionMenu("label.direito", Icones.ALINHA_DIREITO);
				private static final long serialVersionUID = 1L;

				private MenuAlinhamento() {
					super("label.alinhamento", Icones.LARGURA);
					addMenuItem(direitoAcao);
					addMenuItem(esquerdoAcao);
					addMenuItem(mesmaLarguraAcao);
					addMenuItem(somenteDireitoAcao);
					somenteDireitoAcao.setActionListener(e -> alinhar(DesktopAlinhamento.COMPLETAR_DIREITO));
					esquerdoAcao.setActionListener(e -> alinhar(DesktopAlinhamento.ESQUERDO));
					direitoAcao.setActionListener(e -> alinhar(DesktopAlinhamento.DIREITO));
					setToolTipText(ObjetoMensagens.getString("hint.alinhamento_internal"));
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
				private Action umaColunaSemAcao = actionMenu("label.uma_coluna_sem_aspas");
				private Action umaColunaComAcao = actionMenu("label.uma_coluna_com_aspas");
				private Action transferidorAcao = actionMenu("label.transferidor");
				private Action nomeColunasAcao = actionMenu("label.nome_colunas");
				private Action tabularAcao = actionMenu("label.tabular");
				private Action htmlAcao = actionMenu("label.html");
				private static final long serialVersionUID = 1L;

				private MenuCopiar() {
					super("label.copiar", Icones.TABLE2);
					setToolTipText(Mensagens.getString("label.copiar_tabela"));
					addMenuItem(htmlAcao);
					addMenuItem(true, tabularAcao);
					addMenuItem(true, transferidorAcao);
					addMenuItem(true, nomeColunasAcao);
					addMenuItem(true, umaColunaSemAcao);
					addMenuItem(umaColunaComAcao);
					umaColunaSemAcao.setActionListener(e -> umaColuna(false));
					umaColunaComAcao.setActionListener(e -> umaColuna(true));
					transferidorAcao.setActionListener(e -> processar(0));
					nomeColunasAcao.setActionListener(e -> nomeColunas());
					tabularAcao.setActionListener(e -> processar(1));
					htmlAcao.setActionListener(e -> processar(2));
				}

				private List<String> getNomeColunas() {
					return tabelaPersistencia.getListaNomeColunas(true);
				}

				private void umaColuna(boolean comAspas) {
					String titulo = comAspas ? Mensagens.getString("label.uma_coluna_com_aspas")
							: Mensagens.getString("label.uma_coluna_sem_aspas");
					Util.copiarColunaUnicaString(titulo, tabelaPersistencia, comAspas, getNomeColunas());
				}

				private void nomeColunas() {
					Util.copiarNomeColunas(Mensagens.getString("label.nome_colunas"), tabelaPersistencia,
							getNomeColunas());
				}

				private void processar(int tipo) {
					List<Integer> indices = Util.getIndicesLinha(tabelaPersistencia);
					TransferidorTabular transferidor = Util.criarTransferidorTabular(tabelaPersistencia,
							getNomeColunas(), indices);
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
				private Action descreverColunaAcao = acaoMenu("label.descrever_coluna", Icones.TABELA);
				private Action resumirColunaAcao = acaoMenu("label.resumir_coluna", Icones.TABELA);
				private Action ultimaConsAcao = acaoMenu("label.ultima_consulta", Icones.TABELA);
				private static final long serialVersionUID = 1L;

				private MenuDML() {
					super("label.dml", Icones.EXECUTAR);
					add(descreverColunaAcao);
					add(resumirColunaAcao);
					add(ultimaConsAcao);
					add(true, new MenuInsert(true));
					add(false, new MenuInsert(false));
					add(true, new MenuUpdate());
					add(true, new MenuDelete());
					add(true, new MenuSelect());
					add(true, new MenuSelectColuna());
					add(true, new MenuInnerJoin());
					descreverColunaAcao.setActionListener(e -> descreverColuna());
					resumirColunaAcao.setActionListener(e -> resumirColuna());
					ultimaConsAcao.setActionListener(e -> ultimaCons());
				}

				private void ultimaCons() {
					Util.mensagem(InternalContainer.this, ultimaConsulta);
				}

				private void resumirColuna() {
					StringBuilder sb = new StringBuilder(objeto.getTabela() + Constantes.QL);
					sb.append(Util.completar(Constantes.VAZIO, objeto.getTabela().length(), '-'));
					Coletor coletor = new Coletor();
					SetLista.view(objeto.getId(), tabelaPersistencia.getListaNomeColunas(true), coletor,
							InternalContainer.this, null);
					for (String string : coletor.getLista()) {
						sb.append(Constantes.QL);
						sb.append(string);
					}
					Util.mensagem(InternalContainer.this, sb.toString());
				}

				private void descreverColuna() {
					StringBuilder sb = new StringBuilder(objeto.getTabela() + Constantes.QL);
					sb.append(Util.completar(Constantes.VAZIO, objeto.getTabela().length(), '-'));
					Coletor coletor = new Coletor();
					SetLista.view(objeto.getId(), tabelaPersistencia.getListaNomeColunas(true), coletor,
							InternalContainer.this, null);
					List<Coluna> colunas = tabelaPersistencia.getColunas(coletor.getLista());
					for (Coluna coluna : colunas) {
						sb.append(Constantes.QL);
						sb.append(coluna.getDetalhe());
					}
					Util.mensagem(InternalContainer.this, sb.toString());
				}

				private class MenuInsert extends MenuPadrao3 {
					private static final long serialVersionUID = 1L;
					private final boolean camposObrigatorios;

					private MenuInsert(boolean camposObrigatorios) {
						super(camposObrigatorios ? Constantes.LABEL_INSERT_CMP_OBRIG : Constantes.LABEL_INSERT,
								Icones.CRIAR);
						formularioAcao.setActionListener(e -> abrirUpdate(true));
						dialogoAcao.setActionListener(e -> abrirUpdate(false));
						this.camposObrigatorios = camposObrigatorios;
					}

					private void abrirUpdate(boolean abrirEmForm) {
						Conexao conexao = getConexao();
						if (conexao != null) {
							OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
							Coletor coletor = new Coletor();
							SetLista.view(objeto.getId(),
									camposObrigatorios ? tabelaPersistencia.getListaNomeColunasObrigatorias()
											: tabelaPersistencia.getListaNomeColunas(true),
									coletor, InternalContainer.this, new SetLista.Config(true, false));
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
									InternalContainer.this, new SetLista.Config(true, false));
							if (!coletor.estaVazio()) {
								String instrucao = modelo.getUpdate(objeto.getPrefixoNomeTabela(), coletor, true,
										conexao);
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
							String instrucao = modelo.getDelete(objeto.getPrefixoNomeTabela(), true, conexao);
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
								selectFormDialog(abrirEmForm, conexao, instrucao);
							}
						}
					}
				}

				private class MenuInnerJoin extends Menu {
					private Action disponivelAcao = actionMenu("label.disponivel");
					private static final long serialVersionUID = 1L;

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
								InternalContainer.this, new SetLista.Config(true, false));
						if (coletor.estaVazio()) {
							return new StringBuilder();
						}
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
								selectFormDialog(abrirEmForm, conexao, instrucao);
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
							TabelaDialogo.criar(InternalContainer.this, "INFO-BANCO", modelo);
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
							TabelaDialogo.criar(InternalContainer.this, "ESQUEMA", modelo);
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
							TabelaDialogo.criar(InternalContainer.this, objeto.getTitle("CHAVE-PRIMARIA"), modelo);
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
							TabelaDialogo.criar(InternalContainer.this, objeto.getTitle("CHAVES-IMPORTADAS"), modelo);
						} catch (Exception ex) {
							Util.stackTraceAndMessage("CHAVES-IMPORTADAS", ex, InternalContainer.this);
						}
					}
				}
			}

			private class AdicionaHierarquicoAcao extends Action {
				private static final long serialVersionUID = 1L;

				private AdicionaHierarquicoAcao() {
					super(true, ObjetoMensagens.getString("label.adicionar_hierarquico2"), false, Icones.HIERARQUIA);
					setEnabled(false);
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
					if (erro || mapaRef.get(VinculoHandler.PESQUISA) == null || mapaRef.get("ref") == null) {
						return;
					}
					Vinculacao vinculacao = new Vinculacao();
					try {
						vinculoListener.preencherVinculacao(vinculacao);
					} catch (Exception ex) {
						Util.stackTraceAndMessage(DESCRICAO, ex, InternalContainer.this);
						return;
					}
					if (objeto.getPesquisas().isEmpty()) {
						processarPesquisaVazio(mapaRef, vinculacao);
					} else {
						processarPesquisa(mapaRef, vinculacao);
					}
				}

				private void processarPesquisaVazio(Map<String, Object> mapaRef, Vinculacao vinculacao) {
					Pesquisa pesquisa = (Pesquisa) mapaRef.get(VinculoHandler.PESQUISA);
					Coletor coletor = new Coletor();
					Config config = new SetLista.Config(true, true);
					config.setCriar(true);
					SetLista.view(objeto.getId() + ObjetoMensagens.getString(LABEL_NOME_PESQUISA),
							Arrays.asList(pesquisa.getNome()), coletor, InternalContainer.this, config);
					if (coletor.size() == 1) {
						adicionar(mapaRef, vinculacao, coletor.get(0));
						vinculoListener.salvarVinculacao(vinculacao);
					}
				}

				private void processarPesquisa(Map<String, Object> mapaRef, Vinculacao vinculacao) {
					List<Pesquisa> pesquisas = objeto.getPesquisas();
					List<String> nomes = pesquisas.stream().map(Pesquisa::getNome).collect(Collectors.toList());
					Coletor coletor = new Coletor();
					Config config = new SetLista.Config(true, true);
					config.setCriar(true);
					SetLista.view(objeto.getId() + ObjetoMensagens.getString(LABEL_NOME_PESQUISA), nomes, coletor,
							InternalContainer.this, config);
					if (coletor.size() == 1 && !contem(pesquisas, coletor.get(0))) {
						adicionar(mapaRef, vinculacao, coletor.get(0));
						vinculoListener.salvarVinculacao(vinculacao);
						return;
					}
					atualizarPesquisa(mapaRef, vinculacao, pesquisas, coletor);
				}

				private void atualizarPesquisa(Map<String, Object> mapaRef, Vinculacao vinculacao,
						List<Pesquisa> pesquisas, Coletor coletor) {
					AtomicBoolean atom = new AtomicBoolean(false);
					for (Pesquisa pesquisa : pesquisas) {
						if (selecionado(pesquisa, coletor.getLista())) {
							Referencia ref = (Referencia) mapaRef.get("ref");
							atualizarPesquisa(vinculacao, atom, pesquisa, ref);
							if (addInvertido(mapaRef, vinculacao)) {
								atom.set(true);
							}
							objeto.addReferencia(ref);
							pesquisa.add(ref);
							buscaAuto = true;
						}
					}
					if (atom.get()) {
						vinculoListener.salvarVinculacao(vinculacao);
					}
				}

				private void atualizarPesquisa(Vinculacao vinculacao, AtomicBoolean atom, Pesquisa pesquisa,
						Referencia ref) {
					List<Pesquisa> lista = vinculacao.getPesquisas(objeto);
					for (Pesquisa pesq : lista) {
						if (pesq.ehEquivalente(pesquisa, objeto) && pesq.add(ref)) {
							atom.set(true);
						}
					}
				}

				private void adicionar(Map<String, Object> mapaRef, Vinculacao vinculacao, String nome) {
					Pesquisa pesquisa = (Pesquisa) mapaRef.get(VinculoHandler.PESQUISA);
					if (!Util.estaVazio(nome)) {
						pesquisa.setNome(nome);
					}
					objeto.addPesquisa(pesquisa);
					objeto.addReferencias(pesquisa.getReferencias());
					vinculacao.adicionarPesquisa(pesquisa);
					buttonPesquisa.complemento(objeto);
					addInvertido(mapaRef, vinculacao);
					buscaAuto = true;
				}

				private boolean addInvertido(Map<String, Object> mapaRef, Vinculacao vinculacao) {
					Pesquisa pesquisa = (Pesquisa) mapaRef.get(ObjetoConstantes.PESQUISA_INVERTIDO);
					return vinculacao.adicionarPesquisa(pesquisa);
				}

				private boolean selecionado(Pesquisa pesquisa, List<String> lista) {
					for (String string : lista) {
						if (string.equalsIgnoreCase(pesquisa.getNome())) {
							return true;
						}
					}
					return false;
				}

				private boolean contem(List<Pesquisa> pesquisas, String string) {
					for (Pesquisa pesquisa : pesquisas) {
						if (pesquisa.getNome().equalsIgnoreCase(string)) {
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
							TabelaDialogo.criar(InternalContainer.this, objeto.getTitle("CHAVES-EXPORTADAS"), modelo);
						} catch (Exception ex) {
							Util.stackTraceAndMessage("CHAVES-EXPORTADAS", ex, InternalContainer.this);
						}
					}
				}
			}

			private class MetaDadosAcao extends Action {
				private static final long serialVersionUID = 1L;

				private MetaDadosAcao() {
					super(true, "label.info_colunas", null);
				}

				@Override
				public void actionPerformed(ActionEvent e) {
					Conexao conexao = getConexao();
					if (conexao != null) {
						try {
							Connection conn = ConexaoProvedor.getConnection(conexao);
							MemoriaModelo modelo = Persistencia.criarModeloMetaDados(conn, conexao, objeto.getTabela());
							TabelaDialogo.criar(InternalContainer.this,
									objeto.getTitle(Mensagens.getString(Constantes.LABEL_METADADOS)), modelo);
						} catch (Exception ex) {
							Util.stackTraceAndMessage("META-DADOS", ex, InternalContainer.this);
						}
					}
				}
			}
		}

		private void selectFormDialog(boolean abrirEmForm, Conexao conexao, String instrucao) {
			selectFormDialog(abrirEmForm, conexao, instrucao, "Select");
		}

		private void selectFormDialog(boolean abrirEmForm, Conexao conexao, String instrucao, String titulo) {
			if (abrirEmForm) {
				Formulario frame = getFormulario();
				ConsultaFormulario form = ConsultaFormulario.criar2(frame, conexao, instrucao);
				Formulario.posicionarJanela(frame, form);
				form.setTitle(titulo);
				form.setVisible(true);
			} else {
				Formulario frame = getFormulario();
				Component comp = Util.getViewParent(InternalContainer.this);
				ConsultaDialogo form = ConsultaDialogo.criar2(frame, conexao, instrucao);
				config2(comp, frame, form);
				form.setTitle(titulo);
				form.setVisible(true);
			}
		}

		private void updateFormDialog(boolean abrirEmForm, Conexao conexao, String instrucao, String titulo) {
			if (abrirEmForm) {
				Formulario formulario = getFormulario();
				UpdateFormulario form = UpdateFormulario.criar2(formulario, conexao, instrucao);
				Formulario.posicionarJanela(formulario, form);
				form.setTitle(titulo);
				form.setVisible(true);
			} else {
				Formulario formulario = getFormulario();
				Frame frame = Util.getViewParentFrame(InternalContainer.this);
				Component comp = Util.getViewParent(InternalContainer.this);
				UpdateDialogo form = UpdateDialogo.criar2(frame, formulario, conexao, instrucao);
				config2(comp, formulario, form);
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

		private void config2(Component c, Window parent, Window child) {
			if (c instanceof Window) {
				Util.configSizeLocation((Window) c, child, InternalContainer.this);
			} else {
				Util.configSizeLocation(parent, child, InternalContainer.this);
			}
		}
	}

	private void config(Window parent, Window child) {
		Util.configSizeLocation(parent, child, InternalContainer.this);
	}

	private transient MouseListener mouseComplementoListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() >= Constantes.DOIS) {
				Frame frame = Util.getViewParentFrame(InternalContainer.this);
				ComplementoDialogo form = ComplementoDialogo.criar(frame, complementoListener);
				config(frame, form);
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
		toolbar.exceptionDisable();
		if (Preferencias.isDesconectado()) {
			toolbar.exceptionEnable(Constantes.DESCONECTADO);
			return null;
		}
		return (Conexao) comboConexao.getSelectedItem();
	}

	private transient FragmentoListener fragmentoListener = new FragmentoListener() {
		@Override
		public void aplicarFragmento(List<Fragmento> fragmentos, boolean concatenar) {
			StringBuilder sb = new StringBuilder();
			for (Fragmento f : fragmentos) {
				if (sb.length() > 0) {
					sb.append(" ");
				}
				sb.append(f.getValor());
			}
			String complemento = txtComplemento.getText();
			if (!concatenar) {
				complemento = Constantes.VAZIO;
			} else if (!Util.estaVazio(complemento)) {
				complemento = complemento.trim();
			}
			txtComplemento.setText(Util.concatenar(complemento, sb.toString()));
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

	private transient AlternativoListener alternativoListener = new AlternativoListener() {
		@Override
		public void aplicarAlternativo(Alternativo alternativo) {
			Conexao conexao = getConexao();
			final String chave = "###";
			if (conexao == null) {
				return;
			}
			String consulta = alternativo.getValor();
			consulta = Util.replaceAll(consulta, chave + "TABELA" + chave, objeto.getTabelaEsquema(conexao));
			consulta = Util.replaceAll(consulta, chave + "ID" + chave, objeto.getChaves());
			actionListenerInner.processarConsulta(consulta);
		}

		@Override
		public List<String> getGrupoFiltro() {
			List<String> grupos = new ArrayList<>();
			Conexao conexao = getConexao();
			if (conexao != null) {
				grupos.add(conexao.getGrupo());
			}
			return grupos;
		}
	};

	protected class ActionListenerInner implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			processar(cabecalhoFiltro == null ? Constantes.VAZIO : cabecalhoFiltro.getFiltroComplemento(), null,
					cabecalhoFiltro, null);
		}

		public void processarConsulta(String consultaAlter) {
			if (!Util.estaVazio(consultaAlter)) {
				processar(Constantes.VAZIO, null, null, consultaAlter);
			}
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

	private void destacarColunas() {
		if (!Util.estaVazio(objeto.getDestacaveis())) {
			toolbar.buttonInfo.menuTemp.destacarColuna(objeto.getDestacaveis(), false);
			tabelaPersistencia.deslocarColuna(objeto.getDestacaveis());
		}
	}

	private void larguraRotulos() {
		if (objeto.isLarguraRotulos()) {
			SwingUtilities.invokeLater(tabelaPersistencia::larguraTituloTodos);
		}
	}

	public void atualizarTitulo() {
		if (tituloListener != null) {
			String titulo = getTituloAtualizado();
			tituloListener.setTitulo(titulo);
		}
	}

	public String getComplementoChaves(boolean and, Conexao conexao) {
		StringBuilder sb = new StringBuilder();
		OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
		List<Integer> indices = Util.getIndicesLinha(tabelaPersistencia);
		if (!indices.isEmpty()) {
			Map<String, String> chaves = modelo.getMapaChaves(indices.get(0), conexao);
			if (chaves.size() == 1) {
				sb.append(umaChave(modelo, indices, chaves, and, conexao));
			} else if (chaves.size() > 1) {
				sb.append(multiplasChaves(modelo, indices, chaves, and, conexao));
			}
		}
		return sb.toString();
	}

	private String umaChave(OrdenacaoModelo modelo, List<Integer> indices, Map<String, String> chaves, boolean and,
			Conexao conexao) {
		String[] array = criarArray(chaves);
		String chave = array[0];
		StringBuilder sb = new StringBuilder(and ? objeto.comApelido("AND", chave) : chave);
		sb.append(" IN(" + array[1]);
		for (int i = 1; i < indices.size(); i++) {
			sb.append(", ");
			chaves = modelo.getMapaChaves(indices.get(i), conexao);
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
			boolean and, Conexao conexao) {
		StringBuilder sb = new StringBuilder();
		if (indices.size() > 1) {
			sb.append(and ? "AND (" : "(");
		} else {
			sb.append(and ? "AND " : "");
		}
		sb.append(andChaves(chaves, and));
		for (int i = 1; i < indices.size(); i++) {
			sb.append(" OR ");
			chaves = modelo.getMapaChaves(indices.get(i), conexao);
			sb.append(andChaves(chaves, and));
		}
		if (indices.size() > 1) {
			sb.append(")");
		}
		return sb.toString();
	}

	private String andChaves(Map<String, String> map, boolean and) {
		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
		Entry<String, String> entry = it.next();
		StringBuilder sb = new StringBuilder("(" + (and ? objeto.comApelido(entry.getKey()) : entry.getKey()));
		sb.append(" = " + entry.getValue());
		while (it.hasNext()) {
			entry = it.next();
			sb.append(and ? objeto.comApelido(" AND", entry.getKey()) : " AND " + entry.getKey());
			sb.append(" = " + entry.getValue());
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

	private String opcaoConcatenar(String[] simNao) {
		return Util.getValorInputDialog2(InternalContainer.this,
				ObjetoMensagens.getString("msg.concatenar_complemento"), simNao);
	}

	private class TabelaListener implements TabelaPersistenciaListener {
		@Override
		public void selectDistinct(TabelaPersistencia tabelaPersistencia, String nome, boolean form) {
			Conexao conexao = getConexao();
			if (conexao != null) {
				String instrucao = montarInstrucaoDistinct(conexao, nome);
				if (!Util.estaVazio(instrucao)) {
					toolbar.selectFormDialog(form, conexao, instrucao);
				}
			}
		}

		private String montarInstrucaoDistinct(Conexao conexao, String nome) {
			StringBuilder sb = new StringBuilder("SELECT DISTINCT " + objeto.comApelido(nome) + " FROM ");
			sb.append(objeto.getTabelaEsquema(conexao));
			sb.append(" ORDER BY " + objeto.comApelido(nome));
			return sb.toString();
		}

		@Override
		public void selectGroupBy(TabelaPersistencia tabelaPersistencia, String nome, boolean form) {
			Conexao conexao = getConexao();
			if (conexao != null) {
				String instrucao = montarInstrucaoGroupBy(conexao, nome);
				if (!Util.estaVazio(instrucao)) {
					toolbar.selectFormDialog(form, conexao, instrucao);
				}
			}
		}

		private String montarInstrucaoGroupBy(Conexao conexao, String nome) {
			StringBuilder sb = new StringBuilder("SELECT " + objeto.comApelido(nome) + ", COUNT(*)");
			sb.append("\nFROM " + objeto.getTabelaEsquema(conexao));
			sb.append("\nWHERE " + objeto.comApelido(nome) + " IS NOT NULL");
			sb.append("\nGROUP BY " + objeto.comApelido(nome));
			sb.append("\nHAVING COUNT(*) > 1");
			return sb.toString();
		}

		private Coletor getNomePesquisa() {
			List<Pesquisa> pesquisas = objeto.getPesquisas();
			List<String> nomes = pesquisas.stream().map(Pesquisa::getNome).collect(Collectors.toList());
			Coletor coletor = new Coletor();
			Config config = new SetLista.Config(true, true);
			config.setCriar(true);
			SetLista.view(objeto.getId() + ObjetoMensagens.getString(LABEL_NOME_PESQUISA), nomes, coletor,
					InternalContainer.this, config);
			return coletor;
		}

		@Override
		public void pesquisaApartirColuna(TabelaPersistencia tabelaPersistencia, String coluna) {
			if (vinculoListener == null) {
				return;
			}
			Coletor coletor = getNomePesquisa();
			if (coletor.size() != 1) {
				return;
			}
			List<Objeto> objetos = vinculoListener.objetosComTabela();
			if (objetos.isEmpty()) {
				return;
			}
			prepararPesquisa(coluna, coletor.get(0), objetos);
		}

		private void prepararPesquisa(String coluna, String nomePesquisa, List<Objeto> objetos) {
			List<String> ids = objetos.stream().map(Objeto::getId).collect(Collectors.toList());
			Coletor coletor = new Coletor();
			SetLista.view(objeto.getId() + ObjetoMensagens.getString("label.nome_outra_tabela"), ids, coletor,
					InternalContainer.this, new SetLista.Config(true, true));
			if (coletor.size() != 1) {
				return;
			}
			String id = coletor.get(0);
			Objeto obj = get(objetos, id);
			if (obj == null) {
				return;
			}
			criarAtualizarPesquisa(coluna, nomePesquisa, obj);
		}

		private Objeto get(List<Objeto> lista, String id) {
			for (Objeto obj : lista) {
				if (obj.getId().equals(id)) {
					return obj;
				}
			}
			return null;
		}

		private void criarAtualizarPesquisa(String coluna, String nomePesquisa, Objeto objDetalhe) {
			Coletor coletor = new Coletor();
			vinculoListener.selecionarCampo(objDetalhe, coletor, InternalContainer.this);
			if (coletor.size() != 1) {
				return;
			}
			Vinculacao vinculacao = new Vinculacao();
			try {
				vinculoListener.preencherVinculacao(vinculacao);
			} catch (Exception ex) {
				Util.stackTraceAndMessage(DESCRICAO, ex, InternalContainer.this);
				return;
			}
			Pesquisa pesquisa = new Pesquisa(nomePesquisa,
					new Referencia(objeto.getGrupo(), objeto.getTabela(), coluna));
			Referencia referencia = new Referencia(objDetalhe.getGrupo(), objDetalhe.getTabela(), coletor.get(0));
			referencia.setVazioInvisivel(true);
			Pesquisa existente = objeto.getPesquisa(pesquisa);
			if (existente != null) {
				existente.add(referencia);
				objeto.addReferencia(referencia);
				atualizar(vinculacao, pesquisa, referencia, objDetalhe);
			} else {
				pesquisa.add(referencia);
				adicionar(vinculacao, pesquisa, objDetalhe);
			}
		}

		private void atualizar(Vinculacao vinculacao, Pesquisa pesquisa, Referencia referencia, Objeto objDetalhe) {
			Pesquisa existente = vinculacao.getPesquisa(pesquisa);
			if (existente != null) {
				existente.add(referencia);
				toolbar.buttonPesquisa.complemento(objeto);
				buscaAuto = true;
				processarInvertido(vinculacao, existente, objDetalhe);
				vinculoListener.salvarVinculacao(vinculacao);
			}
		}

		private void adicionar(Vinculacao vinculacao, Pesquisa pesquisa, Objeto objDetalhe) {
			if (objeto.addPesquisa(pesquisa)) {
				objeto.addReferencias(pesquisa.getReferencias());
				if (vinculacao.adicionarPesquisa(pesquisa)) {
					toolbar.buttonPesquisa.complemento(objeto);
					buscaAuto = true;
					processarInvertido(vinculacao, pesquisa, objDetalhe);
					vinculoListener.salvarVinculacao(vinculacao);
				}
			}
		}

		private void processarInvertido(Vinculacao vinculacao, Pesquisa pesquisa, Objeto objDetalhe) {
			Pesquisa invertido = pesquisa.inverter(objeto.getId(), objDetalhe);
			if (invertido != null) {
				objDetalhe.addPesquisa(invertido);
				objDetalhe.addReferencias(invertido.getReferencias());
				objeto.addReferencia(invertido.getReferencia());
				vinculacao.adicionarPesquisa(invertido);
				vinculoListener.atualizarComplemento(objDetalhe);
			}
		}

		@Override
		public void colocarColunaComMemoria(TabelaPersistencia tabela, String nome, String memoria) {
			String string = "";
			if (!Util.estaVazio(txtComplemento.getText())) {
				String[] simNao = getArraySimNao();
				String opcao = opcaoConcatenar(simNao);
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
			String comApelido = objeto.comApelido(prefixo, nome);
			txtComplemento.setText(string + comApelido + getValor(opcao, memoria));
			focus();
			if (Util.confirmar3(InternalContainer.this, Constantes.LABEL_EXECUTAR)) {
				actionListenerInner.actionPerformed(null);
			}
		}

		@Override
		public void colocarColunaComMemoriaAtalho(TabelaPersistencia tabela, String nome, String memoria) {
			txtComplemento.setText(objeto.comApelido("AND", nome) + getValor("=", memoria));
			focus();
			if (Util.confirmar3(InternalContainer.this, Constantes.LABEL_EXECUTAR)) {
				actionListenerInner.actionPerformed(null);
			}
		}

		public void colocarNomeColunaAtalho(TabelaPersistencia tabela, String nome, boolean concat, Coluna coluna) {
			String string = txtComplemento.getText();
			if (!concat) {
				string = Constantes.VAZIO;
			}
			String valor = Constantes.VAZIO;
			if (coluna != null && coluna.isValidoConsulta() && !coluna.isNumero()) {
				valor = "''";
			}
			txtComplemento.setText(Util.concatenar(string, objeto.comApelido("AND", nome), getValor("=", valor)));
			focus();
		}

		public void colocarNomeColuna(TabelaPersistencia tabela, String nome, Coluna coluna) {
			String string = Constantes.VAZIO;
			if (!Util.estaVazio(txtComplemento.getText())) {
				String[] simNao = getArraySimNao();
				String opcao = opcaoConcatenar(simNao);
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
			String valor = Constantes.VAZIO;
			if (coluna != null && coluna.isValidoConsulta() && !coluna.isNumero()) {
				valor = "''";
			}
			String comApelido = objeto.comApelido(prefixo, nome);
			txtComplemento.setText(Util.concatenar(string, comApelido, getValor(opcao, valor)));
			focus();
		}

		private void focus() {
			toolbar.requestFocus();
			txtComplemento.focus();
		}

		private String getPrefixo() {
			return Util.getValorInputDialog2(InternalContainer.this, Mensagens.getString("label.prefixo"),
					new String[] { " AND", " OR" });
		}

		private String getOpcao() {
			return Util.getValorInputDialog2(InternalContainer.this, Mensagens.getString("label.operador"),
					new String[] { "=", "IN", "LIKE", "IS NULL", "IS NOT NULL" });
		}

		private String getValor(String opcao, String string) {
			if ("=".equals(opcao)) {
				return " = " + string;
			} else if ("IN".equals(opcao)) {
				return " IN (" + string + ")";
			} else if ("LIKE".equals(opcao)) {
				if ("''".equals(string)) {
					string = Constantes.VAZIO;
				}
				return " LIKE '%" + string + "%'";
			} else if ("IS NULL".equals(opcao)) {
				return " IS NULL";
			} else if ("IS NOT NULL".equals(opcao)) {
				return " IS NOT NULL";
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

		@Override
		public void campoExportadoPara(String coluna) {
			Metadado metadado = objeto.getMetadado();
			if (metadado != null) {
				campoExportadoPara(metadado, coluna);
				return;
			}
			if (vinculoListener != null) {
				AtomicReference<Object> ref = new AtomicReference<>();
				vinculoListener.getMetadado(ref, objeto);
				if (ref.get() instanceof Metadado) {
					metadado = (Metadado) ref.get();
					objeto.setMetadado(metadado);
					campoExportadoPara(metadado, coluna);
				}
			}
		}

		private void campoExportadoPara(Metadado metadado, String coluna) {
			List<String> lista = metadado.getListaCampoExportadoPara(coluna);
			String string = Util.getStringListaSemVirgula(lista, false);
			Util.mensagem(InternalContainer.this, string);
		}

		@Override
		public void campoImportadoDe(String coluna) {
			Metadado metadado = objeto.getMetadado();
			if (metadado != null) {
				campoImportadoDe(metadado, coluna);
				return;
			}
			if (vinculoListener != null) {
				AtomicReference<Object> ref = new AtomicReference<>();
				vinculoListener.getMetadado(ref, objeto);
				if (ref.get() instanceof Metadado) {
					metadado = (Metadado) ref.get();
					objeto.setMetadado(metadado);
					campoImportadoDe(metadado, coluna);
				}
			}
		}

		private void campoImportadoDe(Metadado metadado, String coluna) {
			List<String> lista = metadado.getListaCampoImportadoDe(coluna);
			String string = Util.getStringListaSemVirgula(lista, false);
			Util.mensagem(InternalContainer.this, string);
		}
	}

	public void atualizarComplemento(Objeto obj) {
		if (obj != null && obj == objeto && !obj.getPesquisas().isEmpty()) {
			buscaAuto = true;
			toolbar.buttonPesquisa.complemento(obj);
		}
	}

	public void configuracaoDinamica(Objeto objeto) {
		tabelaPersistencia.setChaveamento(ObjetoUtil.criarMapaCampoNomes(objeto.getChaveamento()));
		tabelaPersistencia.setMapeamento(ObjetoUtil.criarMapaCampoChave(objeto.getMapeamento()));
		objeto.setMapaSequencias(ObjetoUtil.criarMapaSequencias(objeto.getSequencias()));
		tabelaPersistencia.atualizarSequencias(objeto.getMapaSequencias());
		tabelaPersistencia.setClassBiblio(objeto.getClassBiblio());
	}

	public void pesquisarLink(Referencia referencia, String argumentos) {
		if (objeto.isLinkAuto() && argumentos != null) {
			OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
			tabelaPersistencia.clearSelection();
			selecionarRegistros(referencia, argumentos, modelo);
		}
	}

	private void selecionarRegistros(Referencia referencia, String argumentos, OrdenacaoModelo modelo) {
		int coluna = TabelaPersistenciaUtil.getIndiceColuna(tabelaPersistencia, referencia.getCampo(), false);
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
		public synchronized void run() {
			while (destacarTitulo && contador < Constantes.DEZ && !Thread.currentThread().isInterrupted()) {
				try {
					destacarTitulo(original);
					wait(500);
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
				SwingUtilities.invokeLater(() -> selecaoListener.selecionar(indice % 2 == 0));
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
		toolbar.buttonInfo.adicionaHierarquicoAcao.setEnabled(vinculoListener != null);
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

	@Override
	public void invertidoNoFichario(Fichario fichario) {
		txtComplemento.ultimaAltura = 0;
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
		SwingUtilities.invokeLater(() -> processar(fichario.getGraphics()));
	}

	@Override
	public void excluindoDoFichario(Fichario fichario) {
		LOG.log(Level.FINEST, "excluindoDoFichario");
	}

	@Override
	public void windowActivatedHandler(Window window) {
		windowInternalActivatedHandler(null);
	}

	@Override
	public void windowOpenedHandler(Window window) {
		LOG.log(Level.FINEST, "windowOpenedHandler");
	}

	@Override
	public void tabActivatedHandler(Fichario fichario) {
		SwingUtilities.invokeLater(() -> Util.ajustar(tabelaPersistencia, getGraphics()));
	}

	@Override
	public void windowClosingHandler(Window window) {
		LOG.log(Level.FINEST, "windowClosingHandler");
	}

	private boolean ehTabela(InternalConfig config) {
		return config.igual(objeto);
	}

	@Override
	public void windowInternalActivatedHandler(JInternalFrame internal) {
		if (ObjetoPreferencia.isPesquisaFormInternalLazy()) {
			SwingUtilities.invokeLater(InternalContainer.this::windowInternalActivated);
		} else {
			windowInternalActivated();
		}
	}

	private void windowInternalActivated() {
		InternalConfig config = internalConfig;
		internalConfig = null;
		if (config != null && ehTabela(config)) {
			aplicarConfig(config);
		} else {
			processar(getGraphics());
		}
		Util.ajustar(tabelaPersistencia, getGraphics());
	}

	@Override
	public void windowInternalClosingHandler(JInternalFrame internal) {
		LOG.log(Level.FINEST, "windowInternalClosingHandler");
	}

	@Override
	public void windowInternalOpenedHandler(JInternalFrame internal) {
		LOG.log(Level.FINEST, "windowInternalOpenedHandler");
	}

	static Icon iconePesquisa(Pesquisa pesquisa) {
		String iconeGrupo = pesquisa.getReferencia().getIconeGrupo();
		return Util.estaVazio(iconeGrupo) ? null : Imagens.getIcon(iconeGrupo);
	}

	public InternalConfig getInternalConfig() {
		return internalConfig;
	}

	public void setInternalConfig(InternalConfig internalConfig) {
		this.internalConfig = internalConfig;
	}
}