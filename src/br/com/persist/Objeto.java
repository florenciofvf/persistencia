package br.com.persist;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Icon;

import org.xml.sax.Attributes;

import br.com.persist.util.Imagens;
import br.com.persist.util.Util;
import br.com.persist.util.XMLUtil;

public class Objeto {
	public static final Color COR_PADRAO = new Color(64, 80, 34);
	public static final int DIAMETRO_PADRAO = 36;
	public static int diametro = DIAMETRO_PADRAO;
	private Color cor = COR_PADRAO;
	private boolean selecionado;
	private boolean desenharId;
	private String complemento;
	public boolean controlado;
	private String descricao;
	private static long ID;
	private String tabela;
	private String chaves;
	private String icone;
	private int desloc;
	private Icon icon;
	private String id;
	public int x;
	public int y;

	public Objeto() {
		this(0, 0, null, null);
	}

	public Objeto(int x, int y) {
		this(x, y, null, null);
	}

	public Objeto(int x, int y, Color cor) {
		this(x, y, cor, null);
	}

	public Objeto(int x, int y, String icone) {
		this(x, y, COR_PADRAO, icone);
	}

	public Objeto(int x, int y, Color cor, String icone) {
		id = "" + (++ID);
		setIcone(icone);
		setCor(cor);
		this.x = x;
		this.y = y;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;

		if (!this.selecionado) {
			controlado = false;
		}
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public void setDesenharId(boolean desenharId) {
		this.desenharId = desenharId;
	}

	public static void setDiametro(int diametro) {
		Objeto.diametro = diametro;

		if (Objeto.diametro < DIAMETRO_PADRAO) {
			Objeto.diametro = DIAMETRO_PADRAO;
		}
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setTabela(String tabela) {
		this.tabela = tabela;
	}

	public void setChaves(String chaves) {
		this.chaves = chaves;
	}

	public void setIcone(String icone) {
		this.icone = icone;

		if (Util.estaVazio(this.icone)) {
			this.icone = "";
		} else {
			icon = Imagens.getIcon(this.icone);
		}
	}

	public String getComplemento() {
		if (Util.estaVazio(complemento)) {
			complemento = "";
		}

		return complemento;
	}

	public String getChaves() {
		if (Util.estaVazio(chaves)) {
			chaves = "";
		}

		return chaves;
	}

	public String getTabela() {
		if (Util.estaVazio(tabela)) {
			tabela = "";
		}

		return tabela;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public boolean isDesenharId() {
		return desenharId;
	}

	public void setId(String id) {
		if (!Util.estaVazio(id)) {
			this.id = id;
		}
	}

	public String getDescricao() {
		if (descricao == null) {
			descricao = "";
		}

		return descricao;
	}

	public String getIcone() {
		return icone;
	}

	public Icon getIcon() {
		return icon;
	}

	public Color getCor() {
		return cor;
	}

	public String getId() {
		return id;
	}

	public void setCor(Color cor) {
		this.cor = cor;

		if (this.cor == null) {
			this.cor = COR_PADRAO;
		}
	}

	public boolean contem(int x, int y) {
		return (x >= this.x && x <= this.x + diametro) && (y >= this.y && y <= this.y + diametro);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (obj instanceof Objeto) {
			Objeto outro = (Objeto) obj;
			return id.equals(outro.id);
		}

		return false;
	}

	public void desenhar(Component c, Graphics2D g2) {
		Composite composite = g2.getComposite();
		Shape shape = g2.getClip();

		final int raio = diametro / 2;
		final int margem2 = 2;
		final int margem3 = 3;
		final int margem4 = 4;
		final int largura = diametro - margem2;
		final int altura = diametro - margem2;
		final int largura2 = largura - margem4;
		final int altura2 = altura - margem4;
		final int altura22 = altura2 / 2;
		final int altura3 = altura2 / 3;

		g2.setColor(Color.DARK_GRAY);
		g2.fillRoundRect(x, y, largura + 1, altura + 1, diametro, diametro);

		Color inicio = cor.darker();
		Color finall = cor.brighter();
		Paint paint = new GradientPaint(x, y, inicio, x, y + altura, finall, false);
		g2.setPaint(paint);
		g2.fillRoundRect(x, y, largura, altura, diametro, diametro);

		inicio = Color.WHITE;
		finall = cor.brighter();
		paint = new GradientPaint(x, y + margem3, inicio, x, y + margem3 + (altura22), cor.brighter(), false);

		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
		g2.setPaint(paint);

		g2.setClip(new RoundRectangle2D.Float(x + margem3, y + margem3, largura2 - 2, altura22, altura3, altura3));
		g2.fillRoundRect(x + margem3, y + margem3, largura2 - 2, altura2, altura2, altura2);

		g2.setComposite(composite);
		g2.setClip(shape);

		if (icon != null) {
			icon.paintIcon(c, g2, x + raio - 8, y + raio - 8);
		}

		if (selecionado) {
			g2.drawOval(x - margem3, y - margem3, diametro + margem4, diametro + margem4);
		}

		if (desenharId) {
			g2.drawString(id, desloc + x, y - 5);
		}
	}

	public void aplicar(Attributes attr) {
		desenharId = Boolean.parseBoolean(attr.getValue("desenharId"));
		cor = new Color(Integer.parseInt(attr.getValue("cor")));
		complemento = attr.getValue("complemento");
		x = Integer.parseInt(attr.getValue("x"));
		y = Integer.parseInt(attr.getValue("y"));
		setIcone(attr.getValue("icone"));
		tabela = attr.getValue("tabela");
		chaves = attr.getValue("chaves");
		id = attr.getValue("id");
	}

	public void salvar(XMLUtil util) {
		util.abrirTag("objeto");
		util.atributo("complemento", Util.escapar(getComplemento()));
		util.atributo("desenharId", desenharId);
		util.atributo("id", Util.escapar(id));
		util.atributo("tabela", getTabela());
		util.atributo("chaves", getChaves());
		util.atributo("cor", cor.getRGB());
		util.atributo("icone", icone);
		util.atributo("x", x);
		util.atributo("y", y);
		util.fecharTag();
		if (!Util.estaVazio(getDescricao())) {
			util.abrirTag2("desc");
			util.conteudo(Util.escapar(getDescricao())).ql();
			util.finalizarTag("desc");
		}
		util.finalizarTag("objeto");
	}

	public void alinhar(FontMetrics fm) {
		if (fm == null) {
			return;
		}

		int largura = fm.stringWidth(id);
		int metade = largura / 2;
		int raio = diametro / 2;
		desloc = raio - metade;
	}
}