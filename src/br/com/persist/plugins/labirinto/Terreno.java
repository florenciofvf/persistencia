package br.com.persist.plugins.labirinto;

import java.awt.Graphics2D;

public class Terreno {
	public static final int LINHAS = LabirintoConstantes.FILA_BLOCOS_MAX;
	private static final int COLUNAS = 3;
	private final Face[][] faces = new Face[LINHAS][COLUNAS];
	private final Controle controle = new Controle();

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

		controle.calcularLadoFrontE();
		controle.calcularLadoBackE();

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

	void calcular3DGiroE(Ponto origem, Vetor vetor2, Vetor vetor3, int linha) {
		controle.front.ponto2.copiar(origem, vetor2);
		controle.front.ponto3.copiar(origem, vetor3);

		vetor2.reduzir();
		vetor3.reduzir();

		controle.back.ponto2.copiar(origem, vetor2);
		controle.back.ponto3.copiar(origem, vetor3);

		controle.calcularLadoFrontD();
		controle.calcularLadoBackD();

		controle.back.montarDerivado(controle.back.ponto3, controle.back.lado);
		controle.front.montarDerivado(controle.front.ponto3, controle.front.lado);
		Face face = faces[linha][2];
		face.montarPolygon(controle.back.ponto3, controle.back.derivado, controle.front.derivado,
				controle.front.ponto3);

		face = faces[linha][1];
		controle.back.derivado.x = 0;
		controle.back.derivado.y = controle.back.ponto3.y;
		controle.front.derivado.x = 0;
		controle.front.derivado.y = controle.front.ponto3.y;
		face.montarPolygon(controle.back.derivado, controle.back.ponto3, controle.front.ponto3,
				controle.front.derivado);
	}

	void calcular3DGiroD(Ponto origem, Vetor vetor1, Vetor vetor4, int linha, int largura) {
		controle.front.ponto1.copiar(origem, vetor1);
		controle.front.ponto4.copiar(origem, vetor4);

		vetor1.reduzir();
		vetor4.reduzir();

		controle.back.ponto1.copiar(origem, vetor1);
		controle.back.ponto4.copiar(origem, vetor4);

		controle.calcularLadoFrontE();
		controle.calcularLadoBackE();

		controle.back.montarDerivado(controle.back.ponto4, -controle.back.lado);
		controle.front.montarDerivado(controle.front.ponto4, -controle.front.lado);
		Face face = faces[linha][0];
		face.montarPolygon(controle.back.derivado, controle.back.ponto4, controle.front.ponto4,
				controle.front.derivado);

		face = faces[linha][1];
		controle.back.derivado.x = largura;
		controle.back.derivado.y = controle.back.ponto4.y;
		controle.front.derivado.x = largura;
		controle.front.derivado.y = controle.front.ponto4.y;
		face.montarPolygon(controle.back.ponto4, controle.back.derivado, controle.front.derivado,
				controle.front.ponto4);
	}

	void desenhar3D(Graphics2D g2) {
		for (int linha = 0; linha < LINHAS; linha++) {
			faces[linha][0].desenhar3D(g2);
			faces[linha][1].desenhar3D(g2);
			faces[linha][2].desenhar3D(g2);
		}
	}

	void desenhar3DGiroE(Graphics2D g2) {
		for (int linha = 0; linha < LINHAS; linha++) {
			faces[linha][1].desenhar3D(g2);
			faces[linha][2].desenhar3D(g2);
		}
	}

	void desenhar3DGiroD(Graphics2D g2) {
		for (int linha = 0; linha < LINHAS; linha++) {
			faces[linha][0].desenhar3D(g2);
			faces[linha][1].desenhar3D(g2);
		}
	}
}