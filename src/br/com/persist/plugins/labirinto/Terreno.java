package br.com.persist.plugins.labirinto;

import java.awt.Graphics2D;

public class Terreno {
	public static final int LINHAS = 30;
	private static final int COLUNAS = 3;
	private final Face[][] faces = new Face[LINHAS][COLUNAS];
	private Controle controle = new Controle();

	public Terreno() {
		for (int linha = 0; linha < LINHAS; linha++) {
			for (int coluna = 0; coluna < COLUNAS; coluna++) {
				faces[linha][coluna] = new Face();
			}
		}
	}

	void calcular3D(Ponto origem, Vetor vetor1, Vetor vetor2, Vetor vetor3, Vetor vetor4, int linha) {
		controle.front.ponto1.copiar(origem, vetor1);
		controle.front.ponto2.copiar(origem, vetor2);
		controle.front.ponto3.copiar(origem, vetor3);
		controle.front.ponto4.copiar(origem, vetor4);

		vetor1.reduzir();
		vetor2.reduzir();
		vetor3.reduzir();
		vetor4.reduzir();

		controle.back.ponto1.copiar(origem, vetor1);
		controle.back.ponto2.copiar(origem, vetor2);
		controle.back.ponto3.copiar(origem, vetor3);
		controle.back.ponto4.copiar(origem, vetor4);

		Face face = faces[linha][1];
		face.montarPolygon(controle.back.ponto4, controle.back.ponto3, controle.front.ponto3, controle.front.ponto4);

		controle.calcularLadoFront();
		controle.calcularLadoBack();

		controle.back.montarDerivado(controle.back.ponto4, -controle.back.lado);
		controle.front.montarDerivado(controle.front.ponto4, -controle.front.lado);
		face = faces[linha][0];
		face.montarPolygon(controle.back.derivado, controle.back.ponto4, controle.front.ponto4,
				controle.front.derivado);

		controle.back.montarDerivado(controle.back.ponto3, controle.back.lado);
		controle.front.montarDerivado(controle.front.ponto3, controle.front.lado);
		face = faces[linha][2];
		face.montarPolygon(controle.back.ponto3, controle.back.derivado, controle.front.derivado,
				controle.front.ponto3);

	}

	void desenhar3D(Graphics2D g2) {
		for (int linha = 0; linha < LINHAS; linha++) {
			faces[linha][0].desenhar3D(g2);
			faces[linha][1].desenhar3D(g2);
			faces[linha][2].desenhar3D(g2);
		}
	}

	class Controle {
		private final Face front = new Face();
		private final Face back = new Face();

		void calcularLadoFront() {
			front.lado = LabirintoUtil.altura(front.ponto1, front.ponto4);
		}

		void calcularLadoBack() {
			back.lado = LabirintoUtil.altura(back.ponto1, back.ponto4);
		}
	}
}