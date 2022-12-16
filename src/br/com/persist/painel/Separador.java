package br.com.persist.painel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import br.com.persist.assistencia.Util;

public class Separador extends JSplitPane implements FicharioListener {
	private transient Setor nor = new Setor(Setor.NORTE);
	private transient Setor les = new Setor(Setor.LESTE);
	private transient Setor oes = new Setor(Setor.OESTE);
	private transient Setor sul = new Setor(Setor.SUL);
	private static final long serialVersionUID = 1L;

	public Separador(int orientation, Component left, Component right) {
		super(orientation, get(left), get(right));
		new DropTarget(this, dropTargetListener);
		SwingUtilities.invokeLater(() -> setDividerLocation(0.5));
	}

	private static Component get(Component c) {
		if (c instanceof Transferivel) {
			Transferivel aba = (Transferivel) c;
			Fichario fichario = new Fichario();
			fichario.addTab(aba.getTitle(), aba);
			c = fichario;
		}
		return c;
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
						e.acceptDrop(Transferivel.ACAO_VALIDA);
						e.dropComplete(true);
						SwingUtilities.invokeLater(() -> setor.processar(objeto, Separador.this));
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
	};

	@Override
	public void ficharioVazio(Fichario fichario) {
		if (leftComponent == fichario) {
			setLeftComponent(null);
			substituirPor(rightComponent);
		} else if (rightComponent == fichario) {
			setRightComponent(null);
			substituirPor(leftComponent);
		} else {
			throw new IllegalStateException();
		}
	}

	private void substituirPor(Component novo) {
		Container parent = getParent();
		if (parent instanceof Separador) {
			Separador separador = (Separador) parent;
			if (separador.getLeftComponent() == this) {
				separador.setLeftComponent(novo);
			} else {
				separador.setRightComponent(novo);
			}
		} else {
			parent.remove(this);
			parent.add(novo);
		}
	}

	@Override
	public void setLeftComponent(Component comp) {
		super.setLeftComponent(comp);
		setFicharioListener(comp);
	}

	@Override
	public void setRightComponent(Component comp) {
		super.setRightComponent(comp);
		setFicharioListener(comp);
	}

	private void setFicharioListener(Component comp) {
		if (comp instanceof Fichario) {
			((Fichario) comp).setFicharioListener(this);
		}
	}
}