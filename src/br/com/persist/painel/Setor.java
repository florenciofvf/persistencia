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
	int larguraAltura = 40;
	static final float ALPHA_7 = 0.7f;
	static final float ALPHA_3 = 0.3f;
	int metade = larguraAltura / 2;
	static final char DESLOCAR = 'D';
	static final char INCLUIR = 'I';
	static final char NORTE = 'N';
	static final char LESTE = 'L';
	static final char OESTE = 'O';
	static final char SUL = 'S';
	private int metadeLargura;
	private int metadeAltura;
	Component dropTarget;
	Dimension dimension;
	boolean selecionado;
	final float alpha;
	final char local;
	boolean valido;
	Point point;
	int x;
	int y;

	Setor(char setor, float alpha) {
		this.alpha = alpha;
		this.local = setor;
	}

	void localizar(Component c) {
		dimension = c.getSize();
		metadeLargura = dimension.width / 2;
		metadeAltura = dimension.height / 2;
		valido = false;
		if (local == NORTE) {
			x = metadeLargura - metade;
			y = metade;
			valido = true;
		} else if (local == SUL) {
			x = metadeLargura - metade;
			y = dimension.height - larguraAltura;
			valido = true;
		} else if (local == LESTE) {
			x = dimension.width - larguraAltura;
			y = metadeAltura - metade;
			valido = true;
		} else if (local == OESTE) {
			x = 0;
			y = metadeAltura - metade;
			valido = true;
		}
	}

	boolean contem(int posX, int posY) {
		return (posX >= x && posX <= x + larguraAltura) && (posY >= y && posY <= y + larguraAltura);
	}

	void paint(Graphics g) {
		if (!valido) {
			return;
		}
		Graphics2D g2 = (Graphics2D) g;
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g2.fillRect(x, y, larguraAltura, larguraAltura);
		if (selecionado) {
			if (local == NORTE) {
				g2.fillRect(1, 1, dimension.width - 3, metadeAltura);
			} else if (local == SUL) {
				g2.fillRect(1, metadeAltura, dimension.width - 3, metadeAltura - 2);
			} else if (local == LESTE) {
				g2.fillRect(metadeLargura, 1, metadeLargura - 2, dimension.height - 3);
			} else if (local == OESTE) {
				g2.fillRect(1, 1, metadeLargura, dimension.height - 3);
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