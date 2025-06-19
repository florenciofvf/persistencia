package br.com.persist.plugins.objeto.config;

import java.awt.Color;
import java.util.Objects;

public class ItemCor {
	final int linha;
	final int coluna;
	final Color color;

	public ItemCor(int linha, int coluna, Color color) {
		this.color = Objects.requireNonNull(color);
		this.coluna = coluna;
		this.linha = linha;
	}

	public int getLinha() {
		return linha;
	}

	public int getColuna() {
		return coluna;
	}

	public Color getColor() {
		return color;
	}

	@Override
	public String toString() {
		return "Linha=" + linha + " Coluna=" + coluna;
	}
}