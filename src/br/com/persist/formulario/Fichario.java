package br.com.persist.formulario;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JTabbedPane;

import br.com.persist.Objeto;
import br.com.persist.Relacao;
import br.com.persist.banco.Conexao;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class Fichario extends JTabbedPane {
	private static final long serialVersionUID = 1L;
	private final Listener listener = new Listener();
	private Rectangle rectangle;
	private Ponto ponto;
	private int ultX;
	private int ultY;

	public Fichario() {
		setTabLayoutPolicy(SCROLL_TAB_LAYOUT);
		new DropTarget(this, listenerSoltar);
		addMouseMotionListener(listener);
		addMouseListener(listener);
	}

	private DropTargetListener listenerSoltar = new DropTargetListener() {
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

	public void destacar(Formulario formulario, Conexao conexao, List<Objeto> objetos) {
		boolean continua = false;

		for (Objeto objeto : objetos) {
			if (!Util.estaVazio(objeto.getTabela())) {
				continua = true;
				break;
			}
		}

		if (!continua) {
			return;
		}

		Desktop desktop = novoDesktop(formulario);

		int x = 10;
		int y = 10;

		for (Objeto objeto : objetos) {
			if (!Util.estaVazio(objeto.getTabela())) {
				Object[] array = criarArray(conexao, objeto);
				desktop.addForm(array, new Point(x, y));
				x += 25;
				y += 25;
			}
		}
	}

	private Object[] criarArray(Conexao conexao, Objeto objeto) {
		Superficie.setComplemento(conexao, objeto);
		return new Object[] { objeto, conexao, new Dimension(400, 250) };
	}

	public Desktop novoDesktop(Formulario formulario) {
		Desktop desktop = new Desktop(formulario);
		addTab(Mensagens.getString("label.desktop"), desktop);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.DESKTOP);
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);

		return desktop;
	}

	public void novo(Formulario formulario) {
		Container container = new Container(formulario);
		addTab(Mensagens.getString("label.novo"), container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.OBJETOS);
		setTabComponentAt(ultimoIndice, tituloAba);
		setSelectedIndex(ultimoIndice);
		container.estadoSelecao();
	}

	public void abrir(Formulario formulario, File file, List<Objeto> objetos, List<Relacao> relacoes) {
		Container container = new Container(formulario);
		container.abrir(file, objetos, relacoes);
		addTab(Mensagens.getString("label.novo"), container);
		int ultimoIndice = getTabCount() - 1;

		TituloAba tituloAba = new TituloAba(this, TituloAba.OBJETOS);
		setTabComponentAt(ultimoIndice, tituloAba);
		setTitleAt(ultimoIndice, file.getName());
		setSelectedIndex(ultimoIndice);
		container.estadoSelecao();
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