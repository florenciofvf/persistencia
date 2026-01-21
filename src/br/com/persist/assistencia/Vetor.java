package br.com.persist.assistencia;

import static java.lang.Math.PI;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Vetor {
	private static final double FVF = PI / 180;
	private float x;
	private float y;

	public Vetor() {
		this(1, 1);
	}

	public Vetor(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public double diferencaEmRadianos(Vetor outro) {
		double prodEscalar = produtoEscalar(outro);
		double prodModulos = getComprimento() * outro.getComprimento();
		double auxCalculo = prodEscalar / prodModulos;
		return Math.acos(auxCalculo);
	}

	public double diferencaEmGraus(Vetor outro) {
		return converterParaGrau(diferencaEmRadianos(outro));
	}

	public double produtoEscalar(Vetor outro) {
		return x * outro.x + y * outro.y;
	}

	public void alterarTamanho(int valor) {
		normalizar();
		x = x * valor;
		y = y * valor;
	}

	public void rotacionar(float grau) {
		double cos = Math.cos(grau * FVF);
		double sen = Math.sin(grau * FVF);
		double novoX = x * cos - y * sen;
		double novoY = x * sen + y * cos;
		x = (float) novoX;
		y = (float) novoY;
	}

	public double getComprimento() {
		return Math.sqrt(x * x + y * y);
	}

	public void normalizar() {
		double comprimento = getComprimento();
		x = (float) (x / comprimento);
		y = (float) (y / comprimento);
	}

	public Vetor clonar() {
		return new Vetor(x, y);
	}

	public String toString() {
		return "Vetor[x=" + x + ", y=" + y + "]";
	}

	public static double converterParaGrau(double radiano) {
		return radiano * 180 / PI;
	}

	public static double converterParaRadiano(int grau) {
		return grau * FVF;
	}

	public static void main() {
		Vetor a = new Vetor(100, 0);
		Vetor b = new Vetor(100, 0);
		a.rotacionar(90);
		Logger.getLogger(Vetor.class.getName()).log(Level.INFO, "RADIANOS: {0}", b.diferencaEmRadianos(a));
		Logger.getLogger(Vetor.class.getName()).log(Level.INFO, "GRAUS: {0}", b.diferencaEmGraus(a));
	}
}