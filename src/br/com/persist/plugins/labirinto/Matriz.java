package br.com.persist.plugins.labirinto;

import java.awt.Graphics2D;

public class Matriz {
	private final Bloco[][] blocos;
	private final int colunas;
	private final int linhas;
	private int indiceColuna;
	private int indiceLinha;
	int lado = 40;

	public Matriz(int linhas, int colunas) {
		this.linhas = linhas;
		this.colunas = colunas;
		blocos = new Bloco[linhas][colunas];
	}

	public void set(int linha, int coluna, Bloco bloco) {
		blocos[linha][coluna] = bloco;
	}

	public void add(Bloco bloco) {
		blocos[indiceLinha][indiceColuna] = bloco;
		indiceColuna++;
		if (indiceColuna == colunas) {
			indiceColuna = 0;
			indiceLinha++;
		}
	}

	public void desenhar2D(Graphics2D g2) {
		for (int linha = 0; linha < linhas; linha++) {
			for (int coluna = 0; coluna < colunas; coluna++) {
				Bloco bloco = blocos[linha][coluna];
				int x = lado * coluna;
				int y = lado * linha;
				bloco.desenhar2D(x, y, lado, g2);
			}
		}
	}
}