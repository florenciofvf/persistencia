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

	public void add(Bloco bloco) {
		blocos[indiceLinha][indiceColuna] = bloco;
		bloco.linha = indiceLinha;
		bloco.coluna = indiceColuna;
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

	Bloco getAfrente(Pivo pivo) {
		Sentido sentido = pivo.sentido;
		if (sentido == Sentido.ACIMA) {
			return getAfrenteAcima(pivo.piso);
		} else if (sentido == Sentido.DIREITA) {
			sentido = Sentido.ABAIXO;
		} else if (sentido == Sentido.ABAIXO) {
			sentido = Sentido.ESQUERDA;
		} else if (sentido == Sentido.ESQUERDA) {
			sentido = Sentido.ACIMA;
		}
		return null;
	}

	private Bloco getAfrenteAcima(Bloco piso) {
		// TODO Auto-generated method stub
		return null;
	}
}