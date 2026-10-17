package br.com.persist.plugins.labirinto;

import java.awt.Color;
import java.awt.Graphics2D;

public class Pivo {
	Sentido sentido;
	Bloco bloco;

	public void desenhar2D(int x, int y, int lado, Graphics2D g2) {
		g2.setColor(Color.RED);
		lado /= 2;
		int m = lado / 2;
		g2.fillOval(x + m, y + m, lado, lado);
	}
}