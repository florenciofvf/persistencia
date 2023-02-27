package br.com.persist.plugins.mapa.forma;

import java.awt.Color;
import java.awt.Graphics2D;

import br.com.persist.assistencia.Vetor3D;
import br.com.persist.plugins.mapa.Objeto;

public abstract class Forma implements Comparable<Forma> {
	public static boolean desenharObjetoCentro = true;
	public static boolean desenharAtributos = true;
	public boolean centro;
	public Objeto objeto;
	public Vetor3D vetor;
	Color corGradiente1;
	Color corGradiente2;
	public int xOrigem;
	public int yOrigem;
	int qtdObjetos;
	int diametro;
	String nome;

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
		qtdObjetos = objeto.getQtdFilhos() + objeto.getQtdFilhosRef();
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

	public int compareTo(Forma o) {
		return (int) (vetor.z - o.vetor.z);
	}

	public abstract void desenhar(Graphics2D g2);

	public abstract boolean contem(int x, int y);

	public abstract int[] getXYCentro();

	public int getDiametro() {
		return diametro;
	}

	@Override
	public String toString() {
		return objeto.toString();
	}
}