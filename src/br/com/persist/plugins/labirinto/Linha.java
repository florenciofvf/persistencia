package br.com.persist.plugins.labirinto;

public class Linha {
	int x1;
	int y1;
	int x2;
	int y2;

	void copiar1(Ponto ponto) {
		x1 = ponto.x;
		y1 = ponto.y;
	}

	void copiar2(Ponto ponto) {
		x2 = ponto.x;
		y2 = ponto.y;
	}
}