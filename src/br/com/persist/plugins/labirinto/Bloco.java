package br.com.persist.plugins.labirinto;

import java.awt.Color;
import java.awt.Graphics2D;

public class Bloco {
	private final int[] arrayXFrente = new int[4];
	private final int[] arrayYFrente = new int[4];

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

	void calcular3DEsquerdo(int comprimento) {
		if (vazio) {
			return;
		}
	}

	void desenhar3DEsquerdo(Graphics2D g2) {
		if (vazio) {
			return;
		}
	}

	void calcular3DDireito(int comprimento) {
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