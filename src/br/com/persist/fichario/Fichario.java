package br.com.persist.fichario;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicArrowButton;

import br.com.persist.abstrato.FabricaContainer;
import br.com.persist.abstrato.WindowHandler;
import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;
import br.com.persist.formulario.FormularioEvento;

public class Fichario extends JTabbedPane implements WindowHandler {
	private final transient Navegacao navegacaoEsquerdo = new Navegacao(ESQUERDO);
	private final transient Navegacao navegacaoDireito = new Navegacao(DIREITO);
	private final transient Navegacao navegacaoLimpar = new Navegacao(LIMPAR);
	private transient Map<String, Object> args = new HashMap<>();
	private final transient NavegacaoListener navegacaoListener;
	private final transient Listener listener = new Listener();
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private static final int ESQUERDO = 1;
	private static final int DIREITO = 2;
	private static final int LIMPAR = 0;
	private transient Ponto ponto;
	private Rectangle rectangle;
	private int ultX;
	private int ultY;

	public Fichario() {
		navegacaoListener = new NavegacaoListener();
		setTabLayoutPolicy(SCROLL_TAB_LAYOUT);
		new DropTarget(this, listenerSoltar);
		addMouseMotionListener(listener);
		addMouseListener(listener);
		add(navegacaoLimpar);
		add(navegacaoEsquerdo);
		add(navegacaoDireito);
		configurar();
	}

	public void ativarNavegacao() {
		navegacaoListener.inicializar();
		removeChangeListener(navegacaoListener);
		addChangeListener(navegacaoListener);
	}

	public class Navegacao extends BasicArrowButton implements UIResource, SwingConstants {
		private static final long serialVersionUID = 1L;
		final int tipo;

		public Navegacao(int tipo) {
			super(tipo == LIMPAR || tipo == ESQUERDO ? WEST : EAST, UIManager.getColor("TabbedPane.selected"),
					UIManager.getColor("TabbedPane.shadow"), UIManager.getColor("TabbedPane.darkShadow"),
					UIManager.getColor("TabbedPane.highlight"));
			addActionListener(e -> click());
			if (tipo == LIMPAR) {
				setDirection(0);
			}
			this.tipo = tipo;
		}

		private void click() {
			if (tipo == LIMPAR) {
				if (Util.confirmar(Fichario.this, "msg.confirmar_limpar_cache")) {
					navegacaoListener.inicializar();
				}
			} else if (tipo == ESQUERDO) {
				navegacaoListener.voltar();
			} else {
				navegacaoListener.avancar();
			}
		}

		private void checarEstado() {
			if (tipo == LIMPAR) {
				setEnabled(!navegacaoListener.esquerdo.isEmpty() || !navegacaoListener.direito.isEmpty());
			} else if (tipo == ESQUERDO) {
				setEnabled(!navegacaoListener.esquerdo.isEmpty());
			} else {
				setEnabled(!navegacaoListener.direito.isEmpty());
			}
		}

		@Override
		public void setBounds(int x, int y, int width, int height) {
			int xAux = -1;
			if (tipo == LIMPAR) {
				xAux = 0;
			} else if (tipo == ESQUERDO) {
				xAux = 15;
			} else {
				xAux = 30;
			}
			super.setBounds(xAux, 0, 15, 10);
		}
	}

	private class NavegacaoListener implements ChangeListener {
		private final Deque<Integer> esquerdo;
		private final Deque<Integer> direito;
		private boolean habilitado;
		private Integer ultimo;

		public NavegacaoListener() {
			esquerdo = new ArrayDeque<>();
			direito = new ArrayDeque<>();
			habilitado = true;
		}

		private void checarEstadoNavegacao() {
			if (getTabCount() < 1) {
				navegacaoListener.esquerdo.clear();
				navegacaoListener.direito.clear();
			}
			navegacaoEsquerdo.checarEstado();
			navegacaoDireito.checarEstado();
			navegacaoLimpar.checarEstado();
		}

		private void inicializar() {
			ultimo = getSelectedIndex();
			habilitado = true;
			esquerdo.clear();
			direito.clear();
			checarEstadoNavegacao();
		}

		private void push(Deque<Integer> deque, Integer i) {
			if (deque == null || i == null || i == -1) {
				return;
			}
			if (deque.isEmpty()) {
				deque.push(i);
			} else {
				Integer a = deque.peek();
				if (!a.equals(i)) {
					deque.push(i);
				}
			}
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			tabSelected(getSelectedIndex());
			if (habilitado) {
				direito.clear();
				push(esquerdo, ultimo);
				ultimo = getSelectedIndex();
				checarEstadoNavegacao();
			}
		}

		private void voltar() {
			if (!esquerdo.isEmpty()) {
				Integer indice = esquerdo.pop();
				if (indice < getTabCount()) {
					habilitado = false;
					Integer ult = getSelectedIndex();
					setSelectedIndex(indice);
					if (!indice.equals(ult)) {
						push(direito, ult);
					}
					habilitado = true;
				}
			}
			checarEstadoNavegacao();
		}

		private void avancar() {
			if (!direito.isEmpty()) {
				Integer indice = direito.pop();
				if (indice < getTabCount()) {
					habilitado = false;
					Integer ult = getSelectedIndex();
					setSelectedIndex(indice);
					if (!indice.equals(ult)) {
						push(esquerdo, ult);
					}
					habilitado = true;
				}
			}
			checarEstadoNavegacao();
		}
	}

	private void configurar() {
		inputMap().put(getKeyStroke(KeyEvent.VK_Q), "excluir_action");
		getActionMap().put("excluir_action", excluirAction);
	}

	public Component getThis() {
		return this;
	}

	private transient Action excluirAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			int indice = getSelectedIndex();
			if (indice != -1) {
				excluirPagina(indice);
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

	public synchronized int getIndice(Component component) {
		for (int i = 0; i < getTabCount(); i++) {
			if (getComponentAt(i) == component) {
				return i;
			}
		}
		return -1;
	}

	public synchronized int getIndice(File file) {
		for (int i = 0; i < getTabCount(); i++) {
			Pagina pagina = getPagina(i);
			if (pagina == null) {
				continue;
			}
			File paginaFile = pagina.getFile();
			if (paginaFile != null && file != null && paginaFile.getAbsolutePath().equals(file.getAbsolutePath())) {
				return i;
			}
			if (Util.igual(paginaFile, file)) {
				return i;
			}
		}
		return -1;
	}

	public void processar(Formulario formulario, Map<String, Object> args) {
		Boolean fechar = (Boolean) args.get(FormularioEvento.FECHAR_FORMULARIO);
		if (Boolean.TRUE.equals(fechar)) {
			fechandoFormulario();
		} else {
			for (int i = 0; i < getTabCount(); i++) {
				Pagina p = getPagina(i);
				if (p != null) {
					p.processar(formulario, args);
				}
			}
		}
	}

	public synchronized int getIndice(Pagina pagina) {
		for (int i = 0; i < getTabCount(); i++) {
			Pagina p = getPagina(i);
			if (p == pagina) {
				return i;
			}
		}
		return -1;
	}

	public synchronized void fecharTodos() {
		while (getTabCount() > 0) {
			excluirPagina(0);
		}
	}

	public synchronized boolean liberarPagina(Pagina pagina) {
		int indice = getIndice(pagina);
		if (indice == -1) {
			return false;
		}
		remove(indice);
		return true;
	}

	public synchronized boolean excluirPagina(Pagina pagina) {
		int indice = getIndice(pagina);
		if (indice == -1) {
			return false;
		}
		pagina.excluindoDoFichario(this);
		remove(indice);
		return true;
	}

	public synchronized void excluirPagina(int indice) {
		Pagina pagina = getPagina(indice);
		if (pagina != null) {
			pagina.excluindoDoFichario(this);
			remove(indice);
		}
	}

	public synchronized void adicionarPagina(Pagina pagina) throws ArgumentoException {
		if (pagina == null) {
			throw new ArgumentoException("pagina nula.");
		}
		Titulo titulo = pagina.getTitulo();
		String title = Preferencias.isTituloAbaMin() ? titulo.getTituloMin() : titulo.getTitulo();
		addTab(title, pagina.getComponent());
		int ultimoIndice = getTabCount() - 1;
		Cabecalho cabecalho = new Cabecalho(this, pagina);
		setToolTipTextAt(ultimoIndice, titulo.getHint());
		setEnabledAt(ultimoIndice, titulo.isAtivo());
		setTabComponentAt(ultimoIndice, cabecalho);
		if (pagina.getComponent() != null) {
			setSelectedIndex(ultimoIndice);
		}
		pagina.adicionadoAoFichario(this);
	}

	public void fechandoFormulario() {
		try (PrintWriter pw = new PrintWriter(Constantes.PERSISTENCIA_FVF, StandardCharsets.UTF_8.name())) {
			int total = getTabCount();
			for (int i = 0; i < total; i++) {
				Pagina pagina = getPagina(i);
				if (pagina != null) {
					pw.print(pagina.getClasseFabrica().getName() + Constantes.III + pagina.getStringPersistencia()
							+ Constantes.QL);
				}
			}
		} catch (Exception ex) {
			Util.stackTraceAndMessage("SALVAR PAGINAS", ex, Fichario.this);
			LOG.log(Level.SEVERE, ex.getMessage());
		}
	}

	public void tabSelected(int i) {
		if (i >= 0 && i < getTabCount()) {
			Pagina pagina = getPagina(i);
			if (pagina != null) {
				pagina.tabActivatedHandler(this);
				args.put(FicharioEvento.PAGINA_SELECIONADA, pagina);
				for (int j = 0; j < getTabCount(); j++) {
					Pagina p = getPagina(j);
					if (p != null) {
						p.processar(null, args);
					}
				}
			}
		}
	}

	@Override
	public void windowActivatedHandler(Window window) {
		tabSelected(getSelectedIndex());
	}

	@Override
	public void windowClosingHandler(Window window) {
		LOG.log(Level.FINEST, "windowClosingHandler");
	}

	@Override
	public void windowOpenedHandler(Window window) {
		File file = new File(Constantes.PERSISTENCIA_FVF);
		if (file.exists()) {
			List<String> linhas = new ArrayList<>();
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
				String linha = br.readLine();
				while (!Util.isEmpty(linha)) {
					linhas.add(linha);
					linha = br.readLine();
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage("RESTAURAR PAGINAS", ex, Fichario.this);
			}
			for (String s : linhas) {
				try {
					restaurarPagina((Formulario) window, s);
				} catch (ArgumentoException ex) {
					Util.stackTraceAndMessage("RESTAURAR PAGINAS", ex, Fichario.this);
				}
			}
		}
	}

	private synchronized void restaurarPagina(Formulario formulario, String linha) throws ArgumentoException {
		int pos = linha.indexOf(Constantes.III);
		String classeFabrica = linha.substring(0, pos);
		String stringPersistencia = linha.substring(pos + Constantes.III.length());
		FabricaContainer fabrica = formulario.getFabrica(classeFabrica);
		if (fabrica != null) {
			PaginaServico servico = fabrica.getPaginaServico();
			Pagina pagina = servico.criarPagina(formulario, stringPersistencia);
			adicionarPagina(pagina);
		}
	}

	public synchronized Pagina getPagina(int i) {
		Component tab = getTabComponentAt(i);
		if (tab == null) {
			return null;
		}
		Cabecalho cabecalho = (Cabecalho) tab;
		return cabecalho.getPagina();
	}

	public void selecionarPagina(File file) {
		int indice = getIndice(file);
		if (indice >= 0) {
			setSelectedIndex(indice);
		}
	}

	public void fecharArquivo(File file) {
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
			Component tab = getTabComponentAt(origem);
			Component cmp = getComponentAt(origem);
			String hint = getToolTipTextAt(origem);
			String titulo = getTitleAt(origem);
			Pagina pagina = getPagina(origem);
			Icon icon = getIconAt(origem);
			remove(origem);
			insertTab(titulo, icon, cmp, hint, destino);
			setTabComponentAt(destino, tab);
			if (pagina.getComponent() == null) {
				setEnabledAt(destino, false);
			} else {
				pagina.invertidoNoFichario(Fichario.this);
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
}