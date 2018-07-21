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
	final Objeto objeto1;
	final Objeto objeto2;
	boolean ponto1;
	boolean ponto2;

	public Relacao(Objeto objeto1, Objeto objeto2) {
		this(objeto1, false, objeto2, false);
	}

	public Relacao(Objeto objeto1, boolean ponto1, Objeto objeto2) {
		this(objeto1, ponto1, objeto2, false);
	}

	public Relacao(Objeto objeto1, Objeto objeto2, boolean ponto2) {
		this(objeto1, false, objeto2, ponto2);
	}

	public Relacao(Objeto objeto1, boolean ponto1, Objeto objeto2, boolean ponto2) {
		Objects.requireNonNull(objeto1);
		Objects.requireNonNull(objeto2);
		this.objeto1 = objeto1;
		this.objeto2 = objeto2;
		this.ponto1 = ponto1;
		this.ponto2 = ponto2;

		if (objeto1 == objeto2 || objeto1.equals(objeto2)) {
			throw new IllegalStateException();
		}
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setPonto1(boolean ponto1) {
		this.ponto1 = ponto1;
	}

	public void setPonto2(boolean ponto2) {
		this.ponto2 = ponto2;
	}

	public void setCor(Color cor) {
		this.cor = cor;

		if (this.cor == null) {
			this.cor = COR_PADRAO;
		}
	}

	public Color getCor() {
		return cor;
	}

	public String getDescricao() {
		if (descricao == null) {
			descricao = "";
		}

		return descricao;
	}

	public boolean isPonto1() {
		return ponto1;
	}

	public boolean isPonto2() {
		return ponto2;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (obj instanceof Relacao) {
			Relacao outro = (Relacao) obj;
			return (objeto1.equals(outro.objeto1) && objeto2.equals(outro.objeto2))
					|| (objeto1.equals(outro.objeto2) && objeto2.equals(outro.objeto1));
		}

		return false;
	}

	public boolean contem(Objeto objeto) {
		return objeto1.equals(objeto) || objeto2.equals(objeto);
	}

	public boolean contem(int posX, int posY) {
		int raio = Objeto.diametro / 2;
		int x1 = objeto1.x + raio;
		int y1 = objeto1.y + raio;
		int x2 = objeto2.x + raio;
		int y2 = objeto2.y + raio;

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

		int x1 = objeto1.x + raio;
		int y1 = objeto1.y + raio;
		int x2 = objeto2.x + raio;
		int y2 = objeto2.y + raio;
		g2.drawLine(x1, y1, x2, y2);

		if (ponto1 || ponto2) {
			int x = x2 - x1;
			int y = y2 - y1;
			double h = Math.sqrt(x * x + y * y);
			double X = x / h;
			double Y = y / h;

			if (ponto1) {
				int _x1 = (int) (X * raio);
				int _y1 = (int) (Y * raio);
				g2.fillOval(x1 + _x1 - m, y1 + _y1 - m, diametro, diametro);
			}

			if (ponto2) {
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
		util.atributo("objeto1", objeto1.getId());
		util.atributo("ponto1", ponto1);
		util.atributo("objeto2", objeto2.getId());
		util.atributo("ponto2", ponto2);
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