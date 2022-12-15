package br.com.persist.painel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

class PainelSetor {
	private int larguraAltura = 40;
	private int metade = larguraAltura / 2;
	static final char NORTE = 'N';
	static final char LESTE = 'L';
	static final char OESTE = 'O';
	private Dimension dimension;
	static final char SUL = 'S';
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
		if (setor == NORTE) {
			x = metadeLargura - metade;
			y = metade;
			valido = true;
		} else if (setor == SUL) {
			x = metadeLargura - metade;
			y = dimension.height - larguraAltura - metade;
			valido = true;
		} else if (setor == LESTE) {
			x = dimension.width - larguraAltura - metade;
			y = metadeAltura - metade;
			valido = true;
		} else if (setor == OESTE) {
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
			if (setor == NORTE) {
				g.drawRect(1, 1, dimension.width - 3, metadeAltura);
			} else if (setor == SUL) {
				g.drawRect(1, metadeAltura, dimension.width - 3, metadeAltura - 2);
			} else if (setor == LESTE) {
				g.drawRect(metadeLargura, 1, metadeLargura - 2, dimension.height - 3);
			} else if (setor == OESTE) {
				g.drawRect(1, 1, metadeLargura, dimension.height - 3);
			}
		}
	}

	void processar(PainelTransferable objeto, PainelContainer painelContainer) {
		Component raiz = painelContainer.excluirRaiz();
		PainelSeparador separador = null;
		if (setor == NORTE) {
			separador = PainelSeparador.vertical(objeto, raiz);
			painelContainer.adicionar(separador);
		} else if (setor == SUL) {
			separador = PainelSeparador.vertical(raiz, objeto);
			painelContainer.adicionar(separador);
		} else if (setor == LESTE) {
			separador = PainelSeparador.horizontal(raiz, objeto);
			painelContainer.adicionar(separador);
		} else if (setor == OESTE) {
			separador = PainelSeparador.horizontal(objeto, raiz);
			painelContainer.adicionar(separador);
		}
	}
}