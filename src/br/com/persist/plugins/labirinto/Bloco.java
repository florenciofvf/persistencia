package br.com.persist.plugins.labirinto;

import java.awt.Color;
import java.awt.Graphics2D;

public class Bloco {
	private final Ponto origemFrente = new Ponto();
	private int larguraFrente;
	private int alturaFrente;

	private static final int GRAU_ROTACAO = 35;

	private static Vetor vetor1 = new Vetor();
	private static Vetor vetor2 = new Vetor();
	private static Vetor vetor3 = new Vetor();
	private static Vetor vetor4 = new Vetor();

	final Face front = new Face();
	final Face back = new Face();

	final Color color;
	int indiceTemp;
	boolean vazio;
	int coluna;
	int linha;
	Pivo pivo;

	public Bloco() {
		color = LabirintoUtil.createColor();
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

	void calcular3DFrente(Ponto origem, int comprimento) {
		if (vazio) {
			return;
		}

		vetor1.init(-comprimento);
		vetor2.init(comprimento);
		vetor3.init(comprimento);
		vetor4.init(-comprimento);

		vetor1.rotacionar(GRAU_ROTACAO);
		vetor2.rotacionar(-GRAU_ROTACAO);
		vetor3.rotacionar(GRAU_ROTACAO);
		vetor4.rotacionar(-GRAU_ROTACAO);

		front.ponto1.copiar(origem, vetor1);
		front.ponto2.copiar(origem, vetor2);
		front.ponto3.copiar(origem, vetor3);
		front.ponto4.copiar(origem, vetor4);

		origemFrente.copiar(front.ponto1);
		larguraFrente = LabirintoUtil.largura(front.ponto1, front.ponto2);
		alturaFrente = LabirintoUtil.largura(front.ponto1, front.ponto4);
	}

	void desenhar3DFrente(Graphics2D g2) {
		g2.setColor(color);
		g2.fillRect(origemFrente.x, origemFrente.y, larguraFrente, alturaFrente);
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