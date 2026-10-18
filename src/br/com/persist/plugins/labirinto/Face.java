package br.com.persist.plugins.labirinto;

import java.awt.Color;
import java.awt.Graphics2D;

public class Face {
	public static final Color COR_PISO = Color.WHITE;

	private final int[] arrayX = new int[4];
	private final int[] arrayY = new int[4];

	final Ponto ponto1 = new Ponto();
	final Ponto ponto2 = new Ponto();
	final Ponto ponto3 = new Ponto();
	final Ponto ponto4 = new Ponto();

	void desenhar3D(Graphics2D g2) {
		g2.setColor(COR_PISO);
		g2.fillPolygon(arrayX, arrayY, 4);
		g2.setColor(Bloco.COR_BORDA);
		g2.drawPolygon(arrayX, arrayY, 4);
	}
}