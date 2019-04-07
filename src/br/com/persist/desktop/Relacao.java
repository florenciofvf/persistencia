package br.com.persist.desktop;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Objects;

import org.xml.sax.Attributes;

import br.com.persist.util.Util;
import br.com.persist.xml.XMLUtil;

public class Relacao {
	public static final Color COR_PADRAO_FONTE = Color.BLACK;
	public static final Color COR_PADRAO = Color.BLACK;
	private Color corFonte = COR_PADRAO_FONTE;
	protected int deslocamentoXDesc = -5;
	protected int deslocamentoYDesc = -5;
	private boolean desenharDescricao;
	protected static int diametro = 6;
	private Color cor = COR_PADRAO;
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

		int x = x2 - x1;
		int y = y2 - y1;
		double h = Math.sqrt(x * x + y * y);

		int xPos = posX - x1;
		int yPos = posY - y1;
		double hPos = Math.sqrt(xPos * xPos + yPos * yPos);

		if (hPos > h) {
			return false;
		}

		double auxX = x / h;
		double auxY = y / h;

		int auxX1 = (int) (auxX * hPos);
		int auxY1 = (int) (auxY * hPos);

		return comprimento(auxX1, auxY1, xPos, yPos) < 7;
	}

	private int comprimento(int x1, int y1, int x2, int y2) {
		int x = x2 - x1;
		int y = y2 - y1;
		return (int) Math.sqrt(x * x + y * y);
	}

	public void desenhar(Graphics2D g2) {
		int raio = Objeto.DIAMETRO / 2;
		int meta = raio / 2;

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
			double auxX = x / h;
			double auxY = y / h;

			if (pontoOrigem) {
				int valor = origem.transparente ? meta : raio;
				int auxX1 = (int) (auxX * valor);
				int auxY1 = (int) (auxY * valor);
				g2.fillOval(x1 + auxX1 - m, y1 + auxY1 - m, diametro, diametro);
			}

			if (pontoDestino) {
				int valor = destino.transparente ? meta : raio;
				int auxX2 = (int) (auxX * (h - valor));
				int auxY2 = (int) (auxY * (h - valor));
				g2.fillOval(x1 + auxX2 - m, y1 + auxY2 - m, diametro, diametro);
			}
		}

		if (desenharDescricao && descricao != null) {
			g2.setColor(corFonte);
			g2.drawString(descricao, x1 + deslocamentoXDesc, y1 + deslocamentoYDesc);
		}
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
		util.atributo("cor", cor.getRGB());
		util.fecharTag();
		if (!Util.estaVazio(getDescricao())) {
			util.abrirTag2("desc");
			util.conteudo(Util.escapar(getDescricao())).ql();
			util.finalizarTag("desc");
		}
		util.finalizarTag("relacao");
	}

	public int getDeslocamentoXDesc() {
		return deslocamentoXDesc;
	}

	public void setDeslocamentoXDesc(int deslocamentoXDesc) {
		this.deslocamentoXDesc = deslocamentoXDesc;
	}

	public int getDeslocamentoYDesc() {
		return deslocamentoYDesc;
	}

	public void setDeslocamentoYDesc(int deslocamentoYDesc) {
		this.deslocamentoYDesc = deslocamentoYDesc;
	}
}