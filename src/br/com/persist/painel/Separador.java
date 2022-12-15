package br.com.persist.painel;

import java.awt.Component;
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

import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import br.com.persist.assistencia.Util;

public class Separador extends JSplitPane {
	private transient Setor nor = new Setor(Setor.NORTE);
	private transient Setor les = new Setor(Setor.LESTE);
	private transient Setor oes = new Setor(Setor.OESTE);
	private transient Setor sul = new Setor(Setor.SUL);
	private static final long serialVersionUID = 1L;

	public Separador(int orientation, Component left, Component right) {
		super(orientation, left, right);
		new DropTarget(this, dropTargetListener);
		SwingUtilities.invokeLater(() -> setDividerLocation(0.5));
	}

	public static Separador horizontal(Component left, Component right) {
		return new Separador(HORIZONTAL_SPLIT, left, right);
	}

	public static Separador vertical(Component left, Component right) {
		return new Separador(VERTICAL_SPLIT, left, right);
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
				nor.localizar(Separador.this);
				sul.localizar(Separador.this);
				les.localizar(Separador.this);
				oes.localizar(Separador.this);
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
						e.acceptDrop(DnDConstants.ACTION_MOVE);
						e.dropComplete(true);
						processar(objeto, setor);
					} else {
						e.rejectDrop();
					}
					invalidar();
				} catch (Exception ex) {
					Util.stackTraceAndMessage("SOLTAR OBJETO", ex, Separador.this);
				}
			}
		}

		private boolean valido(Transferivel objeto, Setor setor) {
			return objeto != null && setor != null;
		}

		private void processar(Transferivel objeto, Setor setor) {
			// TODO
		}
	};
}