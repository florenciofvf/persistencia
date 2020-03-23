package br.com.persist.fichario;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import br.com.persist.Metadado;
import br.com.persist.banco.Conexao;
import br.com.persist.comp.Panel;
import br.com.persist.container.AnexoContainer;
import br.com.persist.container.AnotacaoContainer;
import br.com.persist.container.ArvoreContainer;
import br.com.persist.container.ComparacaoContainer;
import br.com.persist.container.ConexaoContainer;
import br.com.persist.container.ConfigContainer;
import br.com.persist.container.ConsultaContainer;
import br.com.persist.container.FragmentoContainer;
import br.com.persist.container.MapeamentoContainer;
import br.com.persist.container.MetadadosContainer;
import br.com.persist.container.ObjetoContainer;
import br.com.persist.container.RequisicaoContainer;
import br.com.persist.container.UpdateContainer;
import br.com.persist.container.VariaveisContainer;
import br.com.persist.desktop.Container;
import br.com.persist.desktop.Desktop;
import br.com.persist.desktop.Objeto;
import br.com.persist.desktop.Relacao;
import br.com.persist.desktop.Superficie;
import br.com.persist.formulario.AnotacaoFormulario;
import br.com.persist.formulario.ContainerFormulario;
import br.com.persist.formulario.DesktopFormulario;
import br.com.persist.listener.ObjetoContainerListener;
import br.com.persist.modelo.VariaveisModelo;
import br.com.persist.principal.Formulario;
import br.com.persist.util.BuscaAuto.Grupo;
import br.com.persist.util.ChaveValor;
import br.com.persist.util.Constantes;
import br.com.persist.util.Form;
import br.com.persist.util.LinkAuto.Link;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Preferencias;
import br.com.persist.util.Util;

public class Fichario extends JTabbedPane {
	private static final long serialVersionUID = 1L;
	private final transient Listener listener = new Listener();
	private static final Logger LOG = Logger.getGlobal();
	private transient Ponto ponto;
	private Rectangle rectangle;
	private int ultX;
	private int ultY;

	public Fichario() {
		setTabLayoutPolicy(SCROLL_TAB_LAYOUT);
		new DropTarget(this, listenerSoltar);
		addMouseMotionListener(listener);
		addMouseListener(listener);
		config();
	}

	private void config() {
		inputMap().put(getKeyStroke(KeyEvent.VK_Q), "excluir_action");
		getActionMap().put("excluir_action", excluirAction);
	}

	transient Action excluirAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			int indice = getSelectedIndex();

			if (indice != -1) {
				remove(indice);
			}
		}
	};

	private InputMap inputMap() {
		return getInputMap(WHEN_IN_FOCUSED_WINDOW);
	}

	public static KeyStroke getKeyStroke(int keyCode) {
		return KeyStroke.getKeyStroke(keyCode, InputEvent.CTRL_MASK);
	}

	private transient DropTargetListener listenerSoltar = new DropTargetListener() {
		@Override
		public void dropActionChanged(DropTargetDragEvent e) {
			e.rejectDrag();
		}

		@Override
		public void dragEnter(DropTargetDragEvent e) {
			e.rejectDrag();
		}

		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			Point point = dtde.getLocation();
			int indice = indexAtLocation(point.x, point.y);

			if (indice != -1 && indice != getSelectedIndex()) {
				setSelectedIndex(indice);
			}
		}

		@Override
		public void dragExit(DropTargetEvent dte) {
			LOG.log(Level.FINEST, "dragExit");
		}

		@Override
		public void drop(DropTargetDropEvent e) {
			e.rejectDrop();
		}
	};

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		if (rectangle != null) {
			g.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
		}
	}

	public void destacar(Formulario formulario, Conexao conexao, Superficie superficie, int tipoContainer) {
		List<Objeto> objetos = superficie.getSelecionados();
		boolean continua = false;

		for (Objeto objeto : objetos) {
			if (!Util.estaVazio(objeto.getTabela2())) {
				continua = true;
				break;
			}
		}

		if (!continua) {
			return;
		}

		List<Objeto> selecionados = new ArrayList<>();

		for (Objeto objeto : objetos) {
			if (objeto.isCopiarDestacado()) {
				selecionados.add(objeto.clonar());
			} else {
				selecionados.add(objeto);
			}
		}

		if (tipoContainer == Constantes.TIPO_CONTAINER_FORMULARIO) {
			destacarForm(formulario, selecionados, conexao);

		} else if (tipoContainer == Constantes.TIPO_CONTAINER_DESKTOP) {
			destacarDesk(formulario, selecionados, conexao);

		} else if (tipoContainer == Constantes.TIPO_CONTAINER_FICHARIO) {
			destacarObjt(formulario, selecionados, conexao);

		} else if (tipoContainer == Constantes.TIPO_CONTAINER_PROPRIO) {
			destacarProp(formulario, selecionados, conexao, superficie);
		}
	}

	private void destacarForm(Formulario formulario, List<Objeto> objetos, Conexao conexao) {
		DesktopFormulario form = new DesktopFormulario(formulario);

		int x = 10;
		int y = 10;

		for (Objeto objeto : objetos) {
			if (!Util.estaVazio(objeto.getTabela2())) {
				Object[] array = Util.criarArray(conexao, objeto, null);
				form.getDesktop().addForm(array, new Point(x, y), null, (String) array[Util.ARRAY_INDICE_APE], false);
				objeto.setSelecionado(false);
				x += 25;
				y += 25;
			}
		}

		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	private void destacarDesk(Formulario formulario, List<Objeto> objetos, Conexao conexao) {
		Desktop desktop = novoDesktop(formulario);

		int x = 10;
		int y = 10;

		for (Objeto objeto : objetos) {
			if (!Util.estaVazio(objeto.getTabela2())) {
				Object[] array = Util.criarArray(conexao, objeto, null);
				desktop.addForm(array, new Point(x, y), null, (String) array[Util.ARRAY_INDICE_APE], false);
				objeto.setSelecionado(false);
				x += 25;
				y += 25;
			}
		}

		desktop.ini(getGraphics());
		SwingUtilities.invokeLater(() -> desktop.distribuir(-20));
	}

	private void destacarProp(Formulario formulario, List<Objeto> objetos, Conexao conexao, Superficie superficie) {
		boolean salvar = false;

		ChaveValor cvDeltaX = VariaveisModelo.get(Constantes.DELTA_X_AJUSTE_FORM_OBJETO);
		ChaveValor cvDeltaY = VariaveisModelo.get(Constantes.DELTA_Y_AJUSTE_FORM_OBJETO);

		if (cvDeltaX == null) {
			cvDeltaX = new ChaveValor(Constantes.DELTA_X_AJUSTE_FORM_OBJETO, "" + Constantes.TRINTA);
			VariaveisModelo.adicionar(cvDeltaX);
			salvar = true;
		}

		if (cvDeltaY == null) {
			cvDeltaY = new ChaveValor(Constantes.DELTA_Y_AJUSTE_FORM_OBJETO, "" + Constantes.TRINTA);
			VariaveisModelo.adicionar(cvDeltaY);
			salvar = true;
		}

		if (salvar) {
			VariaveisModelo.salvar();
			VariaveisModelo.inicializar();
		}

		for (Objeto objeto : objetos) {
			if (!Util.estaVazio(objeto.getTabela2())) {
				Object[] array = Util.criarArray(conexao, objeto, null);
				superficie.addForm(array,
						new Point(objeto.getX() + cvDeltaX.getInteiro(Constantes.TRINTA),
								objeto.getY() + cvDeltaY.getInteiro(Constantes.TRINTA)),
						null, (String) array[Util.ARRAY_INDICE_APE], false);
				objeto.setSelecionado(false);
			}
		}

		superficie.repaint();
	}

	private void destacarObjt(Formulario formulario, List<Objeto> objetos, Conexao conexao) {
		for (Objeto objeto : objetos) {
			if (!Util.estaVazio(objeto.getTabela2())) {
				Superficie.setComplemento(conexao, objeto);
				novoObjeto(formulario, conexao, objeto);
				objeto.setSelecionado(false);
			}
		}
	}

	public void addTab(String title, String titleMin, Component component) {
		addTab(Preferencias.isTituloAbaMin() ? Mensagens.getString(titleMin) : Mensagens.getString(title), component);
	}

	public Desktop novoDesktop(Formulario formulario) {
		Desktop desktop = new Desktop(formulario, false);
		desktop.setAbortarFecharComESC(Preferencias.isAbortarFecharComESC());
		addTab(Constantes.LABEL_DESKTOP, Constantes.LABEL_DESKTOP_MIN, desktop);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.DESKTOP);
		setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_DESKTOP));
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);

		return desktop;
	}

	public Panel novaAnotacao(Formulario formulario) {
		AnotacaoContainer container = new AnotacaoContainer(null, formulario);
		addTab(Constantes.LABEL_ANOTACOES, Constantes.LABEL_ANOTACOES_MIN, container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.ANOTACAO);
		setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_ANOTACOES));
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);

		return container;
	}

	public void destacarEmFormularioAnotacao(Formulario formulario, AnotacaoContainer container) {
		int indice = getIndice(container);

		if (indice == -1) {
			return;
		}

		super.remove(indice);
		AnotacaoFormulario.criar(formulario, container);
	}

	public void retornoDestacarEmFormularioAnotacao(Formulario formulario, AnotacaoContainer container) {
		addTab(Constantes.LABEL_ANOTACOES, Constantes.LABEL_ANOTACOES_MIN, container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.ANOTACAO);
		setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_ANOTACOES));
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);
	}

	public Panel novaRequisicao(Formulario formulario) {
		RequisicaoContainer container = new RequisicaoContainer(null);
		addTab(Constantes.LABEL_REQUISICAO, Constantes.LABEL_REQUISICAO_MIN, container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.REQUISICAO);
		setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_REQUISICAO));
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);

		return container;
	}

	public Panel novaConsulta(Formulario formulario, Conexao conexao) {
		ConsultaContainer container = new ConsultaContainer(null, formulario, conexao, null, null, true);
		addTab(Constantes.LABEL_CONSULTA, Constantes.LABEL_CONSULTA_MIN, container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.CONSULTA);
		setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_CONSULTA));
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);

		return container;
	}

	public Panel novoMetadado(Formulario formulario, Conexao conexao) {
		MetadadosContainer container = new MetadadosContainer(null, formulario, formulario, conexao);
		addTab(Constantes.LABEL_METADADOS, Constantes.LABEL_METADADOS_MIN, container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.METADADO);
		setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_METADADOS));
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);

		return container;
	}

	public Panel novoUpdate(Formulario formulario, Conexao conexao) {
		UpdateContainer container = new UpdateContainer(null, formulario, conexao, null, null);
		addTab(Constantes.LABEL_ATUALIZAR, Constantes.LABEL_ATUALIZAR_MIN, container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.UPDATE);
		setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_ATUALIZAR));
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);

		return container;
	}

	public void novaArvore(Formulario formulario) {
		ArvoreContainer container = new ArvoreContainer(null, formulario, null);
		addTab(Constantes.LABEL_ARQUIVOS, Constantes.LABEL_ARQUIVOS_MIN, container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.ARVORE);
		setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_ARQUIVOS));
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);
	}

	public void novoAnexo(Formulario formulario) {
		AnexoContainer container = new AnexoContainer(null, formulario, null);
		addTab(Constantes.LABEL_ANEXOS, Constantes.LABEL_ANEXOS_MIN, container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.ANEXO);
		setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_ANEXOS));
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);
	}

	public void novoConfig(Formulario formulario) {
		ConfigContainer container = new ConfigContainer(null, formulario);
		addTab(Constantes.LABEL_CONFIGURACOES, Constantes.LABEL_CONFIGURACOES_MIN, container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.CONFIG);
		setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_CONFIGURACOES));
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);
	}

	public void novaConexao(Formulario formulario) {
		ConexaoContainer container = new ConexaoContainer(null, formulario);
		addTab(Constantes.LABEL_CONEXAO, Constantes.LABEL_CONEXAO_MIN, container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.CONEXAO);
		setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_CONEXAO));
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);

		container.ini(getGraphics());
	}

	public void novoFragmento(Formulario formulario) {
		FragmentoContainer container = new FragmentoContainer(null, null);
		addTab(Constantes.LABEL_FRAGMENTO, Constantes.LABEL_FRAGMENTO_MIN, container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.FRAGMENTO);
		setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_FRAGMENTO));
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);

		container.ini(getGraphics());
	}

	public void novoMapeamento(Formulario formulario) {
		MapeamentoContainer container = new MapeamentoContainer(null);
		addTab(Constantes.LABEL_MAPEAMENTOS, Constantes.LABEL_MAPEAMENTOS_MIN, container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.MAPEAMENTO);
		setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_MAPEAMENTOS));
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);

		container.ini(getGraphics());
	}

	public void novoVariaveis(Formulario formulario) {
		VariaveisContainer container = new VariaveisContainer(null);
		addTab(Constantes.LABEL_VARIAVEIS, Constantes.LABEL_VARIAVEIS_MIN, container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.VARIAVEIS);
		setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_VARIAVEIS));
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);

		container.ini(getGraphics());
	}

	public void novaComparacao(Formulario formulario) {
		ComparacaoContainer container = new ComparacaoContainer(null);
		addTab(Constantes.LABEL_COMPARACAO, Constantes.LABEL_COMPARACAO_MIN, container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.COMPARACAO);
		setToolTipTextAt(ultimoIndice, Mensagens.getString(Constantes.LABEL_COMPARACAO));
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);
	}

	private transient ObjetoContainerListener objetoContainerListener = new ObjetoContainerListener() {
		@Override
		public void buscaAutomatica(Grupo grupo, String argumentos) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void linkAutomatico(Link link, String argumento) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void configAlturaAutomatica(int total) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setTitulo(String titulo) {
			LOG.log(Level.FINEST, titulo);
		}

		@Override
		public Dimension getDimensoes() {
			return getSize();
		}
	};

	public void novoObjeto(Formulario formulario, Conexao padrao, Objeto objeto) {
		ObjetoContainer container = new ObjetoContainer(null, formulario, padrao, objeto, objetoContainerListener,
				getGraphics(), false);
		addTab(objeto.getId(), container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.OBJETO);
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);
		container.setSuporte(this);

		container.ini(getGraphics());
	}

	public void abrirFormulario(Formulario formulario, File file, List<Objeto> objetos, List<Relacao> relacoes,
			List<Form> forms, StringBuilder sbConexao, Dimension d) {
		ContainerFormulario form = new ContainerFormulario(formulario, file);
		form.abrir(file, objetos, relacoes, forms, sbConexao, getGraphics(), d);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public void destacarEmFormulario(Formulario formulario, Container container) {
		int indice = getIndice(container);

		if (indice == -1) {
			return;
		}

		super.remove(indice);

		File file = container.getArquivo();

		if (file == null) {
			file = new File(Constantes.DESTACADO);
		}

		ContainerFormulario form = new ContainerFormulario(formulario, container, file);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public void retornoDestacarEmFormulario(Formulario formulario, Container container) {
		File file = container.getArquivo();

		if (file == null) {
			file = new File(Constantes.DESTACADO);
		}

		addTab(file.getName(), container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.OBJETOS);
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);
		container.estadoSelecao();
	}

	public Container novo(Formulario formulario) {
		Container container = new Container(formulario, null);
		container.getSuperficie().setAbortarFecharComESC(Preferencias.isAbortarFecharComESC());
		addTab(Mensagens.getString(Constantes.LABEL_NOVO), container);
		container.setAbortarFecharComESCSuperficie(true);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.OBJETOS);
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);
		container.estadoSelecao();

		return container;
	}

	public void abrir(Formulario formulario, File file, List<Objeto> objetos, List<Relacao> relacoes, List<Form> forms,
			StringBuilder sbConexao, Dimension d) {

		if (file.getName().equalsIgnoreCase(Constantes.FVF_SEPARADOR)) {
			addTab(null, null);
			int ultimoIndice = getTabCount() - 1;
			TituloAbaS tituloAba = new TituloAbaS(this, file);
			setTabComponentAt(ultimoIndice, tituloAba);
			setBackgroundAt(ultimoIndice, Color.MAGENTA);
			setEnabledAt(ultimoIndice, false);
			return;
		}

		Container container = novo(formulario);
		container.abrir(file, objetos, relacoes, forms, sbConexao, getGraphics(), d);
		setToolTipTextAt(getTabCount() - 1, file.getAbsolutePath());
		setTitleAt(getTabCount() - 1, file.getName());
	}

	public void abrirExportacaoMetadado(Formulario formulario, Metadado metadado, boolean circular) {
		Container container = novo(formulario);
		container.abrirExportacaoImportacaoMetadado(metadado, true, circular);
		setTitleAt(getTabCount() - 1, Mensagens.getString("label.abrir_exportacao"));
	}

	public void abrirImportacaoMetadado(Formulario formulario, Metadado metadado, boolean circular) {
		Container container = novo(formulario);
		container.abrirExportacaoImportacaoMetadado(metadado, false, circular);
		setTitleAt(getTabCount() - 1, Mensagens.getString("label.abrir_importacao"));
	}

	@Override
	public void remove(int index) {
		Component cmp = getComponentAt(index);

		if (cmp instanceof Container) {
			((Container) cmp).excluido();
		}

		super.remove(index);
	}

	public void selecionarAba(File file) {
		int indice = getIndice(file);

		if (indice >= 0) {
			setSelectedIndex(indice);
		}
	}

	public void fecharArquivo(File file) {
		if (file == null || !file.isFile()) {
			return;
		}

		int indice = getIndice(file);

		while (indice >= 0) {
			remove(indice);
			indice = getIndice(file);
		}
	}

	public boolean isAberto(File file) {
		return getIndice(file) >= 0;
	}

	public boolean isAtivo(File file) {
		int pos = getIndice(file);
		int sel = getSelectedIndex();
		return pos != -1 && pos == sel;
	}

	public int getIndice(File file) {
		int total = getTabCount();

		for (int i = 0; i < total; i++) {
			try {
				Component cmp = getComponentAt(i);

				if (cmp instanceof Container) {
					Container c = (Container) cmp;

					if (c.getArquivo() != null && file != null
							&& c.getArquivo().getAbsolutePath().equals(file.getAbsolutePath())) {
						return i;
					}
				}
			} catch (Exception e) {
				LOG.log(Level.SEVERE, Constantes.ERRO, e);
			}
		}

		return -1;
	}

	public int getIndice(Component cmpConteudo) {
		int total = getTabCount();

		if (cmpConteudo != null) {
			for (int i = 0; i < total; i++) {
				Component cmp = getComponentAt(i);

				if (cmpConteudo == cmp) {
					return i;
				}
			}
		}

		return -1;
	}

	public int getIndice(Container c) {
		int total = getTabCount();

		for (int i = 0; i < total; i++) {
			Component cmp = getComponentAt(i);

			if ((cmp instanceof Container) && cmp == c) {
				return i;
			}
		}

		return -1;
	}

	public void selecionarConexao(Conexao conexao) {
		int total = getTabCount();

		for (int i = 0; i < total; i++) {
			Component cmp = getComponentAt(i);

			if (cmp instanceof IFicharioConexao) {
				IFicharioConexao aba = (IFicharioConexao) cmp;
				aba.selecionarConexao(conexao);
			}
		}
	}

	public void salvarAbertos() {
		try (PrintWriter pw = new PrintWriter(Constantes.ABERTOS_FICHARIO)) {
			int total = getTabCount();

			for (int i = 0; i < total; i++) {
				Component aba = getTabComponentAt(i);
				Component cmp = getComponentAt(i);

				File file = null;

				if (cmp instanceof IFicharioSalvar) {
					file = ((IFicharioSalvar) cmp).getFileSalvarAberto();
				} else if (aba instanceof IFicharioSalvar) {
					file = ((IFicharioSalvar) aba).getFileSalvarAberto();
				}

				if (file != null) {
					pw.print(file.getAbsolutePath() + Constantes.QL2);
				}
			}

		} catch (Exception ex) {
			LOG.log(Level.SEVERE, ex.getMessage());
		}
	}

	public static interface IFicharioSalvar {
		File getFileSalvarAberto();
	}

	public static interface IFicharioConexao {
		void selecionarConexao(Conexao conexao);
	}

	public void abrirArquivos(Formulario formulario) {
		File file = new File(Constantes.ABERTOS_FICHARIO);

		if (file.exists()) {
			List<File> files = new ArrayList<>();

			try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
				String linha = br.readLine();

				while (linha != null) {
					files.add(new File(linha));
					linha = br.readLine();
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage("FICHARIO", ex, Fichario.this);
			}

			for (File f : files) {
				abrirArquivo(f, formulario);
			}
		}
	}

	private void abrirArquivo(File f, Formulario formulario) {
		String nome = f.getAbsolutePath();
		int pos = nome.indexOf(Constantes.III);

		if (pos != -1) {
			nome = nome.substring(pos + Constantes.III.length());
		}

		if (Util.iguais(AnexoContainer.class, nome)) {
			novoAnexo(formulario);

		} else if (Util.iguais(ArvoreContainer.class, nome)) {
			novaArvore(formulario);

		} else if (Util.iguais(ConexaoContainer.class, nome)) {
			novaConexao(formulario);

		} else if (Util.iguais(MetadadosContainer.class, nome)) {
			novoMetadado(formulario, null);

		} else if (Util.iguais(ConsultaContainer.class, nome)) {
			novaConsulta(formulario, null);

		} else if (Util.iguais(UpdateContainer.class, nome)) {
			novoUpdate(formulario, null);

		} else if (Util.iguais(AnotacaoContainer.class, nome)) {
			novaAnotacao(formulario);

		} else if (Util.iguais(FragmentoContainer.class, nome)) {
			novoFragmento(formulario);

		} else {
			abrirArquivo(f, formulario, nome);
		}
	}

	private void abrirArquivo(File f, Formulario formulario, String nome) {
		if (Util.iguais(MapeamentoContainer.class, nome)) {
			novoMapeamento(formulario);

		} else if (Util.iguais(VariaveisContainer.class, nome)) {
			novoVariaveis(formulario);

		} else if (Util.iguais(ComparacaoContainer.class, nome)) {
			novaComparacao(formulario);

		} else if (Util.iguais(RequisicaoContainer.class, nome)) {
			novaRequisicao(formulario);

		} else if (Util.iguais(ConfigContainer.class, nome)) {
			novoConfig(formulario);

		} else if (Util.iguais(Desktop.class, nome)) {
			novoDesktop(formulario);

		} else {
			formulario.abrirArquivo(f, true);
		}
	}

	private class Listener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			int indice = indexAtLocation(x, y);
			ponto = new Ponto(x, y);
			ultX = x;
			ultY = y;

			if (indice != -1) {
				rectangle = getBoundsAt(indice);
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (rectangle != null) {
				int recX = e.getX();
				int recY = e.getY();
				rectangle.x += recX - ultX;
				rectangle.y += recY - ultY;
				ultX = recX;
				ultY = recY;
				repaint();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			rectangle = null;

			if (ponto == null) {
				repaint();
				return;
			}

			int destino = indexAtLocation(e.getX(), e.getY());
			int origem = indexAtLocation(ponto.x, ponto.y);

			if (origem != -1 && destino != -1 && origem != destino) {
				inverter(origem, destino);
			}

			repaint();
		}

		private void inverter(int origem, int destino) {
			Component aba = getTabComponentAt(origem);
			Component cmp = getComponentAt(origem);
			String hint = getToolTipTextAt(origem);
			String titulo = getTitleAt(origem);
			Icon icon = getIconAt(origem);
			remove(origem);

			insertTab(titulo, icon, cmp, hint, destino);
			setTabComponentAt(destino, aba);

			if (aba instanceof TituloAbaS) {
				setEnabledAt(destino, false);
			} else {
				setSelectedIndex(destino);
			}
		}
	}

	private class Ponto {
		final int x;
		final int y;

		Ponto(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	public void fecharTodos() {
		int count = getTabCount();

		while (count > 0) {
			removeTabAt(0);
			count = getTabCount();
		}
	}
}