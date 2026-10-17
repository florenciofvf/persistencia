package br.com.persist.plugins.labirinto;

public class Ponto {
	int x;
	int y;

	void copiar(Ponto origem, Vetor vetor) {
		x = (int) (origem.x + vetor.x);
		y = (int) (origem.y + vetor.y);
	}
}