package br.com.persist.plugins.labirinto;

import java.awt.Color;
import java.awt.Graphics2D;

public class Bloco {
	public static final Color COR_BORDA = Color.BLACK;
	private final Controle controle = new Controle();
	private final int[] arrayXFrente = new int[4];
	private final int[] arrayYFrente = new int[4];
	private final int[] arrayXBloco = new int[6];
	private final int[] arrayYBloco = new int[6];
	private final Linha quina = new Linha();
	private final Color color;
	protected boolean vazio;
	int coluna;
	int linha;
	Pivo pivo;

	public Bloco(boolean vazio) {
		this.color = LabirintoUtil.createColor();
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

		copiar(arrayXFrente, arrayYFrente, controle.front.ponto1, 0);
		copiar(arrayXFrente, arrayYFrente, controle.front.ponto2, 1);
		copiar(arrayXFrente, arrayYFrente, controle.front.ponto3, 2);
		copiar(arrayXFrente, arrayYFrente, controle.front.ponto4, 3);
	}

	private void copiar(int[] arrayX, int[] arrayY, Ponto ponto, int indice) {
		arrayX[indice] = ponto.x;
		arrayY[indice] = ponto.y;
	}

	void desenhar3DFrente(Graphics2D g2) {
		g2.setColor(color);
		g2.fillPolygon(arrayXFrente, arrayYFrente, 4);
		g2.setColor(COR_BORDA);
		g2.drawPolygon(arrayXFrente, arrayYFrente, 4);
	}

	void calcular3DEsquerdo(Ponto origem, Vetor vetor1, Vetor vetor4) {
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

		// 1 1 1
		// 4 4 4

		controle.front.montarDerivado(controle.front.ponto1, -controle.front.lado);
		copiar(arrayXBloco, arrayYBloco, controle.front.derivado, 0);

		copiar(arrayXBloco, arrayYBloco, controle.front.ponto1, 1);
		copiar(arrayXBloco, arrayYBloco, controle.back.ponto1, 2);
		copiar(arrayXBloco, arrayYBloco, controle.back.ponto4, 3);
		copiar(arrayXBloco, arrayYBloco, controle.front.ponto4, 4);

		controle.front.montarDerivado(controle.front.ponto4, -controle.front.lado);
		copiar(arrayXBloco, arrayYBloco, controle.front.derivado, 5);

		quina.copiar1(controle.front.ponto1);
		quina.copiar2(controle.front.ponto4);
	}

	void calcular3DDireito(Ponto origem, Vetor vetor2, Vetor vetor3) {
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

		// 2 2 2
		// 3 3 3

		copiar(arrayXBloco, arrayYBloco, controle.back.ponto2, 0);
		copiar(arrayXBloco, arrayYBloco, controle.front.ponto2, 1);

		controle.front.montarDerivado(controle.front.ponto2, controle.front.lado);
		copiar(arrayXBloco, arrayYBloco, controle.front.derivado, 2);

		controle.front.montarDerivado(controle.front.ponto3, controle.front.lado);
		copiar(arrayXBloco, arrayYBloco, controle.front.derivado, 3);

		copiar(arrayXBloco, arrayYBloco, controle.front.ponto3, 4);
		copiar(arrayXBloco, arrayYBloco, controle.back.ponto3, 5);

		quina.copiar1(controle.front.ponto2);
		quina.copiar2(controle.front.ponto3);
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
		g2.fillPolygon(arrayXBloco, arrayYBloco, 6);
		g2.setColor(COR_BORDA);
		g2.drawPolygon(arrayXBloco, arrayYBloco, 6);
		g2.drawLine(quina.x1, quina.y1, quina.x2, quina.y2);
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