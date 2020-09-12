package br.com.persist.plugins.objeto;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Objects;

import org.xml.sax.Attributes;

import br.com.persist.util.Constantes;
import br.com.persist.util.Util;
import br.com.persist.xml.XMLUtil;

public class Relacao {
	private static final Color COR_PADRAO_FONTE = Color.BLACK;
	private static final Color COR_PADRAO = Color.BLACK;
	private Color corFonte = COR_PADRAO_FONTE;
	private int deslocamentoXDesc = -5;
	private int deslocamentoYDesc = -5;
	private boolean desenharDescricao;
	private static int diametro = 6;
	private Color cor = COR_PADRAO;
	private final Objeto destino;
	private boolean pontoDestino;
	static int m = diametro / 2;
	private boolean pontoOrigem;
	private final Objeto origem;
	private boolean selecionado;
	private boolean quebrado;
	private String descricao;

	public Relacao(Objeto origem, Objeto destino) {
		this(origem, false, destino, false);
	}

	public Relacao(Objeto origem, boolean pontoOrigem, Objeto destino) {
		this(origem, pontoOrigem, destino, false);
	}

	public Relacao(Objeto origem, Objeto destino, boolean pontoDestino) {
		this(origem, false, destino, pontoDestino);
	}

	public Relacao(Objeto origem, boolean pontoOrigem, Objeto destino, boolean pontoDestino) {
		this.pontoDestino = pontoDestino;
		Objects.requireNonNull(destino);
		this.pontoOrigem = pontoOrigem;
		Objects.requireNonNull(origem);
		this.destino = destino;
		this.origem = origem;

		if (origem == destino || origem.equals(destino)) {
			throw new IllegalStateException();
		}
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setPontoDestino(boolean ponto) {
		this.pontoDestino = ponto;
	}

	public void setPontoOrigem(boolean ponto) {
		this.pontoOrigem = ponto;
	}

	public Color getCorFonte() {
		return corFonte;
	}

	public void setCorFonte(Color corFonte) {
		this.corFonte = corFonte;

		if (this.corFonte == null) {
			this.corFonte = COR_PADRAO_FONTE;
		}
	}

	public boolean isDesenharDescricao() {
		return desenharDescricao;
	}

	public void setDesenharDescricao(boolean desenharDescricao) {
		this.desenharDescricao = desenharDescricao;
	}

	public boolean isQuebrado() {
		return quebrado;
	}

	public void setQuebrado(boolean quebrado) {
		this.quebrado = quebrado;
	}

	public boolean isPontoDestino() {
		return pontoDestino;
	}

	public boolean isPontoOrigem() {
		return pontoOrigem;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setCor(Color cor) {
		this.cor = cor;

		if (this.cor == null) {
			this.cor = COR_PADRAO;
		}
	}

	public String getDescricao() {
		if (descricao == null) {
			descricao = Constantes.VAZIO;
		}

		return descricao;
	}

	public Objeto getDestino() {
		return destino;
	}

	public Objeto getOrigem() {
		return origem;
	}

	public Color getCor() {
		return cor;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (obj instanceof Relacao) {
			Relacao outro = (Relacao) obj;
			return (origem.equals(outro.origem) && destino.equals(outro.destino))
					|| (origem.equals(outro.destino) && destino.equals(outro.origem));
		}

		return false;
	}

	@Override
	public int hashCode() {
		return origem.hashCode() + destino.hashCode();
	}

	public boolean contem(Objeto objeto) {
		return origem.equals(objeto) || destino.equals(objeto);
	}

	public boolean contem(int posX, int posY) {
		int raio = Objeto.DIAMETRO / 2;
		int x1 = origem.x + raio;
		int y1 = origem.y + raio;
		int x2 = destino.x + raio;
		int y2 = destino.y + raio;

		return quebrado ? contemQuebrada(posX, posY, x1, y1, x2, y2) : contemVetor(posX, posY, x1, y1, x2, y2);
	}

	private boolean contemQuebrada(int posX, int posY, int x1, int y1, int x2, int y2) {
		if (x1 == x2 || y1 == y2) {
			return contemVetor(posX, posY, x1, y1, x2, y2);
		}

		int[] x1x2Aux = x1x2(x1, x2);
		int[] y1y2Aux = y1y2(y1, y2);

		if (contemV(posX, posY, y1y2Aux[0], y1y2Aux[1], x1x2Aux[0])) {
			return true;
		}

		int y = 0;

		if (x1 < x2) {
			if (y1 < y2) {
				y = y1y2Aux[1];
			} else {
				y = y1y2Aux[0];
			}
		} else {
			if (y1 < y2) {
				y = y1y2Aux[0];
			} else {
				y = y1y2Aux[1];
			}
		}

		return contemH(posX, posY, x1x2Aux[0], x1x2Aux[1], y);
	}

	private boolean contemVetor(int posX, int posY, int x1, int y1, int x2, int y2) {
		int x = x2 - x1;
		int y = y2 - y1;
		double h = Math.sqrt((double) (x * x + y * y));

		int xPos = posX - x1;
		int yPos = posY - y1;
		double hPos = Math.sqrt((double) (xPos * xPos + yPos * yPos));

		if (hPos > h) {
			return false;
		}

		double auxX = x / h;
		double auxY = y / h;

		int auxX1 = (int) (auxX * hPos);
		int auxY1 = (int) (auxY * hPos);

		return comprimento(auxX1, auxY1, xPos, yPos) < 9;
	}

	private int comprimento(int x1, int y1, int x2, int y2) {
		int x = x2 - x1;
		int y = y2 - y1;
		return (int) Math.sqrt((double) (x * x + y * y));
	}

	private int[] x1x2(int x1, int x2) {
		return x1 < x2 ? new int[] { x1, x2 } : new int[] { x2, x1 };
	}

	private int[] y1y2(int y1, int y2) {
		return y1 < y2 ? new int[] { y1, y2 } : new int[] { y2, y1 };
	}

	private boolean contemH(int posX, int posY, int x1, int x2, int y) {
		int l = x2 - x1;
		y -= 8;
		return contem(posX, posY, x1, y, l, 16);
	}

	private boolean contemV(int posX, int posY, int y1, int y2, int x) {
		int a = y2 - y1;
		x -= 8;
		return contem(posX, posY, x, y1, 16, a);
	}

	private boolean contem(int posX, int posY, int x, int y, int l, int a) {
		return (posX >= x && posX <= x + l) && (posY >= y && posY <= y + a);
	}

	public Objeto criarObjetoMeio() {
		Objeto objeto = new Objeto();

		int difX = (destino.x - origem.x) / 2;
		int difY = (destino.y - origem.y) / 2;

		objeto.x = origem.x + difX;
		objeto.y = origem.y + difY;

		return objeto;
	}

	public void aplicar(Attributes attr) {
		desenharDescricao = Boolean.parseBoolean(attr.getValue("desenharDescricao"));
		deslocamentoXDesc = Integer.parseInt(attr.getValue("desloc_x_desc"));
		deslocamentoYDesc = Integer.parseInt(attr.getValue("desloc_y_desc"));
		corFonte = new Color(Integer.parseInt(attr.getValue("corFonte")));
		quebrado = Boolean.parseBoolean(attr.getValue("quebrado"));
		cor = new Color(Integer.parseInt(attr.getValue("cor")));
	}

	public void salvar(XMLUtil util) {
		util.abrirTag("relacao");
		util.atributo("destino", Util.escapar(destino.getId()));
		util.atributo("desenharDescricao", desenharDescricao);
		util.atributo("origem", Util.escapar(origem.getId()));
		util.atributo("desloc_x_desc", deslocamentoXDesc);
		util.atributo("desloc_y_desc", deslocamentoYDesc);
		util.atributo("corFonte", corFonte.getRGB());
		util.atributo("pontoDestino", pontoDestino);
		util.atributo("pontoOrigem", pontoOrigem);
		util.atributo("quebrado", quebrado);
		util.atributo("cor", cor.getRGB());
		util.fecharTag();
		if (!Util.estaVazio(getDescricao())) {
			util.abrirTag2("desc");
			util.conteudo(Util.escapar(getDescricao())).ql();
			util.finalizarTag("desc");
		}
		util.finalizarTag("relacao");
	}

	public void desenhar(Graphics2D g2, Stroke stroke) {
		int raio = Objeto.DIAMETRO / 2;
		int meta = raio / 2;

		g2.setColor(cor);

		if (selecionado) {
			g2.setStroke(Constantes.STROKE_PADRAO);
			g2.setColor(Color.CYAN);
		}

		int x1 = origem.x + raio;
		int y1 = origem.y + raio;
		int x2 = destino.x + raio;
		int y2 = destino.y + raio;

		if (quebrado) {
			desenharLinhaQuebrada(g2, x1, y1, x2, y2);
		} else {
			g2.drawLine(x1, y1, x2, y2);
		}

		desenharPontos(g2, x1, y1, x2, y2, raio, meta);

		if (desenharDescricao && descricao != null) {
			g2.setColor(corFonte);
			g2.drawString(descricao, x1 + deslocamentoXDesc, y1 + deslocamentoYDesc);
		}

		if (selecionado) {
			g2.setStroke(stroke);
		}
	}

	private void desenharLinhaQuebrada(Graphics2D g2, int x1, int y1, int x2, int y2) {
		if (x1 == x2 || y1 == y2) {
			g2.drawLine(x1, y1, x2, y2);
		} else {
			int[] x1x2Aux = x1x2(x1, x2);
			int[] y1y2Aux = y1y2(y1, y2);

			g2.drawLine(x1x2Aux[0], y1y2Aux[0], x1x2Aux[0], y1y2Aux[1]);

			if (x1 < x2) {
				if (y1 < y2) {
					g2.drawLine(x1x2Aux[0], y1y2Aux[1], x1x2Aux[1], y1y2Aux[1]);
				} else {
					g2.drawLine(x1x2Aux[0], y1y2Aux[0], x1x2Aux[1], y1y2Aux[0]);
				}
			} else {
				if (y1 < y2) {
					g2.drawLine(x1x2Aux[0], y1y2Aux[0], x1x2Aux[1], y1y2Aux[0]);
				} else {
					g2.drawLine(x1x2Aux[0], y1y2Aux[1], x1x2Aux[1], y1y2Aux[1]);
				}
			}
		}
	}

	private void desenharPontos(Graphics2D g2, int x1, int y1, int x2, int y2, int raio, int meta) {
		if (pontoOrigem || pontoDestino) {
			int x = x2 - x1;
			int y = y2 - y1;
			double h = Math.sqrt((double) (x * x + y * y));
			double auxX = x / h;
			double auxY = y / h;

			if (pontoOrigem) {
				int valor = origem.isTransparente() ? meta : raio;
				int auxX1 = (int) (auxX * valor);
				int auxY1 = (int) (auxY * valor);
				g2.fillOval(x1 + auxX1 - m, y1 + auxY1 - m, diametro, diametro);
			}

			if (pontoDestino) {
				int valor = destino.isTransparente() ? meta : raio;
				int auxX2 = (int) (auxX * (h - valor));
				int auxY2 = (int) (auxY * (h - valor));
				g2.fillOval(x1 + auxX2 - m, y1 + auxY2 - m, diametro, diametro);
			}
		}
	}

	public int getDeslocamentoXDesc() {
		return deslocamentoXDesc;
	}

	public void setDeslocamentoXDesc(int deslocamentoXDesc) {
		this.deslocamentoXDesc = deslocamentoXDesc;
	}

	public void deslocamentoXDescDelta(int delta) {
		this.deslocamentoXDesc += delta;
	}

	public int getDeslocamentoYDesc() {
		return deslocamentoYDesc;
	}

	public void setDeslocamentoYDesc(int deslocamentoYDesc) {
		this.deslocamentoYDesc = deslocamentoYDesc;
	}

	public void deslocamentoYDescDelta(int delta) {
		this.deslocamentoYDesc += delta;
	}
}