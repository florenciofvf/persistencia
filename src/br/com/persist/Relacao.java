package br.com.persist;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Objects;

import org.xml.sax.Attributes;

import br.com.persist.util.Util;
import br.com.persist.util.XMLUtil;

public class Relacao {
	public static final Color COR_PADRAO = Color.BLACK;
	private Color cor = COR_PADRAO;
	public static int diametro = 6;
	static int m = diametro / 2;
	private boolean selecionado;
	private String descricao;
	final Objeto destino;
	boolean pontoDestino;
	boolean pontoOrigem;
	final Objeto origem;

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
			descricao = "";
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

	public boolean contem(Objeto objeto) {
		return origem.equals(objeto) || destino.equals(objeto);
	}

	public boolean contem(int posX, int posY) {
		int raio = Objeto.diametro / 2;
		int x1 = origem.x + raio;
		int y1 = origem.y + raio;
		int x2 = destino.x + raio;
		int y2 = destino.y + raio;

		int x = x2 - x1;
		int y = y2 - y1;
		double h = Math.sqrt(x * x + y * y);

		int xPos = posX - x1;
		int yPos = posY - y1;
		double hPos = Math.sqrt(xPos * xPos + yPos * yPos);

		if (hPos > h) {
			return false;
		}

		double X = x / h;
		double Y = y / h;

		int _x1 = (int) (X * hPos);
		int _y1 = (int) (Y * hPos);

		return comprimento(_x1, _y1, xPos, yPos) < 4;
	}

	private int comprimento(int x1, int y1, int x2, int y2) {
		int x = x2 - x1;
		int y = y2 - y1;
		return (int) Math.sqrt(x * x + y * y);
	}

	public void desenhar(Graphics2D g2) {
		int raio = Objeto.diametro / 2;

		g2.setColor(cor);

		if (selecionado) {
			g2.setColor(Color.CYAN);
		}

		int x1 = origem.x + raio;
		int y1 = origem.y + raio;
		int x2 = destino.x + raio;
		int y2 = destino.y + raio;
		g2.drawLine(x1, y1, x2, y2);

		if (pontoOrigem || pontoDestino) {
			int x = x2 - x1;
			int y = y2 - y1;
			double h = Math.sqrt(x * x + y * y);
			double X = x / h;
			double Y = y / h;

			if (pontoOrigem) {
				int _x1 = (int) (X * raio);
				int _y1 = (int) (Y * raio);
				g2.fillOval(x1 + _x1 - m, y1 + _y1 - m, diametro, diametro);
			}

			if (pontoDestino) {
				int _x2 = (int) (X * (h - raio));
				int _y2 = (int) (Y * (h - raio));
				g2.fillOval(x1 + _x2 - m, y1 + _y2 - m, diametro, diametro);
			}
		}
	}

	public void aplicar(Attributes attr) {
		cor = new Color(Integer.parseInt(attr.getValue("cor")));
	}

	public void salvar(XMLUtil util) {
		util.abrirTag("relacao");
		util.atributo("destino", Util.escapar(destino.getId()));
		util.atributo("origem", Util.escapar(origem.getId()));
		util.atributo("pontoDestino", pontoDestino);
		util.atributo("pontoOrigem", pontoOrigem);
		util.atributo("cor", cor.getRGB());
		util.fecharTag();
		if (!Util.estaVazio(getDescricao())) {
			util.abrirTag2("desc");
			util.conteudo(Util.escapar(getDescricao())).ql();
			util.finalizarTag("desc");
		}
		util.finalizarTag("relacao");
	}
}