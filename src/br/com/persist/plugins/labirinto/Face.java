package br.com.persist.plugins.labirinto;

import java.awt.Color;
import java.awt.Graphics2D;

public class Face {
	public static final Color COR_PISO = Color.WHITE;

	private final int[] arrayX = new int[4];
	private final int[] arrayY = new int[4];

	final Ponto derivado = new Ponto();

	final Ponto ponto1 = new Ponto();
	final Ponto ponto2 = new Ponto();
	final Ponto ponto3 = new Ponto();
	final Ponto ponto4 = new Ponto();

	int lado;

	void desenhar3D(Graphics2D g2) {
		g2.setColor(COR_PISO);
		g2.fillPolygon(arrayX, arrayY, 4);
		g2.setColor(Bloco.COR_BORDA);
		g2.drawPolygon(arrayX, arrayY, 4);
	}

	void montarPolygon(Ponto ponto1, Ponto ponto2, Ponto ponto3, Ponto ponto4) {
		arrayX[0] = ponto1.x;
		arrayY[0] = ponto1.y;

		arrayX[1] = ponto2.x;
		arrayY[1] = ponto2.y;

		arrayX[2] = ponto3.x;
		arrayY[2] = ponto3.y;

		arrayX[3] = ponto4.x;
		arrayY[3] = ponto4.y;
	}

	void montarDerivado(Ponto ponto, int deltaX) {
		derivado.copiar(ponto, deltaX);
	}
}