package br.com.persist.plugins.mapa.forma;

import java.awt.Graphics2D;

public class Associacao {
	Forma origem;
	Forma destino;

	public Associacao(Forma origem, Forma destino) {
		this.origem = origem;
		this.destino = destino;
	}

	public void desenhar(Graphics2D g2) {
		int[] xyOrigem = origem.getXYCentro();
		int[] xyDestino = destino.getXYCentro();
		g2.drawLine(xyOrigem[0], xyOrigem[1], xyDestino[0], xyDestino[1]);
	}
}