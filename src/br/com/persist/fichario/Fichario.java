package br.com.persist.fichario;

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
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import br.com.persist.banco.Conexao;
import br.com.persist.comp.Panel;
import br.com.persist.comp.ScrollPane;
import br.com.persist.container.AnotacaoContainer;
import br.com.persist.container.ArvoreContainer;
import br.com.persist.container.ConexaoContainer;
import br.com.persist.container.ConfigContainer;
import br.com.persist.container.ConsultaContainer;
import br.com.persist.container.FragmentoContainer;
import br.com.persist.container.ObjetoContainer;
import br.com.persist.container.UpdateContainer;
import br.com.persist.desktop.Desktop;
import br.com.persist.desktop.Objeto;
import br.com.persist.desktop.Relacao;
import br.com.persist.desktop.Superficie;
import br.com.persist.formulario.DesktopFormulario;
import br.com.persist.formulario.SuperficieFormulario;
import br.com.persist.listener.ObjetoContainerListener;
import br.com.persist.principal.Formulario;
import br.com.persist.util.BuscaAuto.Grupo;
import br.com.persist.util.Constantes;
import br.com.persist.util.Form;
import br.com.persist.util.Mensagens;
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

	public void destacar(Formulario formulario, Conexao conexao, List<Objeto> objetos, int tipoContainer) {
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

		if (tipoContainer == Constantes.TIPO_CONTAINER_FORMULARIO) {
			destacarForm(formulario, objetos, conexao);
		} else if (tipoContainer == Constantes.TIPO_CONTAINER_DESKTOP) {
			destacarDesk(formulario, objetos, conexao);
		} else if (tipoContainer == Constantes.TIPO_CONTAINER_FICHARIO) {
			destacarObjt(formulario, objetos, conexao);
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
		desktop.distribuir(-20);
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

	public Desktop novoDesktop(Formulario formulario) {
		Desktop desktop = new Desktop(formulario, false);
		addTab(Mensagens.getString(Constantes.LABEL_DESKTOP), new ScrollPane(desktop));
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.DESKTOP);
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);

		return desktop;
	}

	public Panel novaAnotacao(Formulario formulario) {
		AnotacaoContainer container = new AnotacaoContainer(null);
		addTab(Mensagens.getString(Constantes.LABEL_ANOTACOES), container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.ANOTACAO);
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);

		return container;
	}

	public Panel novaConsulta(Formulario formulario, Conexao conexao) {
		ConsultaContainer container = new ConsultaContainer(null, formulario, conexao, null, null);
		addTab(Mensagens.getString(Constantes.LABEL_CONSULTA), container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.CONSULTA);
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);

		return container;
	}

	public Panel novoUpdate(Formulario formulario, Conexao conexao) {
		UpdateContainer container = new UpdateContainer(null, formulario, conexao, null, null);
		addTab(Mensagens.getString(Constantes.LABEL_ATUALIZAR), container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.UPDATE);
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);

		return container;
	}

	public void novaArvore(Formulario formulario) {
		ArvoreContainer container = new ArvoreContainer(formulario);
		addTab(Mensagens.getString("label.arquivos"), container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.ARVORE);
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);
	}

	public void novoConfig(Formulario formulario) {
		ConfigContainer container = new ConfigContainer(null, formulario);
		addTab(Mensagens.getString(Constantes.LABEL_CONFIGURACOES), container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.CONFIG);
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);
	}

	public void novo(Formulario formulario) {
		FicharioAbaContainer container = new FicharioAbaContainer(formulario);
		addTab(Mensagens.getString("label.novo"), container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.OBJETOS);
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);
		container.estadoSelecao();
	}

	public void novaConexao(Formulario formulario) {
		ConexaoContainer container = new ConexaoContainer(null, formulario);
		addTab(Mensagens.getString(Constantes.LABEL_CONEXAO), container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.CONEXAO);
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);

		container.ini(getGraphics());
	}

	public void novoFragmento(Formulario formulario) {
		FragmentoContainer container = new FragmentoContainer(null, null);
		addTab(Mensagens.getString(Constantes.LABEL_FRAGMENTO), container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.FRAGMENTO);
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);

		container.ini(getGraphics());
	}

	private transient ObjetoContainerListener objetoContainerListener = new ObjetoContainerListener() {
		@Override
		public void buscaAutomatica(Grupo grupo, String argumentos) {
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
		FicharioAbaContainer container = new FicharioAbaContainer(formulario);
		container.abrir(file, objetos, relacoes, forms, sbConexao, getGraphics(), d);

		SuperficieFormulario form = new SuperficieFormulario(formulario, container, file);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
		container.estadoSelecao();
	}

	public void abrir(Formulario formulario, File file, List<Objeto> objetos, List<Relacao> relacoes, List<Form> forms,
			StringBuilder sbConexao, Dimension d) {
		FicharioAbaContainer container = new FicharioAbaContainer(formulario);
		container.abrir(file, objetos, relacoes, forms, sbConexao, getGraphics(), d);
		addTab(Mensagens.getString("label.novo"), container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.OBJETOS);
		setTabComponentAt(ultimoIndice, tituloAba);
		setTitleAt(ultimoIndice, file.getName());
		setSelectedIndex(ultimoIndice);
		container.estadoSelecao();
	}

	@Override
	public void remove(int index) {
		Component cmp = getComponentAt(index);

		if (cmp instanceof FicharioAbaContainer) {
			((FicharioAbaContainer) cmp).excluido();
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

				if (cmp instanceof FicharioAbaContainer) {
					FicharioAbaContainer c = (FicharioAbaContainer) cmp;

					if (c.getArquivo() != null && file != null
							&& c.getArquivo().getAbsolutePath().equals(file.getAbsolutePath())) {
						return i;
					}
				}
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "ERRO", e);
			}
		}

		return -1;
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
			setSelectedIndex(destino);
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