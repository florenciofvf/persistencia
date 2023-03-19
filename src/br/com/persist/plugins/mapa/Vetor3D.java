package br.com.persist.plugins.mapa;

public class Vetor3D {
	private static final double RADIANO = Math.PI / 180;
	double x;
	double y;
	double z;

	public Vetor3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void rotacaoX(int grau) {
		double cos = Math.cos(grau * RADIANO);
		double sen = Math.sin(grau * RADIANO);
		double novoY = y * cos - z * sen;
		double novoZ = z * cos + y * sen;
		y = novoY;
		z = novoZ;
	}

	public void rotacaoY(int grau) {
		double cos = Math.cos(grau * RADIANO);
		double sen = Math.sin(grau * RADIANO);
		double novoX = x * cos - z * sen;
		double novoZ = x * sen + z * cos;
		x = novoX;
		z = novoZ;
	}

	public void rotacaoZ(int grau) {
		double cos = Math.cos(grau * RADIANO);
		double sen = Math.sin(grau * RADIANO);
		double novoX = x * cos - y * sen;
		double novoY = x * sen + y * cos;
		x = novoX;
		y = novoY;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}
}