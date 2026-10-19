package br.com.persist.plugins.labirinto;

import java.awt.Color;
import java.awt.Graphics2D;

public class Bloco {
	public static final Color COR_BORDA = Color.BLACK;
	private final Controle controle = new Controle();
	private final int[] arrayXFront = new int[4];
	private final int[] arrayYFront = new int[4];
	private final int[] arrayXLado = new int[4];
	private final int[] arrayYLado = new int[4];
	private final Color color;
	private Color colorLado;
	protected boolean vazio;
	int offset;
	int coluna;
	int linha;
	Pivo pivo;

	public Bloco(boolean vazio) {
		this.color = LabirintoUtil.createColor();
		this.colorLado = color;
		this.vazio = vazio;
	}

	public void click() {
		vazio = !vazio;
	}

	public boolean contem(int lado, int x, int y) {
		int posX = lado * coluna;
		int posY = lado * linha;
		return (x > posX && x < posX + lado) && (y > posY && y < posY + lado);
	}

	public void desenhar2D(int lado, Graphics2D g2) {
		int x = lado * coluna;
		int y = lado * linha;
		if (vazio) {
			if (pivo != null) {
				pivo.desenhar2D(x, y, lado, g2);
			}
			return;
		}
		g2.setColor(color);
		g2.fillRect(x, y, lado, lado);
	}

	void calcular3DFrente(Ponto origem, Vetor vetor1, Vetor vetor2, Vetor vetor3, Vetor vetor4) {
		if (vazio) {
			vetor1.reduzir();
			vetor2.reduzir();
			vetor3.reduzir();
			vetor4.reduzir();
			return;
		}

		controle.front.ponto1.copiar(origem, vetor1);
		controle.front.ponto2.copiar(origem, vetor2);
		controle.front.ponto3.copiar(origem, vetor3);
		controle.front.ponto4.copiar(origem, vetor4);

		// 1 2
		// 4 3

		copiar(arrayXFront, arrayYFront, controle.front.ponto1, 0);
		copiar(arrayXFront, arrayYFront, controle.front.ponto2, 1);
		copiar(arrayXFront, arrayYFront, controle.front.ponto3, 2);
		copiar(arrayXFront, arrayYFront, controle.front.ponto4, 3);
	}

	private void copiar(int[] arrayX, int[] arrayY, Ponto ponto, int indice) {
		arrayX[indice] = ponto.x;
		arrayY[indice] = ponto.y;
	}

	void desenhar3DFrente(Graphics2D g2) {
		g2.setColor(color);
		g2.fillPolygon(arrayXFront, arrayYFront, 4);
		g2.setColor(COR_BORDA);
		g2.drawPolygon(arrayXFront, arrayYFront, 4);
	}

	void calcular3DEsquerdo(Ponto origem, Vetor vetor1, Vetor vetor4, Bloco anterior) {
		if (vazio) {
			vetor1.reduzir();
			vetor4.reduzir();
			return;
		}

		controle.front.ponto1.copiar(origem, vetor1);
		controle.front.ponto4.copiar(origem, vetor4);

		vetor1.reduzir();
		vetor4.reduzir();

		controle.back.ponto1.copiar(origem, vetor1);
		controle.back.ponto4.copiar(origem, vetor4);

		controle.calcularLadoFrontE();

		// 1 1
		// 4 4
		// 1 1
		// 4 4

		controle.front.montarDerivado(controle.front.ponto1, -controle.front.lado);
		copiar(arrayXFront, arrayYFront, controle.front.derivado, 0);
		copiar(arrayXFront, arrayYFront, controle.front.ponto1, 1);
		copiar(arrayXFront, arrayYFront, controle.front.ponto4, 2);
		controle.front.montarDerivado(controle.front.ponto4, -controle.front.lado);
		copiar(arrayXFront, arrayYFront, controle.front.derivado, 3);

		copiar(arrayXLado, arrayYLado, controle.front.ponto1, 0);
		copiar(arrayXLado, arrayYLado, controle.back.ponto1, 1);
		copiar(arrayXLado, arrayYLado, controle.back.ponto4, 2);
		copiar(arrayXLado, arrayYLado, controle.front.ponto4, 3);

		if (anterior != null) {
			colorLado = anterior.color;
		}
	}

	void calcular3DDireito(Ponto origem, Vetor vetor2, Vetor vetor3, Bloco anterior) {
		if (vazio) {
			vetor2.reduzir();
			vetor3.reduzir();
			return;
		}

		controle.front.ponto2.copiar(origem, vetor2);
		controle.front.ponto3.copiar(origem, vetor3);

		vetor2.reduzir();
		vetor3.reduzir();

		controle.back.ponto2.copiar(origem, vetor2);
		controle.back.ponto3.copiar(origem, vetor3);

		controle.calcularLadoFrontD();

		// 2 2
		// 3 3
		// 2 2
		// 3 3

		copiar(arrayXFront, arrayYFront, controle.front.ponto2, 0);
		controle.front.montarDerivado(controle.front.ponto2, controle.front.lado);
		copiar(arrayXFront, arrayYFront, controle.front.derivado, 1);
		controle.front.montarDerivado(controle.front.ponto3, controle.front.lado);
		copiar(arrayXFront, arrayYFront, controle.front.derivado, 2);
		copiar(arrayXFront, arrayYFront, controle.front.ponto3, 3);

		copiar(arrayXLado, arrayYLado, controle.back.ponto2, 0);
		copiar(arrayXLado, arrayYLado, controle.front.ponto2, 1);
		copiar(arrayXLado, arrayYLado, controle.front.ponto3, 2);
		copiar(arrayXLado, arrayYLado, controle.back.ponto3, 3);

		if (anterior != null) {
			colorLado = anterior.color;
		}
	}

	void desenhar3DEsquerdo(Graphics2D g2) {
		desenhar3D(g2);
	}

	void desenhar3DDireito(Graphics2D g2) {
		desenhar3D(g2);
	}

	private void desenhar3D(Graphics2D g2) {
		if (vazio) {
			return;
		}
		g2.setColor(color);
		g2.fillPolygon(arrayXFront, arrayYFront, 4);
		g2.setColor(colorLado);
		g2.fillPolygon(arrayXLado, arrayYLado, 4);
		g2.setColor(COR_BORDA);
		g2.drawPolygon(arrayXFront, arrayYFront, 4);
		g2.drawPolygon(arrayXLado, arrayYLado, 4);
	}

	class Controle {
		private final Face front = new Face();
		private final Face back = new Face();

		void calcularLadoFrontE() {
			front.lado = LabirintoUtil.altura(front.ponto1, front.ponto4);
		}

		void calcularLadoFrontD() {
			front.lado = LabirintoUtil.altura(front.ponto2, front.ponto3);
		}
	}
}