package br.com.persist.plugins.labirinto;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class Matriz {
	private final Bloco[][] blocos;
	private final int colunas;
	private final int linhas;
	private int indiceColuna;
	private int indiceLinha;
	int lado = 40;
	Pivo pivo;

	public Matriz(int linhas, int colunas) {
		this.linhas = linhas;
		this.colunas = colunas;
		blocos = new Bloco[linhas][colunas];
	}

	public void add(Bloco bloco) {
		if (bloco.pivo != null) {
			pivo = bloco.pivo;
		}
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

	public List<Bloco> getBlocosNaFrente(Pivo pivo) {
		Sentido sentido = pivo.sentido;
		int linha = pivo.piso.linha;
		int coluna = pivo.piso.coluna;
		return getBlocos(sentido, linha, coluna);
	}

	public List<Bloco> getBlocosLadoEsquerdo(Pivo pivo) {
		Sentido sentido = pivo.sentido;
		int linha = pivo.piso.linha;
		int coluna = pivo.piso.coluna;
		if (sentido == Sentido.ACIMA) {
			coluna--;
		} else if (sentido == Sentido.DIREITA) {
			linha--;
		} else if (sentido == Sentido.ABAIXO) {
			coluna++;
		} else if (sentido == Sentido.ESQUERDA) {
			linha++;
		}
		return getBlocos(sentido, linha, coluna);
	}

	public List<Bloco> getBlocosLadoDireito(Pivo pivo) {
		Sentido sentido = pivo.sentido;
		int linha = pivo.piso.linha;
		int coluna = pivo.piso.coluna;
		if (sentido == Sentido.ACIMA) {
			coluna++;
		} else if (sentido == Sentido.DIREITA) {
			linha++;
		} else if (sentido == Sentido.ABAIXO) {
			coluna--;
		} else if (sentido == Sentido.ESQUERDA) {
			linha--;
		}
		return getBlocos(sentido, linha, coluna);
	}

	private List<Bloco> getBlocos(Sentido sentido, int linha, int coluna) {
		if (sentido == Sentido.ACIMA) {
			return getBlocosAcima(linha, coluna);
		} else if (sentido == Sentido.DIREITA) {
			return getBlocosDireita(linha, coluna);
		} else if (sentido == Sentido.ABAIXO) {
			return getBlocosAbaixo(linha, coluna);
		} else if (sentido == Sentido.ESQUERDA) {
			return getBlocosEsquerda(linha, coluna);
		}
		return new ArrayList<>();
	}

	private List<Bloco> getBlocosAcima(int linha, int coluna) {
		List<Bloco> resp = new ArrayList<>();
		if (linha >= 0 && linha < linhas && coluna >= 0 && coluna < colunas) {
			while (linha >= 0) {
				resp.add(blocos[linha][coluna]);
				linha--;
			}
		}
		return LabirintoUtil.limitar(resp);
	}

	private List<Bloco> getBlocosDireita(int linha, int coluna) {
		List<Bloco> resp = new ArrayList<>();
		if (linha >= 0 && linha < linhas && coluna >= 0 && coluna < colunas) {
			while (coluna < colunas) {
				resp.add(blocos[linha][coluna]);
				coluna++;
			}
		}
		return LabirintoUtil.limitar(resp);
	}

	private List<Bloco> getBlocosAbaixo(int linha, int coluna) {
		List<Bloco> resp = new ArrayList<>();
		if (linha >= 0 && linha < linhas && coluna >= 0 && coluna < colunas) {
			while (linha < linhas) {
				resp.add(blocos[linha][coluna]);
				linha++;
			}
		}
		return LabirintoUtil.limitar(resp);
	}

	private List<Bloco> getBlocosEsquerda(int linha, int coluna) {
		List<Bloco> resp = new ArrayList<>();
		if (linha >= 0 && linha < linhas && coluna >= 0 && coluna < colunas) {
			while (coluna >= 0) {
				resp.add(blocos[linha][coluna]);
				coluna--;
			}
		}
		return LabirintoUtil.limitar(resp);
	}
}