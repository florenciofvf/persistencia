package br.com.persist.painel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

class PainelSetor {
	private int larguraAltura = 40;
	private int metade = larguraAltura / 2;
	private Dimension dimension;
	private int metadeLargura;
	private int metadeAltura;
	private final char setor;
	boolean selecionado;
	boolean valido;
	private int x;
	private int y;

	PainelSetor(char setor) {
		this.setor = setor;
	}

	void localizar(Component c) {
		dimension = c.getSize();
		metadeLargura = dimension.width / 2;
		metadeAltura = dimension.height / 2;
		valido = false;
		if (setor == 'N') {
			x = metadeLargura - metade;
			y = metade;
			valido = true;
		} else if (setor == 'S') {
			x = metadeLargura - metade;
			y = dimension.height - larguraAltura - metade;
			valido = true;
		} else if (setor == 'L') {
			x = dimension.width - larguraAltura - metade;
			y = metadeAltura - metade;
			valido = true;
		} else if (setor == 'O') {
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
			if (setor == 'N') {
				g.drawRect(1, 1, dimension.width - 3, metadeAltura);
			} else if (setor == 'S') {
				g.drawRect(1, metadeAltura, dimension.width - 3, metadeAltura - 2);
			} else if (setor == 'L') {
				g.drawRect(metadeLargura, 1, metadeLargura - 2, dimension.height - 3);
			} else if (setor == 'O') {
				g.drawRect(1, 1, metadeLargura, dimension.height - 3);
			}
		}
	}

	public void processar(PainelTransferable objeto, PainelContainer painelContainer) {
		// TODO Auto-generated method stub
	}
}