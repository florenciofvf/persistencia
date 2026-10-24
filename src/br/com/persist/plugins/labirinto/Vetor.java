package br.com.persist.plugins.labirinto;

import static java.lang.Math.PI;

public final class Vetor {
	private static final double FVF = PI / 180;
	double x;
	double y;

	public Vetor(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vetor() {
		this(0, 0);
	}

	void alterarTamanho(int valor) {
		normalizar();
		x = x * valor;
		y = y * valor;
	}

	void reduzir() {
		double comprimento = getComprimento();
		double porcentagem = comprimento * .3;
		alterarTamanho((int) (comprimento - porcentagem));
	}

	void init(double x) {
		this.x = x;
		this.y = 0;
	}

	void init(double x, double y) {
		this.x = x;
		this.y = y;
	}

	private void normalizar() {
		double comprimento = getComprimento();
		x = x / comprimento;
		y = y / comprimento;
	}

	double getComprimento() {
		return Math.sqrt(x * x + y * y);
	}

	void rotacionar(float grau) {
		double cos = Math.cos(grau * FVF);
		double sen = Math.sin(grau * FVF);
		double novoX = x * cos - y * sen;
		double novoY = x * sen + y * cos;
		x = novoX;
		y = novoY;
	}

	public String toString() {
		return "Vetor[x=" + x + ", y=" + y + "]";
	}
}