package br.com.persist.plugins.labirinto;

import java.awt.Graphics2D;

public class Terreno {
	private static final int LINHAS = 30;
	private static final int COLUNAS = 3;
	private final Face[][] faces = new Face[LINHAS][COLUNAS];

	void calcular3D(Ponto origem, Vetor vetor1, Vetor vetor2, Vetor vetor3, Vetor vetor4) {
		//
	}

	void desenhar3D(Graphics2D g2) {
		for (int linha = 0; linha < LINHAS; linha++) {
			faces[linha][0].desenhar3D(g2);
			faces[linha][1].desenhar3D(g2);
			faces[linha][2].desenhar3D(g2);
		}
	}
}