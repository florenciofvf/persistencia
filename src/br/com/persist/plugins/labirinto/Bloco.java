package br.com.persist.plugins.labirinto;

import java.awt.Color;
import java.awt.Graphics2D;

public class Bloco {
	private static Vetor3D vetor1 = new Vetor3D();
	private static Vetor3D vetor2 = new Vetor3D();
	private static Vetor3D vetor3 = new Vetor3D();
	private static Vetor3D vetor4 = new Vetor3D();

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

	void desenhar3DFrente(Graphics2D g2) {
		g2.setColor(color);
		Ponto ponto1 = front.ponto1;
		Ponto ponto2 = front.ponto2;
		Ponto ponto3 = front.ponto3;
		Ponto ponto4 = front.ponto4;
		int lado = 10;
		g2.fillRect(ponto1.x, ponto1.y, lado, lado);
		g2.fillRect(ponto2.x, ponto2.y, lado, lado);
		g2.fillRect(ponto3.x, ponto3.y, lado, lado);
		g2.fillRect(ponto4.x, ponto4.y, lado, lado);
	}

	void desenhar3DEsquerdo(Graphics2D g2) {
		if (vazio) {
			return;
		}
	}

	void desenhar3DDireito(Graphics2D g2) {
		if (vazio) {
			return;
		}
	}

	void calcularFaceFrente(int comprimento) {
		if (vazio) {
			return;
		}
	}

	void calcularFaceEsquerdo(int comprimento) {
		if (vazio) {
			return;
		}
	}

	void calcularFaceDireito(int comprimento) {
		if (vazio) {
			return;
		}
	}
}