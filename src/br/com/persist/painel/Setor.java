package br.com.persist.painel;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.dnd.DropTargetDropEvent;

import javax.swing.SwingUtilities;

class Setor {
	int lado = 20;
	int metadeLado = lado / 2;
	static final float ALPHA_7 = 0.7f;
	static final float ALPHA_3 = 0.3f;
	static final char DESLOCAR = 'D';
	static final char INCLUIR = 'I';
	static final char NORTE = 'N';
	static final char LESTE = 'L';
	static final char OESTE = 'O';
	private int metadeLarguraComp;
	private int metadeAlturaComp;
	static final char SUL = 'S';
	Dimension dimensionComp;
	Component dropTarget;
	boolean selecionado;
	final float alpha;
	final char local;
	Point pointDrop;
	boolean valido;
	int x;
	int y;

	Setor(char setor, float alpha) {
		this.alpha = alpha;
		this.local = setor;
	}

	void localizar(Component c) {
		dimensionComp = c.getSize();
		metadeLarguraComp = dimensionComp.width / 2;
		metadeAlturaComp = dimensionComp.height / 2;
		valido = false;
		if (local == NORTE) {
			x = metadeLarguraComp - metadeLado;
			y = metadeLado;
			valido = true;
		} else if (local == SUL) {
			x = metadeLarguraComp - metadeLado;
			y = dimensionComp.height - lado;
			valido = true;
		} else if (local == LESTE) {
			x = dimensionComp.width - lado;
			y = metadeAlturaComp - metadeLado;
			valido = true;
		} else if (local == OESTE) {
			x = 0;
			y = metadeAlturaComp - metadeLado;
			valido = true;
		}
	}

	boolean contem(int posX, int posY) {
		return (posX >= x && posX <= x + lado) && (posY >= y && posY <= y + lado);
	}

	void paint(Graphics g) {
		if (!valido) {
			return;
		}
		Graphics2D g2 = (Graphics2D) g;
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g2.drawRect(x, y, lado, lado);
		if (selecionado) {
			g2.fillRect(x, y, lado, lado);
			if (local == NORTE) {
				g2.fillRect(1, 1, dimensionComp.width - 3, metadeAlturaComp);
			} else if (local == SUL) {
				g2.fillRect(1, metadeAlturaComp, dimensionComp.width - 3, metadeAlturaComp - 2);
			} else if (local == LESTE) {
				g2.fillRect(metadeLarguraComp, 1, metadeLarguraComp - 2, dimensionComp.height - 3);
			} else if (local == OESTE) {
				g2.fillRect(1, 1, metadeLarguraComp, dimensionComp.height - 3);
			}
		}
	}

	static Setor get(DropTargetDropEvent e, Setor... setores) {
		Point p = e.getLocation();
		for (Setor setor : setores) {
			if (setor.contem(p.x, p.y)) {
				return setor;
			}
		}
		return null;
	}

	void processar(Transferivel objeto) throws SeparadorException {
		if (dropTarget.getParent() == null) {
			dropTarget = ((Separador) dropTarget).substituto;
		}
		Container parent = dropTarget.getParent();
		preParent(parent, dropTarget);
		if (local == NORTE) {
			posParent(parent, Separador.vertical(objeto, dropTarget));
		} else if (local == SUL) {
			posParent(parent, Separador.vertical(dropTarget, objeto));
		} else if (local == LESTE) {
			posParent(parent, Separador.horizontal(dropTarget, objeto));
		} else if (local == OESTE) {
			posParent(parent, Separador.horizontal(objeto, dropTarget));
		}
		dropTarget = null;
		SwingUtilities.updateComponentTreeUI(parent);
	}

	void preParent(Container parent, Component dropTarget) throws SeparadorException {
		if (parent instanceof Separador) {
			Separador separador = (Separador) parent;
			if (separador.getLeftComponent() != dropTarget && separador.getRightComponent() != dropTarget) {
				throw new SeparadorException();
			}
			if (separador.getLeftComponent() == dropTarget) {
				separador.setLeftComponent(null);
			} else {
				separador.setRightComponent(null);
			}
		} else {
			parent.remove(dropTarget);
		}
	}

	void posParent(Container parent, Separador novoSeparador) throws SeparadorException {
		if (parent instanceof Separador) {
			Separador separador = (Separador) parent;
			if (separador.getLeftComponent() != null && separador.getRightComponent() != null) {
				throw new SeparadorException();
			}
			if (separador.getLeftComponent() == null) {
				separador.setLeftComponent(novoSeparador);
			} else {
				separador.setRightComponent(novoSeparador);
			}
		} else {
			parent.add(novoSeparador);
		}
	}
}