package br.com.persist.plugins.mapa;

import java.awt.Color;
import java.awt.Graphics2D;

public abstract class Forma implements Comparable<Forma> {
	public static final boolean DESENHAR_OBJETO_CENTRO = true;
	public static final boolean DESENHAR_ATRIBUTOS = true;
	Color corGradiente1;
	Color corGradiente2;
	boolean centro;
	int qtdObjetos;
	Objeto objeto;
	Vetor3D vetor;
	int diametro;
	String nome;
	int xOrigem;
	int yOrigem;

	public Forma(int x, int y, int z, int diametro, Objeto objeto) {
		if (objeto == null) {
			throw new IllegalArgumentException("Objeto nulo.");
		}
		vetor = new Vetor3D(x, y, z);
		corGradiente1 = Color.WHITE;
		corGradiente2 = Color.BLACK;
		this.diametro = diametro;
		nome = objeto.getNome();
		this.objeto = objeto;
		if (this.objeto.getCorRGB() != null) {
			corGradiente2 = this.objeto.getCorRGB();
		}
		qtdObjetos = objeto.getQtdFilhos() + objeto.getQtdReferencias();
	}

	public void setCorGradienteRef(boolean ref) {
		if (objeto.getCorRGB() != null) {
			return;
		}
		corGradiente2 = ref ? Color.RED : Color.BLACK;
	}

	public void setCorGradiente1(Color corGradiente1) {
		this.corGradiente1 = corGradiente1;
	}

	public void setCorGradiente2(Color corGradiente2) {
		this.corGradiente2 = corGradiente2;
	}

	@Override
	public int compareTo(Forma o) {
		Vetor3D oVetor = o.vetor;
		return (int) (vetor.z - oVetor.z);
	}

	public abstract void desenhar(Graphics2D g2);

	public abstract boolean contem(int x, int y);

	public abstract int[] getXYCentro();

	public int getDiametro() {
		return diametro;
	}

	public Vetor3D getVetor() {
		return vetor;
	}

	public Objeto getObjeto() {
		return objeto;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((objeto == null) ? 0 : objeto.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Forma other = (Forma) obj;
		if (objeto == null) {
			if (other.objeto != null) {
				return false;
			}
		} else if (!objeto.equals(other.objeto)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return objeto.toString();
	}
}