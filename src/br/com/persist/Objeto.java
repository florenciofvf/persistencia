package br.com.persist;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Icon;

import org.xml.sax.Attributes;

import br.com.persist.util.XMLUtil;

public class Objeto {
	private static long ID;
	public static final Color COR_PADRAO = new Color(64, 80, 34);
	public static final int DIAMETRO_PADRAO = 36;
	public static int diametro = DIAMETRO_PADRAO;
	private Color cor = COR_PADRAO;
	private boolean selecionado;
	private Icon icone;
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

	public Objeto(int x, int y, Icon icone) {
		this(x, y, COR_PADRAO, icone);
	}

	public Objeto(int x, int y, Color cor, Icon icone) {
		this.icone = icone;
		setCor(cor);
		this.x = x;
		this.y = y;
		id = "" + (++ID);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public boolean contem(int x, int y) {
		return (x >= this.x && x <= this.x + diametro) && (y >= this.y && y <= this.y + diametro);
	}

	public static void setDiametro(int diametro) {
		Objeto.diametro = diametro;

		if (Objeto.diametro < DIAMETRO_PADRAO) {
			Objeto.diametro = DIAMETRO_PADRAO;
		}
	}

	public Color getCor() {
		return cor;
	}

	public void setCor(Color cor) {
		this.cor = cor;

		if (this.cor == null) {
			this.cor = COR_PADRAO;
		}
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

		if (icone != null) {
			icone.paintIcon(c, g2, x + raio - 8, y + raio - 8);
		}

		if (selecionado) {
			g2.drawOval(x - margem3, y - margem3, diametro + margem4, diametro + margem4);
		}
	}

	public void aplicar(Attributes attr) {
		cor = new Color(Integer.parseInt(attr.getValue("cor")));
		x = Integer.parseInt(attr.getValue("x"));
		y = Integer.parseInt(attr.getValue("y"));
		id = attr.getValue("id");
	}

	public void salvar(XMLUtil util) {
		util.abrirTag("objeto");
		util.atributo("x", x);
		util.atributo("y", y);
		util.atributo("id", id);
		util.atributo("cor", cor.getRGB());
		util.fecharTag().finalizarTag("objeto");
	}
}