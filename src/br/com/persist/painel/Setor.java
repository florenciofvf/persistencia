package br.com.persist.painel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.dnd.DropTargetDropEvent;

class Setor {
	private int larguraAltura = 40;
	private int metade = larguraAltura / 2;
	static final char NORTE = 'N';
	static final char LESTE = 'L';
	static final char OESTE = 'O';
	private Dimension dimension;
	static final char SUL = 'S';
	private int metadeLargura;
	private int metadeAltura;
	private final char local;
	boolean selecionado;
	boolean valido;
	private int x;
	private int y;

	Setor(char setor) {
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
			y = dimension.height - larguraAltura - metade;
			valido = true;
		} else if (local == LESTE) {
			x = dimension.width - larguraAltura - metade;
			y = metadeAltura - metade;
			valido = true;
		} else if (local == OESTE) {
			x = metade;
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
		g.drawRect(x, y, larguraAltura, larguraAltura);
		if (selecionado) {
			if (local == NORTE) {
				g.drawRect(1, 1, dimension.width - 3, metadeAltura);
			} else if (local == SUL) {
				g.drawRect(1, metadeAltura, dimension.width - 3, metadeAltura - 2);
			} else if (local == LESTE) {
				g.drawRect(metadeLargura, 1, metadeLargura - 2, dimension.height - 3);
			} else if (local == OESTE) {
				g.drawRect(1, 1, metadeLargura, dimension.height - 3);
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
}