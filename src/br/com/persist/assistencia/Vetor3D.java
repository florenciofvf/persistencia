package br.com.persist.assistencia;

public class Vetor3D {
	private final double radiano = Math.PI / 180;
	public float x;
	public float y;
	public float z;

	public Vetor3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void rotacaoX(int grau) {
		double cos = Math.cos(grau * radiano);
		double sen = Math.sin(grau * radiano);
		double novoY = y * cos - z * sen;
		double novoZ = z * cos + y * sen;
		y = (float) novoY;
		z = (float) novoZ;
	}

	public void rotacaoY(int grau) {
		double cos = Math.cos(grau * radiano);
		double sen = Math.sin(grau * radiano);
		double novoX = x * cos - z * sen;
		double novoZ = x * sen + z * cos;
		x = (float) novoX;
		z = (float) novoZ;
	}

	public void rotacaoZ(int grau) {
		double cos = Math.cos(grau * radiano);
		double sen = Math.sin(grau * radiano);
		double novoX = x * cos - y * sen;
		double novoY = x * sen + y * cos;
		x = (float) novoX;
		y = (float) novoY;
	}
}