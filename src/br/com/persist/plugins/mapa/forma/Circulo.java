package br.com.persist.plugins.mapa.forma;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import br.com.persist.plugins.mapa.Atributo;
import br.com.persist.plugins.mapa.Objeto;

public class Circulo extends Forma {
	private Vetor3D v = new Vetor3D(0, 0, 0);

	public Circulo(int x, int y, int z, int diametro, Objeto objeto) {
		super(x, y, z, diametro, objeto);
	}

	public void desenhar(Graphics2D g2) {
		int diametro = this.diametro + (int) (vetor.z / 10);
		int diametroMet = diametro / 2;

		GradientPaint gradiente = new GradientPaint((float) (xOrigem + vetor.x), (float) (yOrigem + vetor.y),
				corGradiente1, (float) (xOrigem + vetor.x + diametro), (float) (yOrigem + vetor.y + diametro),
				corGradiente2);

		g2.setPaint(gradiente);

		g2.fill(new Ellipse2D.Double(xOrigem + vetor.x, yOrigem + vetor.y, diametro, diametro));

		g2.setColor(Color.BLACK);

		g2.drawString(nome, (int) (xOrigem + vetor.x), (int) (yOrigem + vetor.y + diametroMet));
		g2.drawString("(" + qtdObjetos + ")", (int) (xOrigem + vetor.x + 10),
				(int) (yOrigem + vetor.y + diametroMet + 10));

		int metade = diametro / 2;
		int xx = xOrigem + metade;
		int yy = yOrigem + metade;
		v.x = (double) (metade + 10);
		v.y = 0;
		v.z = 0;
		v.rotacaoZ(-75);

		if (DESENHAR_ATRIBUTOS) {
			int i = objeto.getQtdAtributos();
			if (i > 0) {
				int g = 180 / i;
				for (Atributo a : objeto.getAtributos()) {
					g2.drawString(a.toString(), (int) (xx + vetor.x + v.x), (int) (yy + vetor.y + v.y));
					v.rotacaoZ(g);
				}
			}
		}
	}

	public boolean contem(int x, int y) {
		int diametro = this.diametro + (int) (vetor.z / 10);
		return (x >= xOrigem + vetor.x && x <= xOrigem + vetor.x + diametro)
				&& (y >= yOrigem + vetor.y && y <= yOrigem + vetor.y + diametro);
	}

	@Override
	public int[] getXYCentro() {
		int diametro = this.diametro + (int) (vetor.z / 10);
		int metade = diametro / 2;
		int[] xy = new int[2];
		xy[0] = (int) (xOrigem + vetor.x + metade);
		xy[1] = (int) (yOrigem + vetor.y + metade);
		return xy;
	}
}