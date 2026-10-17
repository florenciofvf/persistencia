package br.com.persist.plugins.labirinto;

import java.awt.Color;
import java.awt.Graphics2D;

public class Bloco {
	final Color color;
	boolean vazio;
	Face front;
	Face back;
	Pivo pivo;

	public Bloco() {
		color = LabirintoUtil.createColor();
	}

	public void desenhar2D(int x, int y, int lado, Graphics2D g2) {
		if (vazio) {
			if (pivo != null) {
				pivo.desenhar2D(x, y, lado, g2);
			}
			return;
		}
		g2.setColor(color);
		g2.fillRect(x, y, lado, lado);
	}

	public void desenhar3D(Graphics2D g2) {
	}
}