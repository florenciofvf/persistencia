package br.com.persist.painel;

import java.awt.Graphics;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTabbedPane;

import br.com.persist.assistencia.Util;

public class Fichario extends JTabbedPane {
	private static final Logger LOG = Logger.getGlobal();
	private transient Setor nor = new Setor(Setor.NORTE);
	private transient Setor les = new Setor(Setor.LESTE);
	private transient Setor oes = new Setor(Setor.OESTE);
	private transient Setor sul = new Setor(Setor.SUL);
	private static final long serialVersionUID = 1L;

	public Fichario() {
		new DropTarget(this, dropTargetListener);
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
					Setor setor = Setor.get(e, nor, sul, les, oes);
					if (valido(objeto, setor)) {
						e.acceptDrop(Transferivel.ACAO_VALIDA);
						e.dropComplete(true);
						setor.processar(objeto, Fichario.this);
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
			return !(getTabCount() == 1 && getComponentAt(0) == objeto);
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
				if (objeto.getIndex() >= 0 && objeto.getIndex() < getTabCount()) {
					removeTabAt(objeto.getIndex());
				} else {
					throw new IllegalStateException();
				}
			}
		}
	};
}