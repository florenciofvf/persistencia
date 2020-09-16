package br.com.persist.util;

public class PosicaoDimensao {
	private final int x;
	private final int y;
	private final int largura;
	private final int altura;

	public PosicaoDimensao(int x, int y, int largura, int altura) {
		this.x = x;
		this.y = y;
		this.largura = largura;
		this.altura = altura;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getLargura() {
		return largura;
	}

	public int getAltura() {
		return altura;
	}

	@Override
	public String toString() {
		return "x=" + x + ", y=" + y + ", l=" + largura + ", a=" + altura;
	}
}