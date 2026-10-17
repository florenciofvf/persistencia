package br.com.persist.plugins.labirinto;

import static java.lang.Math.PI;

public final class Vetor {
	private static final double FVF = PI / 180;
	float x;
	float y;

	void alterarTamanho(int valor) {
		normalizar();
		x = x * valor;
		y = y * valor;
	}

	void init(float x) {
		this.x = x;
		this.y = 0;
	}

	private void normalizar() {
		double comprimento = getComprimento();
		x = (float) (x / comprimento);
		y = (float) (y / comprimento);
	}

	double getComprimento() {
		return Math.sqrt(x * x + y * y);
	}

	void rotacionar(float grau) {
		double cos = Math.cos(grau * FVF);
		double sen = Math.sin(grau * FVF);
		double novoX = x * cos - y * sen;
		double novoY = x * sen + y * cos;
		x = (float) novoX;
		y = (float) novoY;
	}

	public String toString() {
		return "Vetor[x=" + x + ", y=" + y + "]";
	}
}