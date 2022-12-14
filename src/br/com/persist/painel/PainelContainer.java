package br.com.persist.painel;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JPanel;

import br.com.persist.assistencia.Util;

public class PainelContainer extends JPanel {
	private static final long serialVersionUID = 1L;
	private transient PainelSetor nor = new PainelSetor('N');
	private transient PainelSetor sul = new PainelSetor('S');
	private transient PainelSetor les = new PainelSetor('L');
	private transient PainelSetor oes = new PainelSetor('O');

	public PainelContainer() {
		new DropTarget(this, dropTargetListener);
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
		private boolean acaoValida(int acao) {
			return (acao & DnDConstants.ACTION_MOVE) != 0;
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent e) {
			if (!acaoValida(e.getDropAction())) {
				e.rejectDrag();
				invalidar();
			}
		}

		@Override
		public void dragEnter(DropTargetDragEvent e) {
			if (!acaoValida(e.getDropAction())) {
				e.rejectDrag();
				invalidar();
			} else {
				nor.localizar(PainelContainer.this);
				sul.localizar(PainelContainer.this);
				les.localizar(PainelContainer.this);
				oes.localizar(PainelContainer.this);
				repaint();
			}
		}

		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			Point p = dtde.getLocation();
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
			if (!acaoValida(e.getDropAction())) {
				e.rejectDrop();
				invalidar();
			} else {
				Transferable transferable = e.getTransferable();
				if (transferable != null) {
					DataFlavor[] flavors = transferable.getTransferDataFlavors();
					if (flavors != null && flavors.length > 0) {
						processarArrastado(e, transferable, flavors);
					}
				}
			}
		}

		private void processarArrastado(DropTargetDropEvent e, Transferable transferable, DataFlavor[] flavors) {
			DataFlavor flavor = flavors[0];
			AtomicBoolean processado = new AtomicBoolean(false);
			processarTransferable(e, transferable, flavor, processado);
			processarTransferableFinal(e, processado);
		}

		private void processarTransferable(DropTargetDropEvent e, Transferable transferable, DataFlavor flavor,
				AtomicBoolean processado) {
			if (PainelTransferable.flavor.equals(flavor)) {
				try {
					PainelTransferable objeto = (PainelTransferable) transferable.getTransferData(flavor);
					processado.set(true);
				} catch (Exception ex) {
					Util.stackTraceAndMessage("SOLTAR OBJETO", ex, PainelContainer.this);
				}
			}
		}

		private void processarTransferableFinal(DropTargetDropEvent e, AtomicBoolean completado) {
			if (completado.get()) {
				e.acceptDrop(DnDConstants.ACTION_MOVE);
				e.dropComplete(true);
			} else {
				e.rejectDrop();
			}
			invalidar();
		}
	};
}