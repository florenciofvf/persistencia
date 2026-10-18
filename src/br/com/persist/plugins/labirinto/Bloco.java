package br.com.persist.plugins.labirinto;

import java.awt.Color;
import java.awt.Graphics2D;

public class Bloco {
	private final int[] arrayXFrente = new int[4];
	private final int[] arrayYFrente = new int[4];

	private final int[] arrayXBloco = new int[6];
	private final int[] arrayYBloco = new int[6];

	private static Vetor vetor1 = new Vetor();
	private static Vetor vetor2 = new Vetor();
	private static Vetor vetor3 = new Vetor();
	private static Vetor vetor4 = new Vetor();

	final Face front = new Face();
	final Face back = new Face();

	final boolean vazio;
	final Color color;
	int indiceTemp;
	int coluna;
	int linha;
	Pivo pivo;

	public Bloco(boolean vazio) {
		this.color = LabirintoUtil.createColor();
		this.vazio = vazio;
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

	private void initVetores(int comprimento) {
		vetor1.init(-comprimento);
		vetor2.init(comprimento);
		vetor3.init(comprimento);
		vetor4.init(-comprimento);

		int grau = 35;
		vetor1.rotacionar(grau);
		vetor2.rotacionar(-grau);
		vetor3.rotacionar(grau);
		vetor4.rotacionar(-grau);
	}

	void calcular3DFrente(Ponto origem, int comprimento) {
		if (vazio) {
			return;
		}

		initVetores(comprimento);

		front.ponto1.copiar(origem, vetor1);
		front.ponto2.copiar(origem, vetor2);
		front.ponto3.copiar(origem, vetor3);
		front.ponto4.copiar(origem, vetor4);

		// 1 2
		// 4 3

		copiar(arrayXFrente, arrayYFrente, front.ponto1, 0);
		copiar(arrayXFrente, arrayYFrente, front.ponto2, 1);
		copiar(arrayXFrente, arrayYFrente, front.ponto3, 2);
		copiar(arrayXFrente, arrayYFrente, front.ponto4, 3);
	}

	private void copiar(int[] arrayX, int[] arrayY, Ponto ponto, int indice) {
		arrayX[indice] = ponto.x;
		arrayY[indice] = ponto.y;
	}

	void desenhar3DFrente(Graphics2D g2) {
		g2.setColor(color);
		g2.fillPolygon(arrayXFrente, arrayYFrente, 4);
	}

	void calcular3DEsquerdo(Ponto origem, int comprimento) {
		if (vazio) {
			return;
		}

		int fatia = comprimento / 10;
		comprimento -= fatia * indiceTemp;

		initVetores(comprimento);

		front.ponto1.copiar(origem, vetor1);
		// front.ponto2.copiar(origem, vetor2);
		// front.ponto3.copiar(origem, vetor3);
		front.ponto4.copiar(origem, vetor4);
		int altura = LabirintoUtil.altura(front.ponto1, front.ponto4);
		front.ponto2.copiar(origem, vetor1, -altura, 0);
		front.ponto3.copiar(origem, vetor4, -altura, 0);

		// 2 1 | 1
		// 3 4 | 4

		vetor1.reduzir();
		vetor4.reduzir();
		back.ponto1.copiar(origem, vetor1);
		back.ponto4.copiar(origem, vetor4);

		copiar(arrayXBloco, arrayYBloco, front.ponto2, 0);
		copiar(arrayXBloco, arrayYBloco, front.ponto1, 1);
		copiar(arrayXBloco, arrayYBloco, back.ponto1, 2);
		copiar(arrayXBloco, arrayYBloco, back.ponto4, 3);
		copiar(arrayXBloco, arrayYBloco, front.ponto4, 4);
		copiar(arrayXBloco, arrayYBloco, front.ponto3, 5);
	}

	void desenhar3DEsquerdo(Graphics2D g2) {
		if (vazio) {
			return;
		}
		g2.setColor(color);
		g2.fillPolygon(arrayXBloco, arrayYBloco, 6);
	}

	void calcular3DDireito(Ponto origem, int comprimento) {
		if (vazio) {
			return;
		}
	}

	void desenhar3DDireito(Graphics2D g2) {
		if (vazio) {
			return;
		}
	}
}