package br.com.persist.painel;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.Popup;

public class Fichario extends JTabbedPane {
	private static final Logger LOG = Logger.getGlobal();
	private transient Setor nor = new Setor(Setor.NORTE);
	private transient Setor les = new Setor(Setor.LESTE);
	private transient Setor oes = new Setor(Setor.OESTE);
	private transient FicharioListener ficharioListener;
	private transient Setor sul = new Setor(Setor.SUL);
	private transient Inclusao inc = new Inclusao();
	private static final long serialVersionUID = 1L;
	private final PopupFichario popupFichario;

	public Fichario() {
		setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		new DropTarget(this, dropTargetListener);
		addMouseListener(mouseListenerFichario);
		popupFichario = new PopupFichario();
		DragSource dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(this, Transferivel.ACAO_VALIDA, dge -> {
			int indice = getSelectedIndex();
			if (indice != -1) {
				Transferivel aba = (Transferivel) getComponentAt(indice);
				aba.setTitle(getTitleAt(indice));
				aba.setIndex(indice);
				dge.startDrag(null, aba, dragSourceListener);
			}
		});
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		nor.paint(g);
		sul.paint(g);
		les.paint(g);
		oes.paint(g);
		inc.paint(g);
	}

	private transient DropTargetListener dropTargetListener = new DropTargetListener() {
		@Override
		public void dropActionChanged(DropTargetDragEvent e) {
			if (!Transferivel.acaoValida(e.getDropAction())) {
				e.rejectDrag();
				invalidar();
			}
		}

		@Override
		public void dragEnter(DropTargetDragEvent e) {
			if (!Transferivel.acaoValida(e.getDropAction())) {
				e.rejectDrag();
				invalidar();
			} else {
				nor.localizar(Fichario.this);
				sul.localizar(Fichario.this);
				les.localizar(Fichario.this);
				oes.localizar(Fichario.this);
				inc.localizar(Fichario.this);
				repaint();
			}
		}

		@Override
		public void dragOver(DropTargetDragEvent e) {
			Point p = e.getLocation();
			int x = p.x;
			int y = p.y;
			nor.selecionado = nor.contem(x, y);
			sul.selecionado = sul.contem(x, y);
			les.selecionado = les.contem(x, y);
			oes.selecionado = oes.contem(x, y);
			inc.selecionado = inc.contem(x, y);
			repaint();
		}

		@Override
		public void dragExit(DropTargetEvent dte) {
			invalidar();
		}

		private void invalidar() {
			nor.valido = false;
			sul.valido = false;
			les.valido = false;
			oes.valido = false;
			inc.valido = false;
			repaint();
		}

		@Override
		public void drop(DropTargetDropEvent e) {
			if (!Transferivel.acaoValida(e.getDropAction())) {
				e.rejectDrop();
				invalidar();
			} else {
				Transferable transferable = e.getTransferable();
				if (transferable != null) {
					DataFlavor[] flavors = transferable.getTransferDataFlavors();
					if (flavors != null && flavors.length > 0) {
						processarArrastado(e, transferable, flavors[0]);
					}
				}
			}
		}

		private void processarArrastado(DropTargetDropEvent e, Transferable transferable, DataFlavor flavor) {
			if (Transferivel.flavor.equals(flavor)) {
				try {
					Transferivel objeto = (Transferivel) transferable.getTransferData(flavor);
					Setor setor = Setor.get(e, nor, sul, les, oes, inc);
					if (valido(objeto, setor)) {
						e.acceptDrop(Transferivel.ACAO_VALIDA);
						e.dropComplete(true);
						SwingUtilities.invokeLater(() -> setor.processar(objeto, Fichario.this));
					} else {
						e.rejectDrop();
					}
					invalidar();
				} catch (Exception ex) {
					Util.stackTraceAndMessage("SOLTAR OBJETO", ex, Fichario.this);
				}
			}
		}

		private boolean valido(Transferivel objeto, Setor setor) {
			if (objeto == null || setor == null) {
				return false;
			}
			if (Inclusao.INSERT == setor.local && contemObjeto(objeto)) {
				return false;
			}
			return !(getTabCount() == 1 && getComponentAt(0) == objeto);
		}

		private boolean contemObjeto(Transferivel objeto) {
			for (int i = 0; i < getTabCount(); i++) {
				if (getComponentAt(i) == objeto) {
					return true;
				}
			}
			return false;
		}
	};

	private transient DragSourceListener dragSourceListener = new DragSourceListener() {
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
			if (dsde.getDropSuccess()) {
				DragSourceContext context = (DragSourceContext) dsde.getSource();
				Transferivel objeto = (Transferivel) context.getTransferable();
				remove(objeto);
				checarVazio();
			}
		}
	};

	public void checarVazio() {
		if (getTabCount() == 0 && ficharioListener != null) {
			ficharioListener.ficharioVazio(this);
		}
	}

	public FicharioListener getFicharioListener() {
		return ficharioListener;
	}

	public void setFicharioListener(FicharioListener ficharioListener) {
		this.ficharioListener = ficharioListener;
	}

	private transient MouseListener mouseListenerFichario = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			processar(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			processar(e);
		}

		private void processar(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popupFichario.show(Fichario.this, e.getX(), e.getY());
			}
		}
	};

	private class PopupFichario extends Popup {
		private static final long serialVersionUID = 1L;
		private Action fechar = Action.actionMenu("label.fechar", null);

		PopupFichario() {
			addMenuItem(fechar);
			fechar.setActionListener(e -> fechar());
		}

		private void fechar() {
			int indice = getSelectedIndex();
			if (indice != -1) {
				removeTabAt(indice);
				checarVazio();
			}
		}
	}
}

class Inclusao extends Setor {
	static final char INSERT = 'I';

	Inclusao() {
		super(INSERT);
	}

	@Override
	void localizar(Component c) {
		valido = true;
	}

	@Override
	void paint(Graphics g) {
		if (!valido) {
			return;
		}
		g.drawRect(x, y, larguraAltura, larguraAltura);
		if (selecionado) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
			g.fillRect(x, y, larguraAltura, larguraAltura);
		}
	}

	@Override
	void processar(Transferivel objeto, Fichario dropTarget) {
		dropTarget.addTab(objeto.getTitle(), objeto);
		int indice = dropTarget.getTabCount() - 1;
		dropTarget.setSelectedIndex(indice);
	}
}