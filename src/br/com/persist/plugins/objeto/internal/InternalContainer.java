package br.com.persist.plugins.objeto.internal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
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
import java.nio.charset.StandardCharsets;
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

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.abstrato.DesktopAlinhamento;
import br.com.persist.abstrato.WindowHandler;
import br.com.persist.abstrato.WindowInternalHandler;
import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Imagens;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.TextPool;
import br.com.persist.assistencia.TransferidorTabular;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Acao;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Button;
import br.com.persist.componente.ButtonPopup;
import br.com.persist.componente.CheckBox;
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
import br.com.persist.componente.SetListaCheck;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.icone.IconeContainer;
import br.com.persist.icone.IconeDialogo;
import br.com.persist.icone.IconeListener;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.conexao.ConexaoProvedor;
import br.com.persist.plugins.consulta.ConsultaDialogo;
import br.com.persist.plugins.consulta.ConsultaFormulario;
import br.com.persist.plugins.fragmento.Fragmento;
import br.com.persist.plugins.fragmento.FragmentoDialogo;
import br.com.persist.plugins.fragmento.FragmentoListener;
import br.com.persist.plugins.instrucao.processador.Biblioteca;
import br.com.persist.plugins.instrucao.processador.CacheBiblioteca;
import br.com.persist.plugins.instrucao.processador.Constante;
import br.com.persist.plugins.instrucao.processador.Processador;
import br.com.persist.plugins.metadado.Metadado;
import br.com.persist.plugins.metadado.MetadadoException;
import br.com.persist.plugins.objeto.Desktop;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.ObjetoConstantes;
import br.com.persist.plugins.objeto.ObjetoException;
import br.com.persist.plugins.objeto.ObjetoMensagens;
import br.com.persist.plugins.objeto.ObjetoPreferencia;
import br.com.persist.plugins.objeto.ObjetoUtil;
import br.com.persist.plugins.objeto.Relacao;
import br.com.persist.plugins.objeto.alter.Alternativo;
import br.com.persist.plugins.objeto.alter.AlternativoDialogo;
import br.com.persist.plugins.objeto.alter.AlternativoListener;
import br.com.persist.plugins.objeto.complem.ComplementoDialogo;
import br.com.persist.plugins.objeto.complem.ComplementoListener;
import br.com.persist.plugins.objeto.internal.InternalListener.Alinhamento;
import br.com.persist.plugins.objeto.internal.InternalListener.ConfiguraAlturaSemRegistros;
import br.com.persist.plugins.objeto.internal.InternalListener.Dimensao;
import br.com.persist.plugins.objeto.vinculo.Filtro;
import br.com.persist.plugins.objeto.vinculo.Instrucao;
import br.com.persist.plugins.objeto.vinculo.OrdenarArrastoDialogo;
import br.com.persist.plugins.objeto.vinculo.OrdenarListener;
import br.com.persist.plugins.objeto.vinculo.OrdenarManualDialogo;
import br.com.persist.plugins.objeto.vinculo.ParaTabela;
import br.com.persist.plugins.objeto.vinculo.Param;
import br.com.persist.plugins.objeto.vinculo.Pesquisa;
import br.com.persist.plugins.objeto.vinculo.Referencia;
import br.com.persist.plugins.objeto.vinculo.Vinculacao;
import br.com.persist.plugins.objeto.vinculo.VinculoHandler;
import br.com.persist.plugins.persistencia.ChaveValor;
import br.com.persist.plugins.persistencia.Coluna;
import br.com.persist.plugins.persistencia.IndiceValor;
import br.com.persist.plugins.persistencia.MemoriaModelo;
import br.com.persist.plugins.persistencia.OrdenacaoModelo;
import br.com.persist.plugins.persistencia.Persistencia;
import br.com.persist.plugins.persistencia.PersistenciaException;
import br.com.persist.plugins.persistencia.PersistenciaModelo;
import br.com.persist.plugins.persistencia.PersistenciaModelo.Parametros;
import br.com.persist.plugins.persistencia.Registro;
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
	private final transient Toolbar toolbar = new Toolbar();
	private static final Logger LOG = Logger.getGlobal();
	private static final String DESCRICAO = "DESCRICAO";
	private ScrollPane scrollPane = new ScrollPane();
	private static final long serialVersionUID = 1L;
	private transient InternalConfig internalConfig;
	private final JComboBox<Conexao> comboConexao;
	private CabecalhoColuna cabecalhoFiltro;
	private final transient Objeto objeto;
	static final String WHERE = " WHERE ";
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

	static Action acaoMenu(String chave, Icon icon) {
		return Action.acaoMenu(ObjetoMensagens.getString(chave), icon);
	}

	static Action acaoMenu(String chave) {
		return acaoMenu(chave, null);
	}

	public List<String> getNomeColunas() {
		return tabelaPersistencia.getListaNomeColunas(true);
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
		dragSource.createDefaultDragGestureRecognizer(btnArrasto, DnDConstants.ACTION_COPY,
				InternalContainer.this::preStartDrag);
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), Constantes.EXEC);
		getActionMap().put(Constantes.EXEC, toolbar.buttonSincronizar.atualizarAcao);
		txtComplemento.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
					getConexao();
					actionListenerInner.actionPerformed(null);
					e.consume();
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					txtComplemento.semRegistros = ConfiguraAlturaSemRegistros.SCROLL_SUL;
				} else {
					txtComplemento.checkImagem();
				}
			}
		});
		txtComplemento.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (txtComplemento.ajustarAltura() && configuraAlturaListener != null) {
					configuraAlturaListener.configurarAltura(txtComplemento.getSemRegistros(), false);
					txtComplemento.semRegistros = ConfiguraAlturaSemRegistros.SCROLL_NORTE;
				}
			}
		});
	}

	private void preStartDrag(DragGestureEvent dge) {
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
		try {
			dge.startDrag(null, new InternalTransferidor(objeto, conexao, dimension), listenerArrasto);
		} catch (AssistenciaException ex) {
			Util.mensagem(InternalContainer.this, ex.getMessage());
		}
	}

	private class TxtComplemento extends JTextArea {
		private boolean ignoreScrollRectToVisible = true;
		private static final long serialVersionUID = 1L;
		ConfiguraAlturaSemRegistros semRegistros;
		private transient Imagem imagem;
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
			if (string == null) {
				string = Constantes.VAZIO;
			}
			string = Util.ltrim(string);
			if (Util.stringWidth(InternalContainer.this, string) > toolbar.getWidth() && toolbar.getHeight() > 0) {
				imagemTxtComplemento.string = string;
				super.setText(Constantes.VAZIO);
				imagem = imagemTxtComplemento;
				setColumns(Constantes.DOIS);
			} else {
				super.setText(string);
				imagem = null;
			}
		}

		@Override
		public void scrollRectToVisible(Rectangle aRect) {
			if (ignoreScrollRectToVisible) {
				return;
			}
			super.scrollRectToVisible(aRect);
		}

		@Override
		public String getText() {
			if (imagem != null) {
				return imagem.string;
			}
			return super.getText();
		}

		private String getString(String campo, String string) {
			return objeto.comApelido("AND", campo) + string;
		}

		private void focus() {
			SwingUtilities.invokeLater(this::processFocus);
		}

		private void processFocus() {
			String string = getText();
			if (string != null && string.endsWith("''")) {
				setCaretPosition(string.length() - 1);
			}
			requestFocus();
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			if (imagem != null) {
				imagem.paint(g);
			}
		}

		void checkImagem() {
			if (imagem != null && getDocument().getLength() == 0) {
				imagem = null;
				repaint();
			}
		}

		ConfiguraAlturaSemRegistros getSemRegistros() {
			if (semRegistros == null) {
				semRegistros = ConfiguraAlturaSemRegistros.SCROLL_NORTE;
			}
			return semRegistros;
		}
	}

	private transient Imagem imagemTxtComplemento = new Imagem();

	protected class Imagem {
		final Image image;
		String string;

		Imagem() {
			image = Toolkit.getDefaultToolkit().createImage(Icones.getURL("eye"));
		}

		void paint(Graphics g) {
			g.drawImage(image, Constantes.CINCO, Constantes.QUATRO, null);
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
		if (objeto.isIgnorar()) {
			Variavel vl2 = toolbar.buttonBaixar.getVariavelLimpar2();
			if (vl2 != null) {
				txtComplemento.setText(vl2.getValor());
				consultaAlter = null;
			}
		}
		StringBuilder consulta = !Util.isEmpty(consultaAlter) ? new StringBuilder(consultaAlter)
				: getConsulta(conexao, complemento);
		try {
			Connection conn = ConexaoProvedor.getConnection(conexao);
			Parametros param = criarParametros(conn, conexao, consulta.toString());
			OrdenacaoModelo modeloOrdenacao = consultarEModeloOrdenacao(conexao, param);
			destacarPesquisado(getTituloAtualizado());
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
		int count = tabelaPersistencia.getModel().getRowCount();
		objeto.setCorTemp(count > 0 ? Color.CYAN : Color.WHITE);
		toolbar.buttonPesquisa.habilitar(count > 0 && buscaAuto);
		tabelaListener.tabelaMouseClick(tabelaPersistencia, -1);
		configurarAltura();
	}

	private OrdenacaoModelo consultarEModeloOrdenacao(Conexao conexao, Parametros param) throws PersistenciaException {
		PersistenciaModelo persistenciaModelo = Persistencia.criarPersistenciaModelo(param);
		OrdenacaoModelo modeloOrdenacao = new OrdenacaoModelo(persistenciaModelo);
		persistenciaModelo.setPrefixoNomeTabela(objeto.getPrefixoNomeTabela());
		objeto.setComplemento(txtComplemento.getText());
		configurarCompararRegistroAntes();
		tabelaPersistencia.setModel(modeloOrdenacao);
		persistenciaModelo.setConexao(conexao);
		persistenciaModelo.setComponente(this);
		ultimaConsulta = param.getConsulta();
		checarAtributosObjeto();
		checarScrollPane();
		return modeloOrdenacao;
	}

	private void configurarCompararRegistroAntes() {
		OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
		if (objeto.isCompararRegistro()) {
			tabelaPersistencia.setModeloBackup(modelo);
		} else {
			tabelaPersistencia.setModeloBackup(null);
		}
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
			Util.updateComponentTreeUI(InternalContainer.this);
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
		if (!Util.isEmpty(txtComplemento.getText()) || !Util.isEmpty(complemento)
				|| !Util.isEmpty(objeto.getFinalConsulta()) || !objeto.isCcsc()) {
			return true;
		}
		String msg = ObjetoMensagens.getString("msg.ccsc", objeto.getId() + " - " + objeto.getTabela());
		return Util.confirmar(InternalContainer.this, msg, false);
	}

	private boolean todosVazio(String filtro, Conexao conexao) {
		if (!Util.isEmpty(conexao.getFiltro())) {
			return Util.isEmpty(txtComplemento.getText()) && Util.isEmpty(filtro);
		}
		return Util.isEmpty(txtComplemento.getText()) && Util.isEmpty(filtro)
				&& Util.isEmpty(objeto.getFinalConsulta());
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
			configTableColumn(objeto.isCompararRegistro(), tableColumn, coluna);
			CabecalhoColuna cabecalhoColuna = new CabecalhoColuna(cabecalhoColunaListener, modeloOrdenacao, coluna,
					!coluna.isColunaInfo());
			if (cabecalhoColuna.equals(cabecalho)) {
				cabecalhoColuna.copiar(cabecalho);
				cabecalhoFiltro = cabecalhoColuna;
			}
			tableColumn.setHeaderRenderer(cabecalhoColuna);
		}
		OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
		OrdenacaoModelo backup = tabelaPersistencia.getModeloBackup();

		if (objeto.isCompararRegistro() && backup == null) {
			toolbar.exceptionEnable(ComparaRegistroRenderer.MODELO_DE_DADOS_ANTERIOR_NULO);
		} else if (objeto.isCompararRegistro() && modelo.getRowCount() != backup.getRowCount()) {
			toolbar.exceptionEnable(ComparaRegistroRenderer.getStringTotaisDiff(modelo, backup));
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
			boolean visivelBk = objeto.isVisivel();
			objeto.setVisivel(!invisivel);
			visibilidadeListener.setVisible(!invisivel);
			setBackground(!visivelBk && objeto.isVisivel() ? Color.GREEN : null);
			if (!visivelBk && objeto.isVisivel()) {
				Util.updateComponentTreeUI(this);
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

	private void configTableColumn(boolean cellRendererComparacao, TableColumn tableColumn, Coluna coluna) {
		if (cellRendererComparacao) {
			tableColumn.setCellRenderer(new ComparaRegistroRenderer(toolbar, coluna.getNome()));
			toolbar.exceptionDisable();
			return;
		}
		if (coluna.isChave()) {
			tableColumn.setCellRenderer(new ChaveRenderer());
		}
		if (coluna.isColunaInfo()) {
			tableColumn.setCellRenderer(new InforRenderer());
		}
	}

	private void configurarAltura() {
		if (objeto.isAjusteAutoForm() && configuraAlturaListener != null) {
			configuraAlturaListener.configurarAltura(ConfiguraAlturaSemRegistros.SCROLL_NORTE, false);
		}
	}

	public void pesquisar(Conexao conexao, Pesquisa pesquisa, Referencia referencia, Argumento argumento,
			boolean soTotal, boolean emForms) {
		if (emForms) {
			pesquisarEmMemoria(argumento, referencia);
		} else {
			if (conexao != null) {
				selecionarConexao(conexao);
				String string = null;
				if (argumento instanceof ArgumentoString) {
					string = txtComplemento.getString(referencia.getCampo(),
							" IN (" + ((ArgumentoString) argumento).getString() + ")"
									+ referencia.getConcatenar(pesquisa.getCloneParams()));
				} else if (argumento instanceof ArgumentoArray) {
					ArgumentoArray argumentoArray = (ArgumentoArray) argumento;
					String[] chavesReferencia = referencia.getChavesArray();
					if (chavesReferencia.length != argumentoArray.getQtdChaves()) {
						msgTotalChavesDiferente(argumentoArray.getQtdChaves(), chavesReferencia.length);
						return;
					}
					String filtro = montarFiltro(objeto, argumentoArray, chavesReferencia);
					string = filtro + referencia.getConcatenar(pesquisa.getCloneParams());
				}
				executarPesquisa(string, soTotal);
			} else {
				Util.mensagem(InternalContainer.this, Constantes.CONEXAO_NULA);
			}
		}
	}

	private void pesquisarEmMemoria(Argumento argumento, Referencia referencia) {
		if (argumento instanceof ArgumentoString) {
			String argumentos = ((ArgumentoString) argumento).getString();
			if (objeto.isLinkAuto() && argumentos != null) {
				OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
				String[] strings = argumentos.split(",");
				tabelaPersistencia.clearSelection();
				selecionarRegistros(referencia, strings, modelo);
			}
		} else if (argumento instanceof ArgumentoArray) {
			ArgumentoArray argumentoArray = (ArgumentoArray) argumento;
			String[] chavesReferencia = referencia.getChavesArray();
			if (chavesReferencia.length != argumentoArray.getQtdChaves()) {
				msgTotalChavesDiferente(argumentoArray.getQtdChaves(), chavesReferencia.length);
				return;
			}
			OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
			tabelaPersistencia.clearSelection();
			selecionarRegistros(chavesReferencia, argumentoArray.getValoresChaves(), modelo);
		}
	}

	private void msgTotalChavesDiferente(int totalPesq, int totalRef) {
		toolbar.exceptionEnable("TOTAL DE CHAVES DIFERENTES: Pesquisa-" + totalPesq + " Referencia-" + totalRef);
	}

	private void selecionarRegistros(Referencia referencia, String[] argumentos, OrdenacaoModelo modelo) {
		int coluna = TabelaPersistenciaUtil.getIndiceColuna(tabelaPersistencia, referencia.getCampo(), false);
		if (coluna != -1) {
			for (int i = 0; i < modelo.getRowCount(); i++) {
				for (String arg : argumentos) {
					if (arg.trim().equals(modelo.getValueAt(i, coluna))) {
						tabelaPersistencia.addRowSelectionInterval(i, i);
						tabelaPersistencia.tornarVisivel(i, coluna);
					}
				}
			}
			tabelaListener.tabelaMouseClick(tabelaPersistencia, -1);
		}
	}

	private void selecionarRegistros(String[] chavesReferencia, List<Object[]> argumentos, OrdenacaoModelo modelo) {
		int[] indicesColuna = new int[chavesReferencia.length];
		for (int i = 0; i < indicesColuna.length; i++) {
			indicesColuna[i] = TabelaPersistenciaUtil.getIndiceColuna(tabelaPersistencia, chavesReferencia[i], false);
			if (indicesColuna[i] == -1) {
				return;
			}
		}
		for (int i = 0; i < modelo.getRowCount(); i++) {
			for (Object[] arg : argumentos) {
				if (igual(arg, indicesColuna, modelo, i)) {
					tabelaPersistencia.addRowSelectionInterval(i, i);
				}
			}
		}
		tabelaListener.tabelaMouseClick(tabelaPersistencia, -1);
	}

	private boolean igual(Object[] arg, int[] indicesColuna, OrdenacaoModelo modelo, int row) {
		boolean resp = true;
		for (int indice = 0; indice < indicesColuna.length; indice++) {
			if (!arg[indice].equals(modelo.getValueAt(row, indicesColuna[indice]))) {
				resp = false;
			}
		}
		return resp;
	}

	public static String montarFiltro(Objeto objeto, ArgumentoArray argumentoArray, String[] chavesReferencia) {
		List<ChaveValor[]> listaArrayCV = listarArrayChaveValor(objeto, argumentoArray, chavesReferencia);
		StringBuilder sb = new StringBuilder();
		for (ChaveValor[] arrayCV : listaArrayCV) {
			if (sb.length() > 0) {
				sb.append(" OR ");
			}
			sb.append(FiltroUtil.termo(arrayCV));
		}
		return concat(listaArrayCV.size() > 1, "AND (") + concat(listaArrayCV.size() == 1, "AND ") + sb.toString()
				+ concat(listaArrayCV.size() > 1, ")");
	}

	private static String concat(boolean test, String string) {
		return test ? string : "";
	}

	private static List<ChaveValor[]> listarArrayChaveValor(Objeto objeto, ArgumentoArray argumentoArray,
			String[] chavesReferencia) {
		String[] colunas = new String[chavesReferencia.length];
		for (int i = 0; i < colunas.length; i++) {
			colunas[i] = objeto.comApelido(chavesReferencia[i]);
		}
		List<ChaveValor[]> resposta = new ArrayList<>();
		for (Object[] valores : argumentoArray.getValoresChaves()) {
			ChaveValor[] arrayCV = new ChaveValor[chavesReferencia.length];
			for (int i = 0; i < colunas.length; i++) {
				String chave = colunas[i];
				Object valor = valores[i];
				arrayCV[i] = new ChaveValor(chave, valor);
			}
			resposta.add(arrayCV);
		}
		return resposta;
	}

	private void executarPesquisa(String string, boolean soTotal) {
		txtComplemento.setText(string);
		if (soTotal) {
			toolbar.buttonFuncoes.totalRegistrosComFiltro();
		} else {
			actionListenerInner.actionPerformed(null);
		}
	}

	class Toolbar extends BarraButton {
		private final Button buttonExcluir = new Button(new ExcluirRegistrosAcao());
		private final ButtonSincronizar buttonSincronizar = new ButtonSincronizar();
		private final ButtonComplemento buttonComplemento = new ButtonComplemento();
		private Action exceptionAcao = actionIcon("label.exception", null);
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
			exceptionDisable();
		}

		private void complementoNoFinal() {
			int count = getComponentCount();
			if (super.getComponent(count - 1) != txtComplemento) {
				remove(txtComplemento);
				add(txtComplemento);
				Util.updateComponentTreeUI(Toolbar.this);
			}
		}

		void exceptionDisable() {
			if (exceptionAcao.isEnabled()) {
				msgException = Constantes.VAZIO;
				exceptionAcao.setEnabled(false);
				exceptionAcao.icon(null);
			}
		}

		void exceptionEnable(String string) {
			if (!exceptionAcao.isEnabled()) {
				exceptionAcao.icon(Icones.GLOBO_GIF);
				exceptionAcao.setEnabled(true);
				msgException = string;
			}
		}

		boolean contemExcecao() {
			return exceptionAcao.isEnabled();
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
				if (!Util.isEmpty(txtComplemento.getText())) {
					String[] simNao = getArraySimNao();
					String opcao = opcaoConcatenar(simNao);
					if (Util.isEmpty(opcao)) {
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
				if (conexao != null && !Util.isEmpty(conexao.getFiltro())) {
					filtro = conexao.getFiltro();
				}
				String string = Constantes.VAZIO;
				if (!Util.isEmpty(txtComplemento.getText())) {
					String[] simNao = getArraySimNao();
					String opcao = opcaoConcatenar(simNao);
					if (Util.isEmpty(opcao)) {
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
				Variavel vl2 = getVariavelLimpar2();
				if (vl2 != null) {
					txtComplemento.setText(vl2.getValor());
					actionListenerInner.actionPerformed(null);
				}
			}

			private Variavel getVariavelLimpar2() {
				boolean salvar = false;
				Variavel vl2 = VariavelProvedor.getVariavel("LIMPAR2");
				if (vl2 == null) {
					try {
						vl2 = new Variavel("LIMPAR2", "AND 1 > 2");
						VariavelProvedor.adicionar(vl2);
						salvar = true;
					} catch (ArgumentoException ex) {
						Util.mensagem(InternalContainer.this, ex.getMessage());
					}
				}
				checarSalvarVariavelProvedor(salvar);
				return vl2;
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
				try {
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
								checarListaIndiceValor(chaves);
								listaValores.add(chaves);
							}
						}
						excluirRegistros(modelo, listaValores);
					}
				} catch (ObjetoException ex) {
					Util.mensagem(InternalContainer.this, ex.getMessage());
				}
			}

			private void checarListaIndiceValor(List<IndiceValor> chaves) throws ObjetoException {
				if (chaves.isEmpty()) {
					throw new ObjetoException("chaves.isEmpty()");
				}
			}

			private void excluirRegistros(OrdenacaoModelo modelo, List<List<IndiceValor>> listaValores) {
				modelo.excluirValoresChaves(listaValores);
				modelo.iniArray();
				modelo.fireTableDataChanged();
				tabelaListener.tabelaMouseClick(tabelaPersistencia, -1);
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
					if (!Util.isEmpty(f.getValor())) {
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
					if (!Util.isEmpty(inst.getValor()) && inst.isComoFiltro()) {
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
				if (conexao != null && !Util.isEmpty(conexao.getEsquema())
						|| !Util.isEmpty(objeto.getApelidoParaJoins())) {
					String[] array = new String[] { objeto.getTabelaEsquema(conexao), objeto.getTabela() };
					String opcao = Util.getValorInputDialog2(InternalContainer.this, null, array);
					if (!Util.isEmpty(opcao)) {
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
				if (Util.isEmpty(complemento)) {
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
				List<Pesquisa> pesquisas = objeto.getPesquisas(true);
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
				private Action ordenarArrastoAcao = actionMenu("label.ordenar_arrasto", Icones.ASC_TEXTO);
				private Action ordenarManualAcao = actionMenu("label.ordenar_manual", Icones.ASC_TEXTO);
				private Action nomeIconeReferAcao = acaoMenu("label.nome_icone_apontado");
				private Action excluirElementoAcao = actionMenu("label.excluir_elemento");
				private JCheckBoxMenuItem chkPesqEmMemoria = new JCheckBoxMenuItem(
						ObjetoMensagens.getString("label.pesquisa_em_forms"));
				private JCheckBoxMenuItem chkSomenteTotal = new JCheckBoxMenuItem(
						ObjetoMensagens.getString("label.somente_total_reg"));
				private Action limparItensAcao = acaoMenu("label.limpar_itens");
				private Action nomeReferAcao = acaoMenu("label.nome_apontado");
				private Action renomearAcao = actionMenu("label.renomear");
				private Action excluirAcao = actionMenu("label.excluir");
				private static final long serialVersionUID = 1L;
				private ButtonGroup grupo = new ButtonGroup();
				private MenuInfo menuInfo = new MenuInfo();
				private MenuUtil menuUtil = new MenuUtil();
				private final transient Pesquisa pesquisa;
				private boolean destacarObjeto;

				private MenuPesquisa(Pesquisa pesquisa) {
					super(pesquisa.getNomeParaMenuItem(), false, iconePesquisa(pesquisa));
					addMenuItem(limparItensAcao);
					addItem(chkPesqEmMemoria);
					addItem(chkSomenteTotal);
					addMenuItem(true, nomeIconeReferAcao);
					addMenuItem(nomeReferAcao);
					addMenuItem(renomearAcao);
					addMenuItem(true, ordenarManualAcao);
					addMenuItem(ordenarArrastoAcao);
					addMenuItem(true, excluirElementoAcao);
					addMenuItem(excluirAcao);
					addSeparator();
					add(menuInfo);
					addSeparator();
					add(menuUtil);
					this.pesquisa = pesquisa;
					limparItensAcao.setActionListener(e -> grupo.clearSelection());
					nomeIconeReferAcao.setActionListener(e -> preNomeIconeRefer());
					excluirElementoAcao.setActionListener(e -> excluirElemento());
					ordenarArrastoAcao.setActionListener(e -> ordenarArrasto());
					ordenarManualAcao.setActionListener(e -> ordenarManual());
					semAspasAcao.setActionListener(e -> preProcessar(false));
					comAspasAcao.setActionListener(e -> preProcessar(true));
					nomeReferAcao.setActionListener(e -> nomeRefer());
					renomearAcao.setActionListener(e -> renomear());
					excluirAcao.setActionListener(e -> excluir());
					int size = pesquisa.getReferencias().size();
					nomeIconeReferAcao.setEnabled(size == 1);
					nomeReferAcao.setEnabled(size == 1);
					menuUtil.habilitar(size == 1);
					addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							if (comAspasAcao.isEnabled()) {
								preProcessar(true);
							}
						}
					});
					grupo.add(chkPesqEmMemoria);
					grupo.add(chkSomenteTotal);
				}

				private void preNomeIconeRefer() {
					try {
						nomeIconeRefer();
					} catch (AssistenciaException ex) {
						Util.mensagem(InternalContainer.this, ex.getMessage());
					}
				}

				private class MenuInfo extends Menu {
					private Action destParticAcao = acaoMenu("label.dest_particp");
					private Action elementosAcao = actionMenu("label.elementos");
					private Action descricaoAcao = actionMenu("label.descricao");
					private Action consultaAcao = actionMenu("label.consulta");
					private static final long serialVersionUID = 1L;

					private MenuInfo() {
						super("label.info");
						addMenuItem(destParticAcao);
						addMenuItem(true, elementosAcao);
						addMenuItem(true, descricaoAcao);
						addMenuItem(true, consultaAcao);
						destParticAcao.setActionListener(e -> destacarParticps());
						elementosAcao.setActionListener(e -> elementos());
						descricaoAcao.setActionListener(e -> descricao());
						consultaAcao.setActionListener(e -> consulta());
					}

					private void destacarParticps() {
						if (vinculoListener != null) {
							destacarObjeto = !destacarObjeto;
							vinculoListener.pesquisarDestacar(pesquisa, destacarObjeto);
						}
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
					private Action iconeColarAcao = actionMenu("label.colar_icone");
					private Action iconeRefAcao = acaoMenu("label.icone_apontado");
					private Action iconeAcao = actionMenu("label.icone");
					private static final long serialVersionUID = 1L;

					private MenuUtil() {
						super("label.util");
						addMenuItem(addLimparRestoAcao);
						addMenuItem(excLimparRestoAcao);
						addMenuItem(true, vazioInvisivelAcao);
						addMenuItem(vazioVisivelAcao);
						addMenuItem(true, iconeRefAcao);
						addMenuItem(iconeColarAcao);
						addMenuItem(iconeAcao);
						iconeAcao.hint(ObjetoMensagens.getString("hint.pesquisa.icone.excluir"));
						excLimparRestoAcao.setActionListener(e -> processar(false));
						addLimparRestoAcao.setActionListener(e -> processar(true));
						vazioInvisivelAcao.setActionListener(e -> vazio(true));
						vazioVisivelAcao.setActionListener(e -> vazio(false));
						iconeColarAcao.setActionListener(e -> preIconeColar());
						iconeRefAcao.setActionListener(e -> preIconeRefer());
						iconeAcao.setActionListener(e -> icone());
					}

					private void habilitar(boolean b) {
						iconeRefAcao.setEnabled(b);
					}

					private void preIconeColar() {
						try {
							iconeColar();
						} catch (AssistenciaException ex) {
							Util.mensagem(InternalContainer.this, ex.getMessage());
						}
					}

					private void preIconeRefer() {
						try {
							iconeRefer();
						} catch (AssistenciaException ex) {
							Util.mensagem(InternalContainer.this, ex.getMessage());
						}
					}

					public void iconeRefer() throws AssistenciaException {
						Referencia ref = pesquisa.get();
						if (vinculoListener == null || ref == null) {
							return;
						}
						Objeto objetoRef = vinculoListener.getObjeto(ref);
						if (objetoRef == null || Util.isEmpty(objetoRef.getIcone())) {
							return;
						}
						String nomeIcone = objetoRef.getIcone();
						Vinculacao vinculacao = new Vinculacao();
						try {
							vinculoListener.preencherVinculacao(vinculacao);
						} catch (Exception ex) {
							Util.stackTraceAndMessage(DESCRICAO, ex, InternalContainer.this);
							return;
						}
						Pesquisa pesq = vinculacao.getPesquisa(pesquisa);
						if (pesq != null) {
							configurarIcone(nomeIcone, vinculacao, pesq);
						}
					}

					private void configurarIcone(String nomeIcone, Vinculacao vinculacao, Pesquisa pesq)
							throws AssistenciaException {
						MenuPesquisa.this.setIcon(Imagens.getIcon(nomeIcone));
						pesquisa.setIconeGrupo(nomeIcone);
						pesq.setIconeGrupo(nomeIcone);
						vinculoListener.salvarVinculacao(vinculacao);
					}

					public void iconeColar() throws AssistenciaException {
						if (vinculoListener == null || Util.isEmpty(IconeContainer.getNomeIconeCopiado())) {
							return;
						}
						String nomeIcone = IconeContainer.getNomeIconeCopiado();
						Vinculacao vinculacao = new Vinculacao();
						try {
							vinculoListener.preencherVinculacao(vinculacao);
						} catch (Exception ex) {
							Util.stackTraceAndMessage(DESCRICAO, ex, InternalContainer.this);
							return;
						}
						Pesquisa pesq = vinculacao.getPesquisa(pesquisa);
						if (pesq != null) {
							configurarIcone(nomeIcone, vinculacao, pesq);
						}
					}

					private void icone() {
						IconeDialogo.criar(InternalContainer.this, pesquisa.getNomeParaMenuItem(), new ListenerIcone(),
								pesquisa.getIconeGrupo());
					}

					private class ListenerIcone implements IconeListener {
						@Override
						public void setIcone(String nome) throws AssistenciaException {
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
								configurarIcone(nome, vinculacao, pesq);
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
								pesquisa.setIconeGrupo(Constantes.VAZIO);
								pesq.setIconeGrupo(Constantes.VAZIO);
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
							try {
								if (adicionar && !pesq.contemLimparResto()) {
									pesquisa.addLimparResto();
									pesq.addLimparResto();
									vinculoListener.salvarVinculacao(vinculacao);
								} else if (!adicionar && pesq.contemLimparResto()) {
									pesquisa.excluirLimparResto();
									pesq.excluirLimparResto();
									vinculoListener.salvarVinculacao(vinculacao);
								}
							} catch (ObjetoException ex) {
								Util.stackTraceAndMessage(DESCRICAO, ex, InternalContainer.this);
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

				private void excluirElemento() {
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
						try {
							Referencia ref = selecionarRef(pesq);
							if (ref != null
									&& Util.confirmar(InternalContainer.this,
											ObjetoMensagens.getString("msg.confirmar_exclusao_elemento",
													ref.toString()),
											false)
									&& pesq.remove(ref) && pesquisa.remove(ref)) {
								vinculoListener.salvarVinculacao(vinculacao);
							}
						} catch (ObjetoException ex) {
							Util.stackTraceAndMessage(DESCRICAO, ex, InternalContainer.this);
						}
					}
				}

				private Referencia selecionarRef(Pesquisa pesquisa) {
					Coletor coletor = new Coletor();
					Config config = new SetLista.Config(true, true);
					SetLista.view(objeto.getId(), pesquisa.getListRefToString(), coletor, InternalContainer.this,
							config);
					if (coletor.size() == 1) {
						return pesquisa.get(coletor.get(0));
					}
					return null;
				}

				private void nomeIconeRefer() throws AssistenciaException {
					Referencia ref = pesquisa.get();
					if (vinculoListener == null || ref == null) {
						return;
					}
					Objeto objetoRef = vinculoListener.getObjeto(ref);
					if (objetoRef == null) {
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
					if (pesq != null && !Util.isEmpty(objetoRef.getId())) {
						String nomeBkp = pesquisa.getNome();
						String nome = objetoRef.getId();
						if (nome.equalsIgnoreCase(pesquisa.getNome())) {
							checarConfigurarIcone(objetoRef, vinculacao, pesq);
							return;
						}
						pesquisa.setNome(nome);
						if (vinculacao.getPesquisa(pesquisa) != null) {
							msgNomePesquisaExistente(nome);
							pesquisa.setNome(nomeBkp);
						} else {
							pesq.setNome(nome);
							setText(pesq.getNomeParaMenuItem());
							String nomeIcone = objetoRef.getIcone();
							if (!Util.isEmpty(nomeIcone)) {
								configurarIcone(pesq, nomeIcone);
							}
							vinculoListener.salvarVinculacao(vinculacao);
						}
					}
				}

				private void checarConfigurarIcone(Objeto objetoRef, Vinculacao vinculacao, Pesquisa pesq)
						throws AssistenciaException {
					String nomeIcone = objetoRef.getIcone();
					if (!Util.isEmpty(nomeIcone)) {
						configurarIcone(pesq, nomeIcone);
						vinculoListener.salvarVinculacao(vinculacao);
					}
				}

				private void configurarIcone(Pesquisa pesq, String nomeIcone) throws AssistenciaException {
					setIcon(Imagens.getIcon(nomeIcone));
					pesquisa.setIconeGrupo(nomeIcone);
					pesq.setIconeGrupo(nomeIcone);
				}

				private void nomeRefer() {
					Referencia ref = pesquisa.get();
					if (vinculoListener == null || ref == null) {
						return;
					}
					Objeto objetoRef = vinculoListener.getObjeto(ref);
					if (objetoRef == null) {
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
					if (pesq != null && !Util.isEmpty(objetoRef.getId())) {
						String nomeBkp = pesquisa.getNome();
						String nome = objetoRef.getId();
						if (nome.equalsIgnoreCase(pesquisa.getNome())) {
							return;
						}
						pesquisa.setNome(nome);
						if (vinculacao.getPesquisa(pesquisa) != null) {
							msgNomePesquisaExistente(nome);
							pesquisa.setNome(nomeBkp);
						} else {
							pesq.setNome(nome);
							setText(pesq.getNomeParaMenuItem());
							vinculoListener.salvarVinculacao(vinculacao);
						}
					}
				}

				private void msgNomePesquisaExistente(String nome) {
					Util.mensagem(InternalContainer.this,
							ObjetoMensagens.getString("msg.nome_pesquisa_existente", nome));
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
					if (resp != null && !Util.isEmpty(resp.toString())) {
						String nomeBkp = pesquisa.getNome();
						String nome = resp.toString();
						if (nome.equalsIgnoreCase(pesquisa.getNome())) {
							return;
						}
						pesquisa.setNome(nome);
						if (vinculacao.getPesquisa(pesquisa) != null) {
							msgNomePesquisaExistente(nome);
							pesquisa.setNome(nomeBkp);
						} else {
							pesq.setNome(nome);
							setText(pesq.getNomeParaMenuItem());
							vinculoListener.salvarVinculacao(vinculacao);
						}
					}
				}

				private void ordenarArrasto() {
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
					List<Pesquisa> pesquisas = vinculacao.getPesquisas(objeto);
					OrdenarArrastoDialogo.criar(InternalContainer.this, objeto.getId(),
							new ListenerOrdenar(pesquisas, vinculacao));
				}

				private void ordenarManual() {
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
					List<Pesquisa> pesquisas = vinculacao.getPesquisas(objeto);
					OrdenarManualDialogo.criar(InternalContainer.this, objeto.getId(),
							new ListenerOrdenar(pesquisas, vinculacao));
				}

				private class ListenerOrdenar implements OrdenarListener {
					final List<Pesquisa> pesquisas;
					final Vinculacao vinculacao;

					ListenerOrdenar(List<Pesquisa> pesquisas, Vinculacao vinculacao) {
						this.vinculacao = vinculacao;
						this.pesquisas = pesquisas;
					}

					public List<Pesquisa> getPesquisas() {
						return pesquisas;
					}

					@Override
					public void salvar() {
						if (vinculoListener != null) {
							for (Pesquisa p : pesquisas) {
								Pesquisa pesq = objeto.getPesquisa(p);
								if (p != null) {
									pesq.setOrdem(p.getOrdem());
								}
							}
							vinculoListener.salvarVinculacao(vinculacao);
							toolbar.buttonPesquisa.complemento(objeto);
						}
					}
				}

				private void preProcessar(boolean apostrofes) {
					Desktop desktop = getDesktop();
					JViewport viewPort = getViewPort(desktop);
					Point last = getViewPosition(viewPort);
					try {
						processar(apostrofes);
						if (ObjetoPreferencia.isMoverTopoFormOrigemPesquisa() && desktop != null && viewPort != null
								&& last != null && componenteListener != null) {
							final Point point = componenteListener.getComponente().getLocation();
							if (point != null) {
								Animar animar = new Animar(last.y, point.y,
										y -> setViewPosition(viewPort, new Point(last.x, y)), 5, 50);
								animar.iniciar();
							}
						}
					} catch (ObjetoException | AssistenciaException ex) {
						Util.stackTraceAndMessage(DESCRICAO, ex, InternalContainer.this);
					}
				}

				private JViewport getViewPort(Desktop desktop) {
					JScrollPane scroll = getScroll(desktop);
					return scroll != null ? scroll.getViewport() : null;
				}

				private JScrollPane getScroll(Desktop desktop) {
					Container parent = desktop;
					JScrollPane scroll = null;
					while (parent != null) {
						if (parent instanceof JScrollPane) {
							scroll = (JScrollPane) parent;
							break;
						}
						parent = parent.getParent();
					}
					return scroll;
				}

				private Point getViewPosition(JViewport viewPort) {
					return viewPort != null ? viewPort.getViewPosition() : null;
				}

				private void setViewPosition(JViewport viewPort, Point point) {
					if (viewPort != null) {
						SwingUtilities.invokeLater(() -> viewPort.setViewPosition(point));
					}
				}

				private void processar(boolean apostrofes) throws ObjetoException, AssistenciaException {
					if (vinculoListener == null) {
						return;
					}
					int coluna = TabelaPersistenciaUtil.getIndiceColuna(tabelaPersistencia,
							pesquisa.getReferencia().getCampo(), false);
					if (coluna != -1) {
						List<String> lista = TabelaPersistenciaUtil.getValoresLinha(tabelaPersistencia, coluna);
						if (lista.isEmpty()) {
							Util.mensagem(InternalContainer.this, pesquisa.getReferencia().getCampo() + " vazio.");
						} else {
							String argumentos = Util.getStringLista(lista, ", ", false, apostrofes);
							Argumento argumento = new ArgumentoString(argumentos);
							pesquisar(lista, argumento, coluna);
						}
					} else if (pesquisa.getReferencia().isChaveMultipla()) {
						String[] chavesReferencia = pesquisa.getReferencia().getChavesArray();
						Coluna[] chaves = new Coluna[chavesReferencia.length];
						for (int i = 0; i < chavesReferencia.length; i++) {
							Coluna chave = tabelaPersistencia.getColuna(chavesReferencia[i]);
							if (chave == null) {
								Util.mensagem(InternalContainer.this, chavesReferencia[i]
										+ " inexistente! Cheque a pesquisa: " + pesquisa.getNomeParaMenuItem());
								return;
							}
							chaves[i] = chave;
						}
						List<Object[]> valoresChaves = TabelaPersistenciaUtil.getValoresLinha(tabelaPersistencia,
								chaves, apostrofes);
						if (valoresChaves.isEmpty()) {
							Util.mensagem(InternalContainer.this, pesquisa.getReferencia().getCampo() + " vazio.");
						} else {
							Argumento argumento = new ArgumentoArray(valoresChaves, chavesReferencia.length);
							pesquisaArray(argumento);
						}
					}
				}

				private void pesquisar(List<String> lista, Argumento argumento, int coluna)
						throws ObjetoException, AssistenciaException {
					pesquisa.setObjeto(objeto);
					processarParams(pesquisa);
					if (!chkSomenteTotal.isSelected() && !chkPesqEmMemoria.isSelected()) {
						pesquisa.setProcessado(false);
						pesquisa.inicializarColetores(lista);
						pesquisa.validoInvisibilidade(vinculoListener.validoInvisibilidade());
					}
					vinculoListener.pesquisar(getConexao(), pesquisa, argumento, chkSomenteTotal.isSelected(),
							chkPesqEmMemoria.isSelected());
					if (!chkSomenteTotal.isSelected() && !chkPesqEmMemoria.isSelected()) {
						pesquisarFinal(coluna);
					}
				}

				private void pesquisaArray(Argumento argumento) throws ObjetoException, AssistenciaException {
					pesquisa.setObjeto(objeto);
					processarParams(pesquisa);
					if (!chkSomenteTotal.isSelected()) {
						pesquisa.setProcessado(false);
						pesquisa.validoInvisibilidade(vinculoListener.validoInvisibilidade());
					}
					vinculoListener.pesquisar(getConexao(), pesquisa, argumento, chkSomenteTotal.isSelected(),
							chkPesqEmMemoria.isSelected());
					if (!chkSomenteTotal.isSelected()) {
						pesquisarFinalArray();
					}
				}

				private void processarParams(Pesquisa pesquisa) throws ObjetoException {
					pesquisa.clonarParams();
					for (Param param : pesquisa.getCloneParams()) {
						if (Util.isEmpty(param.getValor())) {
							Object obj = Util.getValorInputDialog(InternalContainer.this, "label.atencao",
									"Valor para: " + param.getRotulo(), null);
							param.setValor(obj == null ? Constantes.VAZIO : obj.toString());
						}
					}
				}

				private void pesquisarFinal(int coluna) {
					super.habilitar(pesquisa.isProcessado());
					if (pesquisa.isProcessado()) {
						vinculoListener.pesquisarApos(objeto, pesquisa);
					}
					SwingUtilities.invokeLater(() -> processarColunaInfo(coluna));
					SwingUtilities.invokeLater(InternalContainer.this::atualizar);
				}

				private void pesquisarFinalArray() {
					super.habilitar(pesquisa.isProcessado());
					if (pesquisa.isProcessado()) {
						vinculoListener.pesquisarApos(objeto, pesquisa);
					}
					SwingUtilities.invokeLater(InternalContainer.this::atualizar);
				}

				private void processarColunaInfo(int coluna) {
					if (objeto.isColunaInfo()) {
						List<Integer> indices = Util.getIndicesLinha(tabelaPersistencia);
						for (int linha : indices) {
							InternalUtil.consolidarColetores(tabelaPersistencia, linha, coluna, pesquisa);
						}
						Util.ajustar(tabelaPersistencia, InternalContainer.this.getGraphics());
						larguraRotulos();
						if (configuraAlturaListener != null) {
							configuraAlturaListener.configurarAltura(ConfiguraAlturaSemRegistros.SCROLL_SUL, false);
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
						if (!Util.isEmpty(i.getValor()) && !i.isComoFiltro()) {
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
							try {
								String instrucao = modelo.getUpdate(linhas[0], objeto.getPrefixoNomeTabela(), coletor,
										false, conexao);
								instrucao += Constantes.QL + WHERE + getComplementoChaves(false, conexao);
								if (!Util.isEmpty(instrucao)) {
									updateFormDialog(abrirEmForm, conexao, instrucao, "Atualizar");
								}
							} catch (PersistenciaException ex) {
								Util.mensagem(InternalContainer.this, ex.getMessage());
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
							try {
								String instrucao = modelo.getUpdate(linhas[0], objeto.getPrefixoNomeTabela(), coletor,
										true, conexao);
								if (!Util.isEmpty(instrucao)) {
									updateFormDialog(abrirEmForm, conexao, instrucao, "Update");
								}
							} catch (PersistenciaException ex) {
								Util.mensagem(InternalContainer.this, ex.getMessage());
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
						try {
							String instrucao = modelo.getDelete(linhas[0], objeto.getPrefixoNomeTabela(), false,
									conexao);
							instrucao += Constantes.QL + WHERE + getComplementoChaves(false, conexao);
							if (!Util.isEmpty(instrucao)) {
								updateFormDialog(abrirEmForm, conexao, instrucao, "Excluir");
							}
						} catch (PersistenciaException ex) {
							Util.mensagem(InternalContainer.this, ex.getMessage());
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
						try {
							String instrucao = modelo.getDelete(linhas[0], objeto.getPrefixoNomeTabela(), true,
									conexao);
							if (!Util.isEmpty(instrucao)) {
								updateFormDialog(abrirEmForm, conexao, instrucao, "Delete");
							}
						} catch (PersistenciaException ex) {
							Util.mensagem(InternalContainer.this, ex.getMessage());
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
								if (!Util.isEmpty(instrucao)) {
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
						Variavel varCol = VariavelProvedor.getVariavel("INSERT_" + objeto.getTabela() + "_" + col);
						if (varCol != null) {
							Coluna coluna = modelo.getColuna(col);
							if (coluna != null) {
								coluna.setValorAlternativoInsert(varCol.getValor());
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
						if (chaves.isEmpty() || Util.isEmpty(instrucao.getValor())) {
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
			private TotalizarRegistrosAcao totalRegistroAcao = new TotalizarRegistrosAcao(true);
			private JCheckBoxMenuItem chkSubsTotalComparacao = new JCheckBoxMenuItem(
					ObjetoMensagens.getString("label.subs_total_comparacao"));
			private JCheckBoxMenuItem chkExibirInstrucao = new JCheckBoxMenuItem(
					ObjetoMensagens.getString("label.exibir_instrucao"));

			private ButtonFuncoes() {
				super("label.funcoes", Icones.SOMA);
				addMenuItem(new MinimoMaximoAcao(true));
				addMenuItem(new MinimoMaximoAcao(false));
				addSeparator();
				addItem(chkSubsTotalComparacao);
				addItem(chkExibirInstrucao);
				addMenuItem(new TotalizarRegistrosAcao(false));
				addMenuItem(totalRegistroAcao);
				addMenuItem(true, new AlternativoAcao());
			}

			private void totalRegistrosComFiltro() {
				chkExibirInstrucao.setSelected(false);
				totalRegistroAcao.actionPerformed(null);
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
					return new Filter(conexao, chaves, funcao, objeto).gerar();
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
							String filtro = Util.isEmpty(complementar) ? Constantes.VAZIO
									: " WHERE 1=1 " + complementar;
							String[] array = Persistencia.getTotalRegistros(conn,
									objeto.getTabelaEsquema(conexao) + filtro);
							long totalRegistros = Long.parseLong(array[1]);
							labelTotalRegistros(totalRegistros);
							if (chkSubsTotalComparacao.isSelected()) {
								objeto.setTotalRegistros(totalRegistros);
							}
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
			private Action checagemAcao = acaoMenu("label.checar_registro", Icones.SUCESSO);
			private MenuAddHierarquico menuAddHierarquico = new MenuAddHierarquico();
			private MenuAddInvisivel menuAddInvisivel = new MenuAddInvisivel();
			private MenuAlinhamento menuAlinhamento = new MenuAlinhamento();
			private static final long serialVersionUID = 1L;
			private MenuTemp menuTemp = new MenuTemp();

			private ButtonInfo() {
				super(Constantes.LABEL_METADADOS, Icones.INFO);
			}

			private void ini(Objeto objeto) {
				if (!Util.isEmpty(objeto.getBiblioChecagem())) {
					addMenuItem(checagemAcao);
				}
				if (objeto.getPesquisaAdicaoHierarquico() != null) {
					addMenuItem(scriptAdicaoHierAcao);
				}
				addMenu(menuAddHierarquico);
				addMenu(menuAddInvisivel);
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

			private String getSelBiblio(String string) {
				String[] strings = string.split(",");
				if (strings.length == 1) {
					return string;
				}
				Object resp = Util.getValorInputDialogSelect(InternalContainer.this, strings);
				return resp == null ? null : resp.toString();
			}

			private void checarRegistro() {
				String nomeBiblio = getSelBiblio(objeto.getBiblioChecagem());
				if (nomeBiblio == null) {
					return;
				}
				nomeBiblio = nomeBiblio.trim();
				Processador processador = new Processador();
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
				Map<String, Object> map = modelo.getMap(linhas[0], coletor);
				map.put("CONEXAO", conn);
				try {
					CacheBiblioteca cacheBiblioteca = processador.getCacheBiblioteca();
					Biblioteca biblioteca = cacheBiblioteca.getBiblioteca(nomeBiblio);
					adicionarConstantes(biblioteca, map);
					List<Object> resp = processador.processar(nomeBiblio, "main");
					if (isTextPool(resp)) {
						TextPool textPool = (TextPool) resp.get(0);
						Util.mensagem(InternalContainer.this, textPool.getListaText());
					} else {
						String string = getStringResposta(resp);
						Util.mensagem(InternalContainer.this, string);
					}
				} catch (Exception ex) {
					Util.stackTraceAndMessage(DESCRICAO, ex, InternalContainer.this);
				}
			}

			private void adicionarConstantes(Biblioteca biblioteca, Map<String, Object> map) {
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					String nome = entry.getKey().toUpperCase();
					Constante constante = new Constante(nome);
					constante.setValor(entry.getValue());
					biblioteca.addConstante(constante);
				}
			}

			private boolean isTextPool(List<Object> resp) {
				return !resp.isEmpty() && resp.get(0) instanceof TextPool;
			}

			private String getStringResposta(List<Object> lista) {
				StringBuilder sb = new StringBuilder();
				for (Object obj : lista) {
					if (obj == null) {
						sb.append("null\n");
						continue;
					}
					String string = obj.toString();
					if (string != null) {
						sb.append(new String(string.getBytes(), StandardCharsets.UTF_8) + "\n");
					}
				}
				if (sb.length() > 0) {
					sb.delete(sb.length() - 1, sb.length());
				}
				return sb.toString();
			}

			private class MenuTemp extends Menu {
				private Action selIntervaloColunaAcao = acaoMenu("label.selecionar_intervalo_registros");
				private Action complFinalBarraAcao = acaoMenu("label.complemento_no_final");
				private Action tabelasRepetidasAcao = acaoMenu("label.tabelas_repetidas");
				private Action larTitTodosAcao = acaoMenu("label.largura_titulo_todos");
				private Action colunasComplAcao = acaoMenu("label.colunas_complemento");
				private Action destacarColunaAcao = acaoMenu("label.destacar_coluna");
				private Action corAcao = actionMenu("label.cor", Icones.COR);
				private Action largAltAcao = acaoMenu("label.lar_alt");
				private Action xyAcao = acaoMenu("label.x_y");
				private static final long serialVersionUID = 1L;

				private MenuTemp() {
					super("label.temp");
					addMenuItem(corAcao);
					addMenuItem(true, colunasComplAcao);
					addMenuItem(true, complFinalBarraAcao);
					addMenuItem(true, larTitTodosAcao);
					addMenuItem(true, selIntervaloColunaAcao);
					addMenuItem(true, tabelasRepetidasAcao);
					addMenuItem(true, destacarColunaAcao);
					addMenuItem(true, largAltAcao);
					addMenuItem(true, xyAcao);
					larTitTodosAcao.setActionListener(e -> tabelaPersistencia.larguraTituloTodos());
					tabelasRepetidasAcao.hint(ObjetoMensagens.getString("hint.incon_link_auto"));
					selIntervaloColunaAcao.setActionListener(e -> selIntervaloRegistro());
					tabelasRepetidasAcao.setActionListener(e -> tabelasRepetidas());
					complFinalBarraAcao.setActionListener(e -> complFinalBarra());
					colunasComplAcao.setActionListener(e -> totalColunasCompl());
					destacarColunaAcao.setActionListener(e -> destacarColuna());
					largAltAcao.setActionListener(e -> larguraAltura());
					corAcao.setActionListener(e -> configCor());
					xyAcao.setActionListener(e -> xy());
				}

				private void larguraAltura() {
					if (dimensaoListener != null) {
						Dimension dimension = dimensaoListener.getDimensoes();
						String string = dimension.width + "," + dimension.height;
						Object resp = Util.getValorInputDialog(InternalContainer.this, "label.largura_altura", string,
								string);
						if (resp != null && !Util.isEmpty(resp.toString())) {
							ajustarLarguraManual(resp, dimensaoListener);
						}
					}
				}

				private void ajustarLarguraManual(Object resp, Dimensao dimensaoListener) {
					String[] strings = resp.toString().split(",");
					if (strings != null && strings.length == 2) {
						try {
							int largura = Integer.parseInt(strings[0].trim());
							int altura = Integer.parseInt(strings[1].trim());
							dimensaoListener.setLargAltura(largura, altura);
						} catch (Exception ex) {
							Util.stackTraceAndMessage("LARGURA-ALTURA", ex, InternalContainer.this);
						}
					}
				}

				private void xy() {
					if (alinhamentoListener != null) {
						Point posicao = alinhamentoListener.getPosicao();
						String string = posicao.x + "," + posicao.y;
						Object resp = Util.getValorInputDialog(InternalContainer.this, "label.x_y", string, string);
						if (resp != null && !Util.isEmpty(resp.toString())) {
							ajustarPosicaoManual(resp, alinhamentoListener);
						}
					}
				}

				private void ajustarPosicaoManual(Object resp, Alinhamento alinhamentoListener) {
					String[] strings = resp.toString().split(",");
					if (strings != null && strings.length == 2) {
						try {
							int x = Integer.parseInt(strings[0].trim());
							int y = Integer.parseInt(strings[1].trim());
							alinhamentoListener.setXY(x, y);
						} catch (Exception ex) {
							Util.stackTraceAndMessage("X-Y", ex, InternalContainer.this);
						}
					}
				}

				private void totalColunasCompl() {
					int atual = txtComplemento.getColumns();
					Object resp = Util.showInputDialog(InternalContainer.this, objeto.getId(),
							ObjetoMensagens.getString("label.colunas_complemento"), String.valueOf(atual));
					if (resp != null && !Util.isEmpty(resp.toString())) {
						try {
							int colunas = Util.getInt(resp.toString(), atual);
							txtComplemento.setColumns(colunas);
							Util.updateComponentTreeUI(InternalContainer.this);
						} catch (Exception e) {
							LOG.log(Level.SEVERE, Constantes.ERRO, e);
						}
					}
				}

				private void complFinalBarra() {
					toolbar.complementoNoFinal();
				}

				private void tabelasRepetidas() {
					Util.mensagem(InternalContainer.this, objeto.getInconsistencias());
				}

				private void configCor() {
					Color cor = InternalContainer.this.getBackground();
					cor = JColorChooser.showDialog(InternalContainer.this, "Cor", cor);
					InternalContainer.this.setBackground(cor);
					Util.updateComponentTreeUI(InternalContainer.this);
				}

				private void destacarColuna() {
					Object resp = Util.getValorInputDialog(InternalContainer.this, "label.coluna",
							Mensagens.getString("label.coluna_outra_coluna"), Constantes.VAZIO);
					if (resp != null && !Util.isEmpty(resp.toString())) {
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
					if (!Util.isEmpty(nome)) {
						int coluna = TabelaPersistenciaUtil.getIndiceColuna(tabelaPersistencia, nome.trim(), like);
						if (coluna != -1) {
							tabelaPersistencia.destacarColuna(coluna, false);
						}
					}
				}

				private void selIntervaloRegistro() {
					Object resp = Util.getValorInputDialog(InternalContainer.this, "label.intervalo",
							ObjetoMensagens.getString("msg.selecionar_intervalo_registros"), Constantes.VAZIO);
					if (resp != null && !Util.isEmpty(resp.toString())) {
						selecionar(resp.toString().split(","));
					}
				}

				private void selecionar(String[] array) {
					List<Intervalo> lista = new ArrayList<>();
					int i = 0;
					while (i < array.length) {
						if (i + 1 < array.length) {
							String min = array[i].trim();
							String max = array[++i].trim();
							try {
								lista.add(new Intervalo(Integer.parseInt(min), Integer.parseInt(max)));
							} catch (Exception ex) {
								//
							}
						}
						i++;
					}
					tabelaPersistencia.clearSelection();
					for (Intervalo intervalo : lista) {
						intervalo.selecionar(tabelaPersistencia);
					}
					tabelaListener.tabelaMouseClick(tabelaPersistencia, -1);
				}
			}

			private class MenuAlinhamento extends Menu {
				private static final long serialVersionUID = 1L;
				private SemResize semResize = new SemResize();
				private ComResize comResize = new ComResize();

				private MenuAlinhamento() {
					super(ObjetoMensagens.getString("label.alinhamento_internal_outros"), false, Icones.LARGURA);
					add(semResize);
					add(true, comResize);
					setToolTipText(ObjetoMensagens.getString("hint.alinhamento_internal"));
					habilitar(false);
				}

				void habilitar(boolean b) {
					semResize.habilitar(b);
					comResize.habilitar(b);
					setEnabled(b);
				}

				private void alinhar(DesktopAlinhamento opcao) {
					if (alinhamentoListener != null) {
						alinhamentoListener.alinhar(opcao);
					}
				}

				private class SemResize extends Menu {
					private Action esquerdoAcao = actionMenu("label.esquerdo", Icones.ALINHA_ESQUERDO);
					private Action direitoAcao = actionMenu("label.direito", Icones.ALINHA_DIREITO);
					private static final long serialVersionUID = 1L;

					private SemResize() {
						super(ObjetoMensagens.getString("label.sem_resize"), false, null);
						addMenuItem(direitoAcao);
						addMenuItem(esquerdoAcao);
						esquerdoAcao.setActionListener(e -> alinhar(DesktopAlinhamento.ESQUERDO));
						direitoAcao.setActionListener(e -> alinhar(DesktopAlinhamento.DIREITO));
					}

					void habilitar(boolean b) {
						esquerdoAcao.setEnabled(b);
						direitoAcao.setEnabled(b);
						setEnabled(b);
					}
				}

				private class ComResize extends Menu {
					private Action direitoAlinhadoFalseAcao = acaoMenu("label.direito_alinhado_false",
							Icones.ALINHA_DIREITO);
					private Action direitoAlinhadoTrueAcao = acaoMenu("label.direito_alinhado_true",
							Icones.ALINHA_DIREITO);
					private Action somenteDireitoAcao = acaoMenu("label.somente_direito", Icones.ALINHA_DIREITO);
					private Action mesmaLarguraAcao = acaoMenu("label.mesma_largura", Icones.LARGURA);
					private static final long serialVersionUID = 1L;

					private ComResize() {
						super(ObjetoMensagens.getString("label.com_resize"), false, null);
						addMenuItem(mesmaLarguraAcao);
						addMenuItem(true, somenteDireitoAcao);
						addMenuItem(direitoAlinhadoFalseAcao);
						addMenuItem(direitoAlinhadoTrueAcao);
						direitoAlinhadoFalseAcao.setActionListener(
								e -> alinhar(DesktopAlinhamento.COMPLETAR_DIREITO_AJUSTAR_LARG_FALSE));
						direitoAlinhadoTrueAcao.setActionListener(
								e -> alinhar(DesktopAlinhamento.COMPLETAR_DIREITO_AJUSTAR_LARG_TRUE));
						somenteDireitoAcao.setActionListener(e -> alinhar(DesktopAlinhamento.COMPLETAR_DIREITO));
						mesmaLarguraAcao.setActionListener(e -> mesma());
					}

					void habilitar(boolean b) {
						direitoAlinhadoFalseAcao.setEnabled(b);
						direitoAlinhadoTrueAcao.setEnabled(b);
						somenteDireitoAcao.setEnabled(b);
						mesmaLarguraAcao.setEnabled(b);
						setEnabled(b);
					}

					private void mesma() {
						if (larguraListener != null) {
							larguraListener.mesma();
						}
					}
				}
			}

			private class MenuCopiar extends Menu {
				private Action nomeColunasDestacAcao = actionMenu("label.nome_colunas_destac");
				private Action umaColunaSemAcao = actionMenu("label.uma_coluna_sem_aspas");
				private Action umaColunaComAcao = actionMenu("label.uma_coluna_com_aspas");
				private Action transferidorAcao = actionMenu("label.transferidor");
				private Action nomeColunasAcao = actionMenu("label.nome_colunas");
				private Action tabularAcao = actionMenu("label.tabular");
				private Action htmlAcao = actionMenu("label.html");
				private Action pipeAcao = actionMenu("label.pipe");
				private static final long serialVersionUID = 1L;

				private MenuCopiar() {
					super("label.copiar", Icones.TABLE2);
					setToolTipText(Mensagens.getString("label.copiar_tabela"));
					addMenuItem(pipeAcao);
					addMenuItem(true, htmlAcao);
					addMenuItem(true, tabularAcao);
					addMenuItem(true, transferidorAcao);
					addMenuItem(true, nomeColunasDestacAcao);
					addMenuItem(nomeColunasAcao);
					addMenuItem(true, umaColunaSemAcao);
					addMenuItem(umaColunaComAcao);
					nomeColunasDestacAcao.setActionListener(e -> nomeColunasDestac());
					umaColunaSemAcao.setActionListener(e -> umaColuna(false));
					umaColunaComAcao.setActionListener(e -> umaColuna(true));
					transferidorAcao.setActionListener(e -> processar(0));
					nomeColunasAcao.setActionListener(e -> nomeColunas());
					tabularAcao.setActionListener(e -> processar(1));
					htmlAcao.setActionListener(e -> processar(2));
					pipeAcao.setActionListener(e -> processar(3));
				}

				private List<String> getNomeColunasDestacadas() {
					return tabelaPersistencia.getListaNomeColunasDestacadas();
				}

				private List<String> getNomeColunas() {
					return tabelaPersistencia.getListaNomeColunas(true);
				}

				private void umaColuna(boolean comAspas) {
					String titulo = comAspas ? Mensagens.getString("label.uma_coluna_com_aspas")
							: Mensagens.getString("label.uma_coluna_sem_aspas");
					Util.copiarColunaUnicaString(titulo, tabelaPersistencia, comAspas, getNomeColunas());
				}

				private void nomeColunasDestac() {
					Util.copiarNomeColunas(Mensagens.getString("label.nome_colunas_destac"), tabelaPersistencia,
							getNomeColunasDestacadas());
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
						} else if (tipo == 3) {
							Util.setContentTransfered(transferidor.getPipe());
						}
					}
				}
			}

			private class MenuDML extends Menu {
				private Action descreverColunaAcao = acaoMenu("label.descrever_coluna", Icones.TABELA);
				private Action resumirColunaAcao = acaoMenu("label.resumir_coluna", Icones.TABELA);
				private Action filtroColunaAcao = acaoMenu("label.filtrar_coluna", Icones.TABELA);
				private Action ultimaConsAcao = acaoMenu("label.ultima_consulta", Icones.TABELA);
				private static final long serialVersionUID = 1L;

				private MenuDML() {
					super("label.dml", Icones.EXECUTAR);
					add(descreverColunaAcao);
					add(resumirColunaAcao);
					add(ultimaConsAcao);
					add(filtroColunaAcao);
					add(true, new MenuInsert(true));
					add(false, new MenuInsert(false));
					add(true, new MenuUpdate());
					add(true, new MenuDelete());
					add(true, new MenuSelect());
					add(true, new MenuSelectColuna());
					add(true, new MenuInnerJoin());
					descreverColunaAcao.setActionListener(e -> descreverColuna());
					resumirColunaAcao.setActionListener(e -> resumirColuna());
					filtroColunaAcao.setActionListener(e -> filtrarColuna());
					ultimaConsAcao.setActionListener(e -> ultimaCons());
				}

				private void ultimaCons() {
					Util.mensagem(InternalContainer.this, ultimaConsulta);
				}

				private void resumirColuna() {
					StringBuilder sb = criarSBTitulo();
					Coletor coletor = new Coletor();
					SetLista.view(objeto.getId(), tabelaPersistencia.getListaNomeColunas(true), coletor,
							InternalContainer.this, null);
					for (String string : coletor.getLista()) {
						sb.append(Constantes.QL);
						sb.append(string);
					}
					Util.mensagem(InternalContainer.this, sb.toString());
				}

				private String nomeTabela() {
					return PersistenciaModelo.prefixarEsquema(null, objeto.getPrefixoNomeTabela(), objeto.getTabela(),
							null);
				}

				private StringBuilder criarSBTitulo() {
					String string = nomeTabela();
					StringBuilder sb = new StringBuilder(string + Constantes.QL);
					sb.append(Util.completar(Constantes.VAZIO, string.length(), '-'));
					return sb;
				}

				private void filtrarColuna() {
					StringBuilder sb = criarSBTitulo();
					CheckBox checkObrigatorio = new CheckBox("OBRIGATORIO", false);
					CheckBox checkNulavel = new CheckBox("NULAVEL", false);
					CheckBox checkChave = new CheckBox("CHAVE", false);
					CheckBox checkBlob = new CheckBox("BLOB", false);
					CheckBox checkAuto = new CheckBox("AUTO", false);
					CheckBox checkNum = new CheckBox("NUM", false);
					List<CheckBox> list = new ArrayList<>();
					list.add(checkObrigatorio);
					list.add(checkNulavel);
					list.add(checkChave);
					list.add(checkBlob);
					list.add(checkAuto);
					list.add(checkNum);

					SetListaCheck.view(objeto.getId(), list, InternalContainer.this);

					OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
					List<Coluna> colunas = modelo.getModelo().getColunas();
					for (Coluna item : colunas) {
						if (sel(item, checkChave.isSelected(), checkNulavel.isSelected(), checkObrigatorio.isSelected(),
								checkBlob.isSelected(), checkAuto.isSelected(), checkNum.isSelected())) {
							sb.append(Constantes.QL);
							sb.append(item.getNome());
						}
					}
					Util.mensagem(InternalContainer.this, sb.toString());
				}

				private boolean sel(Coluna coluna, boolean chave, boolean nulavel, boolean obrigatorio, boolean blob,
						boolean auto, boolean numero) {
					if (coluna.isColunaInfo()) {
						return false;
					}
					return (chave && chave == coluna.isChave()) || (nulavel && nulavel == coluna.isNulavel())
							|| (obrigatorio && !coluna.isNulavel()) || (blob && blob == coluna.isBlob())
							|| (auto && auto == coluna.isAutoInc()) || (numero && numero == coluna.isNumero());
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
								if (!Util.isEmpty(instrucao)) {
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
								try {
									String instrucao = modelo.getUpdate(objeto.getPrefixoNomeTabela(), coletor, true,
											conexao);
									if (!Util.isEmpty(instrucao)) {
										updateFormDialog(abrirEmForm, conexao, instrucao, "Update");
									}
								} catch (PersistenciaException ex) {
									Util.mensagem(InternalContainer.this, ex.getMessage());
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
							try {
								String instrucao = modelo.getDelete(objeto.getPrefixoNomeTabela(), true, conexao);
								if (!Util.isEmpty(instrucao)) {
									updateFormDialog(abrirEmForm, conexao, instrucao, "Delete");
								}
							} catch (PersistenciaException ex) {
								Util.mensagem(InternalContainer.this, ex.getMessage());
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
							if (!Util.isEmpty(instrucao)) {
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
							if (!Util.isEmpty(frag)) {
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
						objeto.setSelectAlternativo(Constantes.SELECT + getNomeColunas(coletor));
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
							if (!Util.isEmpty(objeto.getApelidoParaJoins())) {
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
							if (!Util.isEmpty(instrucao)) {
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

			private class ChavesExportadasAcao extends Action {
				private static final long serialVersionUID = 1L;

				private ChavesExportadasAcao() {
					super(true, ObjetoMensagens.getString("label.chaves_exportadas"), false, Icones.KEY);
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

			private class ChavesImportadasAcao extends Action {
				private static final long serialVersionUID = 1L;

				private ChavesImportadasAcao() {
					super(true, ObjetoMensagens.getString("label.chaves_importadas"), false, Icones.KEY);
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

			private class MenuAddInvisivel extends Menu {
				private AbaixoAcao abaixoAcao = new AbaixoAcao();
				private static final long serialVersionUID = 1L;
				private AcimaAcao acimaAcao = new AcimaAcao();

				private MenuAddInvisivel() {
					super(ObjetoMensagens.getString("label.adicionar_invisivel"), false, Icones.HIERARQUIA);
					addMenuItem(acimaAcao);
					addMenuItem(abaixoAcao);
					habilitar(false);
				}

				void habilitar(boolean b) {
					setEnabled(b);
				}

				private class AcimaAcao extends Action {
					private static final long serialVersionUID = 1L;

					private AcimaAcao() {
						super(true, "label.acima", Icones.UPDATE);
					}

					@Override
					public void actionPerformed(ActionEvent e) {
						if (vinculoListener != null) {
							vinculoListener.adicionarHierarquicoInvisivelAcima(getConexao(), objeto);
						}
					}
				}

				private class AbaixoAcao extends Action {
					private static final long serialVersionUID = 1L;

					private AbaixoAcao() {
						super(true, "label.abaixo", Icones.BAIXAR);
					}

					@Override
					public void actionPerformed(ActionEvent e) {
						if (vinculoListener != null) {
							vinculoListener.adicionarHierarquicoInvisivelAbaixo(getConexao(), objeto);
						}
					}
				}
			}

			private class MenuAddHierarquico extends Menu {
				private RelacionadoAbaixoAcao relacionadoAbaixoAcao = new RelacionadoAbaixoAcao();
				private AvulsoAbaixoAcao avulsoAbaixoAcao = new AvulsoAbaixoAcao();
				private AvulsoAcimaAcao avulsoAcimaAcao = new AvulsoAcimaAcao();
				private static final long serialVersionUID = 1L;

				private MenuAddHierarquico() {
					super(ObjetoMensagens.getString("label.adicionar_hierarquico2"), false, Icones.HIERARQUIA);
					addMenuItem(relacionadoAbaixoAcao);
					addMenuItem(true, avulsoAcimaAcao);
					addMenuItem(avulsoAbaixoAcao);
					habilitar(false);
				}

				void habilitar(boolean b) {
					setEnabled(b);
				}

				private class AvulsoAcimaAcao extends Action {
					private static final long serialVersionUID = 1L;

					private AvulsoAcimaAcao() {
						super(true, ObjetoMensagens.getString("label.adicionar_hierarquico4"), false, Icones.UPDATE);
					}

					@Override
					public void actionPerformed(ActionEvent e) {
						if (vinculoListener != null) {
							try {
								vinculoListener.adicionarHierarquicoAvulsoAcima(getConexao(), objeto);
							} catch (AssistenciaException ex) {
								Util.mensagem(InternalContainer.this, ex.getMessage());
							}
						}
					}
				}

				private class AvulsoAbaixoAcao extends Action {
					private static final long serialVersionUID = 1L;

					private AvulsoAbaixoAcao() {
						super(true, ObjetoMensagens.getString("label.adicionar_hierarquico3"), false, Icones.BAIXAR);
					}

					@Override
					public void actionPerformed(ActionEvent e) {
						if (vinculoListener != null) {
							try {
								vinculoListener.adicionarHierarquicoAvulsoAbaixo(getConexao(), objeto);
							} catch (AssistenciaException ex) {
								Util.mensagem(InternalContainer.this, ex.getMessage());
							}
						}
					}
				}

				private class RelacionadoAbaixoAcao extends Action {
					private static final long serialVersionUID = 1L;

					private RelacionadoAbaixoAcao() {
						super(true, ObjetoMensagens.getString("label.adicionar_hierarquico5"), false, Icones.BAIXAR);
					}

					@Override
					public void actionPerformed(ActionEvent e) {
						if (vinculoListener != null) {
							Map<String, Object> mapaRef = new HashMap<>();
							mapaRef.put(ObjetoConstantes.ERROR, Boolean.FALSE);
							try {
								vinculoListener.adicionarHierarquico(getConexao(), objeto, mapaRef);
								processarMapaReferencia(mapaRef);
							} catch (MetadadoException | ObjetoException | AssistenciaException ex) {
								Util.mensagem(InternalContainer.this, ex.getMessage());
							}
						}
					}

					private void processarMapaReferencia(Map<String, Object> mapaRef) throws ObjetoException {
						Boolean erro = (Boolean) mapaRef.get(ObjetoConstantes.ERROR);
						if (erro.booleanValue() || mapaRef.get(VinculoHandler.PESQUISA) == null
								|| mapaRef.get("ref") == null) {
							return;
						}
						Vinculacao vinculacao = new Vinculacao();
						try {
							vinculoListener.preencherVinculacao(vinculacao);
						} catch (Exception ex) {
							Util.stackTraceAndMessage(DESCRICAO, ex, InternalContainer.this);
							return;
						}
						if (objeto.getPesquisas(false).isEmpty()) {
							processarPesquisaVazio(mapaRef, vinculacao);
						} else {
							processarPesquisa(mapaRef, vinculacao);
						}
					}

					private void processarPesquisaVazio(Map<String, Object> mapaRef, Vinculacao vinculacao)
							throws ObjetoException {
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

					private void processarPesquisa(Map<String, Object> mapaRef, Vinculacao vinculacao)
							throws ObjetoException {
						List<Pesquisa> pesquisas = objeto.getPesquisas(false);
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
							List<Pesquisa> pesquisas, Coletor coletor) throws ObjetoException {
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
							Referencia ref) throws ObjetoException {
						List<Pesquisa> lista = vinculacao.getPesquisas(objeto);
						for (Pesquisa pesq : lista) {
							if (pesq.ehEquivalente(pesquisa, objeto) && pesq.add(ref)) {
								atom.set(true);
							}
						}
					}

					private void adicionar(Map<String, Object> mapaRef, Vinculacao vinculacao, String nome)
							throws ObjetoException {
						Pesquisa pesquisa = (Pesquisa) mapaRef.get(VinculoHandler.PESQUISA);
						if (!Util.isEmpty(nome)) {
							pesquisa.setNome(nome);
						}
						objeto.addPesquisa(pesquisa);
						objeto.addReferencias(pesquisa.getReferencias());
						vinculacao.adicionarPesquisa(pesquisa);
						buttonPesquisa.complemento(objeto);
						addInvertido(mapaRef, vinculacao);
						buscaAuto = true;
					}

					private boolean addInvertido(Map<String, Object> mapaRef, Vinculacao vinculacao)
							throws ObjetoException {
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
				return componenteListener.getFormulario();
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
			txtComplemento.imagem = null;
			actionListenerInner.actionPerformed(null);
		}

		@Override
		public Set<String> getColecaoComplemento() {
			return objeto.getComplementos();
		}

		@Override
		public String getComplemento() {
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
		public void aplicarFragmento(List<Fragmento> fragmentos, boolean concatenar, boolean and) {
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
			} else if (!Util.isEmpty(complemento)) {
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
			final String chave = Constantes.SEP;
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
			if (!Util.isEmpty(consultaAlter)) {
				processar(Constantes.VAZIO, null, null, consultaAlter);
			}
		}
	}

	public void aplicarConfig(InternalConfig config) {
		if (!Util.isEmpty(config.getConexao())) {
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

	public void destacarObjeto(boolean b) {
		if (b) {
			objeto.setProcessar(true);
			objeto.ativar();
		} else {
			objeto.desativar();
		}
	}

	public String getTituloAtualizado() {
		OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
		return objeto.getTitle(modelo);
	}

	private void destacarColunas() {
		if (!Util.isEmpty(objeto.getDestacaveis())) {
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
		OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
		List<Integer> indices = Util.getIndicesLinha(tabelaPersistencia);
		if (indices.isEmpty()) {
			return Constantes.VAZIO;
		}
		List<Registro> registros = modelo.listarRegistrosChave(indices, conexao);
		return new FiltroUtil(objeto, and, registros).gerar();
	}

	public String getComplemento(boolean and, Conexao conexao) {
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

	public void limpar3() {
		if (getTotalRegistros() == 0) {
			limpar2();
		}
	}

	public boolean isAjustarLargura() {
		return objeto.isAjustarLargura();
	}

	public boolean contemExcecao() {
		return toolbar.contemExcecao();
	}

	public String detalhesExcecao() {
		StringBuilder sb = new StringBuilder();
		sb.append(tabelaPersistencia.detalhesExcecao());
		if (sb.length() == 0) {
			sb.append(toolbar.msgException);
		}
		return sb.toString();
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
		public void selectTotalValoresQueRepetem(TabelaPersistencia tabelaPersistencia, String nome, boolean form) {
			Conexao conexao = getConexao();
			if (conexao != null) {
				InstrucaoCampo instrucaoCampo = new InstrucaoCampo(conexao, objeto, nome);
				String instrucao = instrucaoCampo.totalValoresQueRepetem();
				if (!Util.isEmpty(instrucao)) {
					toolbar.selectFormDialog(form, conexao, instrucao);
				}
			}
		}

		@Override
		public void selectTotalValorMaisRepetido(TabelaPersistencia tabelaPersistencia, String nome, boolean form) {
			Conexao conexao = getConexao();
			if (conexao != null) {
				InstrucaoCampo instrucaoCampo = new InstrucaoCampo(conexao, objeto, nome);
				String instrucao = instrucaoCampo.totalDoValorMaisRepetido();
				if (!Util.isEmpty(instrucao)) {
					toolbar.selectFormDialog(form, conexao, instrucao);
				}
			}
		}

		@Override
		public void selectTotalMaiorLengthString(TabelaPersistencia tabelaPersistencia, String nome, boolean form) {
			Conexao conexao = getConexao();
			if (conexao != null) {
				InstrucaoCampo instrucaoCampo = new InstrucaoCampo(conexao, objeto, nome);
				String instrucao = instrucaoCampo.totalDoMaiorLengthString();
				if (!Util.isEmpty(instrucao)) {
					toolbar.selectFormDialog(form, conexao, instrucao);
				}
			}
		}

		@Override
		public void selectTotalMenorLengthString(TabelaPersistencia tabelaPersistencia, String nome, boolean form) {
			Conexao conexao = getConexao();
			if (conexao != null) {
				InstrucaoCampo instrucaoCampo = new InstrucaoCampo(conexao, objeto, nome);
				String instrucao = instrucaoCampo.totalDoMenorLengthString();
				if (!Util.isEmpty(instrucao)) {
					toolbar.selectFormDialog(form, conexao, instrucao);
				}
			}
		}

		@Override
		public void selectValorRepetidoComSuaQtd(TabelaPersistencia tabelaPersistencia, String nome, boolean form) {
			Conexao conexao = getConexao();
			if (conexao != null) {
				InstrucaoCampo instrucaoCampo = new InstrucaoCampo(conexao, objeto, nome);
				String instrucao = instrucaoCampo.valorRepetidoComESuaQtd();
				if (!Util.isEmpty(instrucao)) {
					toolbar.selectFormDialog(form, conexao, instrucao);
				}
			}
		}

		@Override
		public void selectDistinct(TabelaPersistencia tabelaPersistencia, String nome, boolean form) {
			Conexao conexao = getConexao();
			if (conexao != null) {
				InstrucaoCampo instrucaoCampo = new InstrucaoCampo(conexao, objeto, nome);
				String instrucao = instrucaoCampo.valorDistinto();
				if (!Util.isEmpty(instrucao)) {
					toolbar.selectFormDialog(form, conexao, instrucao);
				}
			}
		}

		@Override
		public void selectGroupBy(TabelaPersistencia tabelaPersistencia, String nome, boolean form) {
			Conexao conexao = getConexao();
			if (conexao != null) {
				InstrucaoCampo instrucaoCampo = new InstrucaoCampo(conexao, objeto, nome);
				String instrucao = instrucaoCampo.valorAgrupado();
				if (!Util.isEmpty(instrucao)) {
					toolbar.selectFormDialog(form, conexao, instrucao);
				}
			}
		}

		@Override
		public void selectMinimo(TabelaPersistencia tabelaPersistencia, String nome, boolean form) {
			Conexao conexao = getConexao();
			if (conexao != null) {
				String instrucao = new Filter(conexao, new String[] { nome }, "MIN", objeto).gerarCompleto();
				if (!Util.isEmpty(instrucao)) {
					toolbar.selectFormDialog(form, conexao, instrucao);
				}
			}
		}

		@Override
		public void selectMaximo(TabelaPersistencia tabelaPersistencia, String nome, boolean form) {
			Conexao conexao = getConexao();
			if (conexao != null) {
				String instrucao = new Filter(conexao, new String[] { nome }, "MAX", objeto).gerarCompleto();
				if (!Util.isEmpty(instrucao)) {
					toolbar.selectFormDialog(form, conexao, instrucao);
				}
			}
		}

		private Coletor getNomeBiblio() {
			List<String> nomes = new ArrayList<>();
			vinculoListener.listarNomeBiblio(nomes, InternalContainer.this);
			Coletor coletor = new Coletor();
			Config config = new SetLista.Config(true, true);
			SetLista.view(objeto.getId() + ObjetoMensagens.getString("label.biblio"), nomes, coletor,
					InternalContainer.this, config);
			return coletor;
		}

		@Override
		public void mapearApartirBiblio(TabelaPersistencia tabelaPersistencia, Coluna coluna) {
			if (vinculoListener == null) {
				return;
			}
			Coletor coletor = getNomeBiblio();
			if (coletor.size() != 1) {
				return;
			}
			mapear(coluna, coletor.get(0));
		}

		private void mapear(Coluna coluna, String nomeBiblio) {
			try {
				Coletor coletor = new Coletor();
				List<String> entradas = Util.listarEntradas(new File(nomeBiblio));
				SetLista.view(objeto.getId() + ObjetoMensagens.getString("label.nome_entrada_file"), entradas, coletor,
						InternalContainer.this, new SetLista.Config(true, true, objeto.getId()));
				if (coletor.size() != 1) {
					return;
				}
				String classe = normalizar(coletor.get(0));
				objeto.setClassBiblio(classe);
				tabelaPersistencia.configClasseBiblio(classe, coluna);
				checkSalvarVinculacao(classe);
			} catch (Exception ex) {
				Util.stackTraceAndMessage(DESCRICAO, ex, InternalContainer.this);
			}
		}

		private String normalizar(String string) {
			if (string.endsWith(".class")) {
				string = string.substring(0, string.length() - 6);
			}
			string = Util.replaceAll(string, "/", ".");
			string = Util.replaceAll(string, "\\", ".");
			return string;
		}

		private void checkSalvarVinculacao(String classe) throws ObjetoException {
			String tabela = objeto.getTabela().trim();
			if (vinculoListener == null || Util.isEmpty(tabela)) {
				return;
			}
			if (!Util.confirmar(InternalContainer.this, ObjetoMensagens.getString("msg.salvar_classe_biblio_vinculo"),
					false)) {
				return;
			}
			Vinculacao vinculacao = new Vinculacao();
			try {
				vinculoListener.preencherVinculacao(vinculacao);
			} catch (Exception ex) {
				Util.stackTraceAndMessage("CLASSE BIBLIO", ex, InternalContainer.this);
				return;
			}
			ParaTabela para = vinculacao.getParaTabela(tabela);
			if (para == null) {
				para = new ParaTabela(tabela);
				vinculacao.putParaTabela(para);
			}
			para.setClassBiblio(classe, null);
			vinculoListener.salvarVinculacao(vinculacao);
		}

		private Coletor getNomePesquisa() {
			List<Pesquisa> pesquisas = objeto.getPesquisas(false);
			List<String> nomes = pesquisas.stream().map(Pesquisa::getNome).collect(Collectors.toList());
			Coletor coletor = new Coletor();
			Config config = new SetLista.Config(true, true);
			config.setCriar(true);
			SetLista.view(objeto.getId() + ObjetoMensagens.getString(LABEL_NOME_PESQUISA), nomes, coletor,
					InternalContainer.this, config);
			return coletor;
		}

		@Override
		public void pesquisaApartirColuna(TabelaPersistencia tabelaPersistencia, String coluna) throws ObjetoException {
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

		private void prepararPesquisa(String coluna, String nomePesquisa, List<Objeto> objetos) throws ObjetoException {
			List<String> ids = objetos.stream().map(Objeto::getId).collect(Collectors.toList());
			Coletor coletor = new Coletor();
			SetLista.view(objeto.getId() + ObjetoMensagens.getString("label.nome_outra_tabela"), ids, coletor,
					InternalContainer.this, new SetLista.Config(true, true, nomePesquisa));
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

		private void criarAtualizarPesquisa(String coluna, String nomePesquisa, Objeto objDetalhe)
				throws ObjetoException {
			Coletor coletor = new Coletor();
			vinculoListener.selecionarCampo(objDetalhe, coletor, InternalContainer.this, coluna);
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
					new Referencia(objeto.getGrupo(), objeto.getTabela(), coluna), null);
			Referencia referencia = new Referencia(objDetalhe.getGrupo(), objDetalhe.getTabela(), coletor.get(0));
			referencia.setVazioInvisivel(true);
			Pesquisa existente = objeto.getPesquisa(pesquisa);
			if (existente != null) {
				existente.add(referencia);
				objeto.addReferencia(referencia);
				atualizar(vinculacao, pesquisa, referencia, objDetalhe);
			} else {
				pesquisa.add(referencia);
				pesquisa.setIconeGrupo(objDetalhe.getIcone());
				adicionar(vinculacao, pesquisa, objDetalhe);
			}
		}

		private void atualizar(Vinculacao vinculacao, Pesquisa pesquisa, Referencia referencia, Objeto objDetalhe)
				throws ObjetoException {
			Pesquisa existente = vinculacao.getPesquisa(pesquisa);
			if (existente != null) {
				existente.add(referencia);
				toolbar.buttonPesquisa.complemento(objeto);
				buscaAuto = true;
				processarInvertido(vinculacao, existente, objDetalhe);
				vinculoListener.salvarVinculacao(vinculacao);
			}
		}

		private void adicionar(Vinculacao vinculacao, Pesquisa pesquisa, Objeto objDetalhe) throws ObjetoException {
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

		private void processarInvertido(Vinculacao vinculacao, Pesquisa pesquisa, Objeto objDetalhe)
				throws ObjetoException {
			Referencia ref = pesquisa.get(objDetalhe);
			if (ref == null) {
				return;
			}
			Pesquisa invertido = ref.rotuloDe(objeto);
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
			if (!Util.isEmpty(txtComplemento.getText())) {
				String[] simNao = getArraySimNao();
				String opcao = opcaoConcatenar(simNao);
				if (Util.isEmpty(opcao)) {
					return;
				}
				if (simNao[0].equals(opcao)) {
					string = txtComplemento.getText();
				}
			}
			String prefixo = getPrefixo();
			if (Util.isEmpty(prefixo)) {
				return;
			}
			String opcao = getOpcao();
			if (Util.isEmpty(opcao)) {
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
			if (!Util.isEmpty(txtComplemento.getText())) {
				String[] simNao = getArraySimNao();
				String opcao = opcaoConcatenar(simNao);
				if (Util.isEmpty(opcao)) {
					return;
				}
				if (simNao[0].equals(opcao)) {
					string = txtComplemento.getText();
				}
			}
			String prefixo = getPrefixo();
			if (Util.isEmpty(prefixo)) {
				return;
			}
			String opcao = getOpcao();
			if (Util.isEmpty(opcao)) {
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
			try {
				List<String> lista = metadado.getListaCampoExportadoPara(coluna);
				String string = InternalUtil.campoExportadoPara(objeto.getTabela(), coluna, lista);
				Util.mensagem(InternalContainer.this, string);
			} catch (MetadadoException ex) {
				Util.mensagem(InternalContainer.this, ex.getMessage());
			}
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
			try {
				List<String> lista = metadado.getListaCampoImportadoDe(coluna);
				String string = InternalUtil.campoImportadoDe(objeto.getTabela(), coluna, lista);
				Util.mensagem(InternalContainer.this, string);
			} catch (MetadadoException ex) {
				Util.mensagem(InternalContainer.this, ex.getMessage());
			}
		}
	}

	public void atualizarComplemento(Objeto obj) {
		if (obj != null && obj == objeto && !obj.getPesquisas(false).isEmpty()) {
			buscaAuto = true;
			toolbar.buttonPesquisa.complemento(obj);
		}
	}

	public void labelTotalRegistros(long total) {
		toolbar.labelTotal.setText(Util.formatarNumero("" + total));
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

	private void destacarPesquisado(String titulo) {
		if (objeto.getReferenciaPesquisa() == null) {
			return;
		}
		if (ObjetoPreferencia.getTipoDestaqueFormulario() == ObjetoConstantes.TIPO_DESTAC_FORM_VISIBILIDADE) {
			new Thread(new DestaqueVisibilidade()).start();
		} else if (ObjetoPreferencia.getTipoDestaqueFormulario() == ObjetoConstantes.TIPO_DESTAC_FORM_COR_FUNDO) {
			new Thread(new DestaqueCorFundo()).start();
		} else if (ObjetoPreferencia.getTipoDestaqueFormulario() == ObjetoConstantes.TIPO_DESTAC_FORM_TITULO) {
			new Thread(new DestaqueTitulo(titulo)).start();
		}
	}

	private class DestaqueVisibilidade implements Runnable {
		private int contador;

		@Override
		public synchronized void run() {
			while (contador < Constantes.DEZ && !Thread.currentThread().isInterrupted()) {
				try {
					if (selecaoListener != null) {
						selecaoListener.visibilidade(contador % 2 == 0);
					}
					wait(500);
					contador++;
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			if (selecaoListener != null) {
				selecaoListener.visibilidade(false);
			}
			atualizar();
		}
	}

	private class DestaqueCorFundo implements Runnable {
		private int contador;

		@Override
		public synchronized void run() {
			while (contador < Constantes.DEZ && !Thread.currentThread().isInterrupted()) {
				try {
					if (selecaoListener != null) {
						selecaoListener.corFundo(contador % 2 == 0);
					}
					wait(500);
					contador++;
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			if (selecaoListener != null) {
				selecaoListener.corFundo(false);
			}
			atualizar();
		}
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
			while (contador < Constantes.DEZ && !Thread.currentThread().isInterrupted()) {
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
			atualizar();
		}

		private void destacarTitulo(String titulo) {
			if (indice < 0) {
				indice = esq.length() - 1;
			}
			if (tituloListener != null) {
				tituloListener.setTitulo(esq.substring(indice) + titulo + dir.substring(indice));
			}
			indice--;
		}
	}

	private void atualizar() {
		Desktop desktop = getDesktop();
		if (desktop != null) {
			desktop.repaint();
		}
	}

	private Desktop getDesktop() {
		Container parent = this;
		Desktop desktop = null;
		while (parent != null) {
			if (parent instanceof Desktop) {
				desktop = (Desktop) parent;
				break;
			}
			parent = parent.getParent();
		}
		return desktop;
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
		toolbar.buttonInfo.menuAddHierarquico.habilitar(vinculoListener != null);
		toolbar.buttonInfo.menuAddInvisivel.habilitar(vinculoListener != null);
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
		try {
			String iconeGrupo = pesquisa.getIconeGrupo();
			return Util.isEmpty(iconeGrupo) ? null : Imagens.getIcon(iconeGrupo);
		} catch (AssistenciaException ex) {
			Util.mensagem(null, ex.getMessage());
			return null;
		}
	}

	public InternalConfig getInternalConfig() {
		return internalConfig;
	}

	public void setInternalConfig(InternalConfig internalConfig) {
		this.internalConfig = internalConfig;
	}
}

class InstrucaoCampo {
	private static final String HAVING_COUNT_1 = "\nHAVING COUNT(*) > 1";
	private static final String HAVING_COUNT = "\n    HAVING COUNT(";
	private static final String FROM_SELECT = "\nFROM (SELECT ";
	private static final String GROUP_BY = "\n    GROUP BY ";
	private static final String FROM = "\n    FROM ";
	private static final String COUNT = ", COUNT(";
	private static final String FROM2 = "\nFROM ";
	final Conexao conexao;
	final Objeto objeto;
	final String campo;

	InstrucaoCampo(Conexao conexao, Objeto objeto, String campo) {
		this.conexao = conexao;
		this.objeto = objeto;
		this.campo = campo;
	}

	String fromTabela() {
		return FROM2 + objeto.getTabelaEsquema(conexao);
	}

	String groupBy() {
		return "\nGROUP BY " + objeto.comApelido(campo);
	}

	String valorAgrupado() {
		StringBuilder sb = new StringBuilder(Constantes.SELECT + objeto.comApelido(campo) + ", COUNT(*)");
		sb.append(fromTabela());
		sb.append("\nWHERE " + objeto.comApelido(campo) + " IS NOT NULL");
		sb.append(groupBy());
		sb.append(HAVING_COUNT_1);
		return sb.toString();
	}

	String valorDistinto() {
		StringBuilder sb = new StringBuilder("SELECT DISTINCT " + objeto.comApelido(campo));
		sb.append(fromTabela());
		sb.append("\nORDER BY " + objeto.comApelido(campo));
		return sb.toString();
	}

	String valorRepetidoComESuaQtd() {
		StringBuilder sb = new StringBuilder(
				Constantes.SELECT + objeto.comApelido(campo) + COUNT + objeto.comApelido(campo) + ")");
		sb.append(fromTabela());
		sb.append("\nWHERE EXISTS (SELECT " + campo + ", COUNT(*)");
		sb.append(FROM + objeto.getTabelaEsquema2(conexao));
		sb.append(GROUP_BY + campo);
		sb.append("\n    HAVING COUNT(*) > 1");
		sb.append("\n)");
		sb.append(HAVING_COUNT_1);
		sb.append(groupBy());
		return sb.toString();
	}

	String totalDoValorMaisRepetido() {
		StringBuilder sb = new StringBuilder("SELECT MAX(tabela.TOTAL)");
		sb.append(FROM_SELECT + objeto.comApelido(campo) + COUNT + objeto.comApelido(campo) + ") AS TOTAL");
		sb.append(FROM + objeto.getTabelaEsquema(conexao));
		sb.append(GROUP_BY + objeto.comApelido(campo));
		sb.append(HAVING_COUNT + objeto.comApelido(campo) + ") > 1");
		sb.append("\n) tabela");
		return sb.toString();
	}

	String totalValoresQueRepetem() {
		StringBuilder sb = new StringBuilder("SELECT COUNT(*)");
		sb.append(FROM_SELECT + objeto.comApelido(campo) + COUNT + objeto.comApelido(campo) + ") AS TOTAL");
		sb.append(FROM + objeto.getTabelaEsquema(conexao));
		sb.append(GROUP_BY + objeto.comApelido(campo));
		sb.append(HAVING_COUNT + objeto.comApelido(campo) + ") > 1");
		sb.append("\n) tabela");
		return sb.toString();
	}

	String totalDoMaiorLengthString() {
		StringBuilder sb = new StringBuilder("SELECT MAX(LENGTH(" + objeto.comApelido(campo) + "))");
		sb.append(FROM2 + objeto.getTabelaEsquema(conexao));
		return sb.toString();
	}

	String totalDoMenorLengthString() {
		StringBuilder sb = new StringBuilder("SELECT MIN(LENGTH(" + objeto.comApelido(campo) + "))");
		sb.append(FROM2 + objeto.getTabelaEsquema(conexao));
		return sb.toString();
	}
}

class Filter {
	final Conexao conexao;
	final String[] campos;
	final String funcao;
	final Objeto obj;

	Filter(Conexao conexao, String[] campos, String funcao, Objeto obj) {
		this.conexao = conexao;
		this.campos = campos;
		this.funcao = funcao;
		this.obj = obj;
	}

	String gerar() {
		StringBuilder sb = new StringBuilder();
		sb.append(obj.comApelido(campos[0]));
		sb.append(" = (SELECT " + funcao + "(" + campos[0] + ")");
		sb.append(" FROM ");
		sb.append(obj.getTabelaEsquema(conexao) + ")");
		if (campos.length > 1 && !Util.isEmpty(conexao.getLimite())) {
			sb.append(Constantes.QL);
			sb.append(conexao.getLimite());
		}
		return sb.toString();
	}

	String gerarCompleto() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM " + obj.getTabelaEsquema(conexao) + Constantes.QL);
		sb.append("WHERE " + gerar());
		return sb.toString();
	}
}

class Intervalo {
	final int min;
	final int max;

	Intervalo(int min, int max) {
		this.min = min;
		this.max = max;
	}

	boolean valido(int rows) {
		return (min <= max) && (min >= 0 && min < rows) && (max >= 0 && max < rows);
	}

	void selecionar(JTable table) {
		int rows = table.getRowCount();
		if (valido(rows)) {
			table.addRowSelectionInterval(min, max);
		}
	}
}

class Animar extends Thread {
	final AnimarListener listener;
	final int delta;
	final int delay;
	final int pivo;
	final int para;
	int status;

	public Animar(int pivo, int para, AnimarListener listener, int delta, int delay) {
		this.listener = listener;
		this.delta = delta;
		this.delay = delay;
		this.pivo = pivo;
		this.para = para;
		status = pivo;
	}

	void atualizarStatus() {
		if (pivo < para) {
			status += delta;
		} else {
			status -= delta;
		}
	}

	boolean processado() {
		return (pivo < para) ? status >= para : status <= para;
	}

	void iniciar() {
		start();
	}

	@Override
	public void run() {
		while (!processado() && !Thread.currentThread().isInterrupted()) {
			atualizarStatus();
			listener.atualizar(status);
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}

@FunctionalInterface
interface AnimarListener {
	void atualizar(int valor);
}