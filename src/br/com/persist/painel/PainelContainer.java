package br.com.persist.painel;

import java.awt.BorderLayout;
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

import javax.swing.SwingUtilities;

import br.com.persist.assistencia.Util;
import br.com.persist.componente.Panel;

public class PainelContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private transient PainelSetor nor = new PainelSetor(PainelSetor.NORTE);
	private transient PainelSetor les = new PainelSetor(PainelSetor.LESTE);
	private transient PainelSetor oes = new PainelSetor(PainelSetor.OESTE);
	private transient PainelSetor sul = new PainelSetor(PainelSetor.SUL);

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
			if (!acaoValida(e.getDropAction())) {
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
			if (PainelTransferable.flavor.equals(flavor)) {
				try {
					PainelTransferable objeto = (PainelTransferable) transferable.getTransferData(flavor);
					PainelSetor setor = getSetor(e, nor, sul, les, oes);
					if (valido(objeto, setor)) {
						e.acceptDrop(DnDConstants.ACTION_MOVE);
						e.dropComplete(true);
						setor.processar(objeto, PainelContainer.this);
					} else {
						e.rejectDrop();
					}
					invalidar();
				} catch (Exception ex) {
					Util.stackTraceAndMessage("SOLTAR OBJETO", ex, PainelContainer.this);
				}
			}
		}

		private PainelSetor getSetor(DropTargetDropEvent e, PainelSetor... setores) {
			Point p = e.getLocation();
			for (PainelSetor setor : setores) {
				if (setor.contem(p.x, p.y)) {
					return setor;
				}
			}
			return null;
		}

		private boolean valido(PainelTransferable objeto, PainelSetor setor) {
			if (objeto == null || setor == null) {
				return false;
			}
			Component c = getRaiz();
			if (c instanceof PainelFichario) {
				PainelFichario fichario = (PainelFichario) c;
				if (fichario.getTotalAbas() == 1 && fichario.getComponentAt(0) == objeto) {
					return false;
				}
			}
			return true;
		}
	};

	public Component getRaiz() {
		if (getComponentCount() != 1) {
			throw new IllegalStateException();
		}
		Component c = getComponent(0);
		checarComponente(c);
		return c;
	}

	public Component excluirRaiz() {
		Component c = getRaiz();
		remove(c);
		return c;
	}

	public void adicionar(Component c) {
		checarComponente(c);
		add(BorderLayout.CENTER, c);
		SwingUtilities.updateComponentTreeUI(this);
	}

	private void checarComponente(Component c) {
		if (!(c instanceof PainelFichario) && !(c instanceof PainelSeparador)) {
			throw new IllegalStateException();
		}
	}
}