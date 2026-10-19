package br.com.persist.plugins.labirinto;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Constantes;

public class Matriz {
	private final Bloco[][] blocos;
	private final int colunas;
	private final int linhas;
	private int indiceColuna;
	private int indiceLinha;
	private int lado = 40;
	protected Pivo pivo;
	private int total;

	public Matriz(int linhas, int colunas) {
		this.linhas = linhas;
		this.colunas = colunas;
		blocos = new Bloco[linhas][colunas];
	}

	public int getTamanho() {
		return linhas * colunas;
	}

	public int getTotal() {
		return total;
	}

	public void setLado(int lado) {
		this.lado = lado;
	}

	public void add(Bloco bloco) {
		if (bloco.pivo != null) {
			pivo = bloco.pivo;
		}
		blocos[indiceLinha][indiceColuna] = bloco;
		total++;
		bloco.linha = indiceLinha;
		bloco.coluna = indiceColuna;
		indiceColuna++;
		if (indiceColuna == colunas) {
			indiceColuna = 0;
			indiceLinha++;
		}
	}

	public boolean click(int x, int y) {
		Bloco bloco = getBloco(x, y);
		if (bloco != null) {
			bloco.click();
			return true;
		}
		return false;
	}

	private Bloco getBloco(int x, int y) {
		for (int linha = 0; linha < linhas; linha++) {
			for (int coluna = 0; coluna < colunas; coluna++) {
				Bloco bloco = blocos[linha][coluna];
				if (bloco.contem(lado, x, y)) {
					return bloco;
				}
			}
		}
		return null;
	}

	public void desenhar2D(Graphics2D g2) {
		g2.setColor(Face.COR_PISO);
		int largura = colunas * lado;
		int altura = linhas * lado;
		g2.fillRect(0, 0, largura, altura);
		for (int linha = 0; linha < linhas; linha++) {
			for (int coluna = 0; coluna < colunas; coluna++) {
				Bloco bloco = blocos[linha][coluna];
				bloco.desenhar2D(lado, g2);
			}
		}
	}

	public void preencher() {
		for (int linha = 0; linha < linhas; linha++) {
			for (int coluna = 0; coluna < colunas; coluna++) {
				add(new Bloco(false));
			}
		}
	}

	public void completar() {
		while (total < getTamanho()) {
			add(new Bloco(false));
		}
	}

	public void criarPivo() {
		if (pivo != null) {
			return;
		}
		Bloco bloco = getBlocoParaPivo();
		pivo = new Pivo();
		pivo.ocupar(bloco);
	}

	private Bloco getBlocoParaPivo() {
		for (int linha = linhas - 1; linha >= 0; linha--) {
			for (int coluna = 0; coluna < colunas; coluna++) {
				Bloco bloco = blocos[linha][coluna];
				if (bloco.vazio) {
					return bloco;
				}
			}
		}
		Bloco bloco = blocos[linhas - 1][0];
		bloco.vazio = true;
		return bloco;
	}

	public String gerarStringFile() {
		StringBuilder builder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		builder.append(Constantes.QL2);
		builder.append("<matriz linhas=\"" + linhas + "\" colunas=\"" + colunas + "\">").append(Constantes.QL);
		for (int linha = 0; linha < linhas; linha++) {
			for (int coluna = 0; coluna < colunas; coluna++) {
				Bloco bloco = blocos[linha][coluna];
				builder.append("<cell");
				if (bloco.vazio) {
					builder.append(" vazio=\"true\"");
				}
				if (bloco.pivo != null) {
					builder.append(" pivo=\"true\"");
				}
				builder.append("/>");
				builder.append(Constantes.QL);
			}
			builder.append(Constantes.QL);
		}
		builder.append("</matriz>");
		return builder.toString();
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
		int linhaBkp = linha + 1;
		Bloco anterior = null;
		List<Bloco> resp = new ArrayList<>();
		if (linha >= 0 && linha < linhas && coluna >= 0 && coluna < colunas) {
			while (linha >= 0) {
				resp.add(blocos[linha][coluna]);
				linha--;
			}
			if (linhaBkp >= 0 && linhaBkp < linhas) {
				anterior = blocos[linhaBkp][coluna];
			}
		}
		return LabirintoUtil.limitar(resp, anterior);
	}

	private List<Bloco> getBlocosDireita(int linha, int coluna) {
		int colunaBkp = coluna - 1;
		Bloco anterior = null;
		List<Bloco> resp = new ArrayList<>();
		if (linha >= 0 && linha < linhas && coluna >= 0 && coluna < colunas) {
			while (coluna < colunas) {
				resp.add(blocos[linha][coluna]);
				coluna++;
			}
			if (colunaBkp >= 0 && colunaBkp < colunas) {
				anterior = blocos[linha][colunaBkp];
			}
		}
		return LabirintoUtil.limitar(resp, anterior);
	}

	private List<Bloco> getBlocosAbaixo(int linha, int coluna) {
		int linhaBkp = linha - 1;
		Bloco anterior = null;
		List<Bloco> resp = new ArrayList<>();
		if (linha >= 0 && linha < linhas && coluna >= 0 && coluna < colunas) {
			while (linha < linhas) {
				resp.add(blocos[linha][coluna]);
				linha++;
			}
			if (linhaBkp >= 0 && linhaBkp < linhas) {
				anterior = blocos[linhaBkp][coluna];
			}
		}
		return LabirintoUtil.limitar(resp, anterior);
	}

	private List<Bloco> getBlocosEsquerda(int linha, int coluna) {
		int colunaBkp = coluna + 1;
		Bloco anterior = null;
		List<Bloco> resp = new ArrayList<>();
		if (linha >= 0 && linha < linhas && coluna >= 0 && coluna < colunas) {
			while (coluna >= 0) {
				resp.add(blocos[linha][coluna]);
				coluna--;
			}
			if (colunaBkp >= 0 && colunaBkp < colunas) {
				anterior = blocos[linha][colunaBkp];
			}
		}
		return LabirintoUtil.limitar(resp, anterior);
	}
}