package br.com.persist.plugins.mapa.forma;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import br.com.persist.assistencia.Vetor3D;
import br.com.persist.plugins.mapa.Atributo;
import br.com.persist.plugins.mapa.Objeto;

public class Circulo extends Forma {
	private Vetor3D v = new Vetor3D(0, 0, 0);

	public Circulo(int x, int y, int z, int diametro, Objeto objeto) {
		super(x, y, z, diametro, objeto);
	}

	public void desenhar(Graphics2D g2) {
		int diametro = this.diametro + (int) (vetor.z / 10);

		GradientPaint gradiente = new GradientPaint((float) (xOrigem + vetor.x), (float) (yOrigem + vetor.y),
				corGradiente1, (float) (xOrigem + vetor.x + diametro), (float) (yOrigem + vetor.y + diametro),
				corGradiente2);

		g2.setPaint(gradiente);

		g2.fill(new Ellipse2D.Float(xOrigem + vetor.x, yOrigem + vetor.y, diametro, diametro));

		g2.setColor(Color.BLACK);

		g2.drawString(nome, xOrigem + vetor.x, yOrigem + vetor.y + diametro / 2);
		g2.drawString("(" + qtdObjetos + ")", xOrigem + vetor.x + 10, yOrigem + vetor.y + diametro / 2 + 10);

		int metade = diametro / 2;
		int X = xOrigem + metade;
		int Y = yOrigem + metade;
		v.x = metade + 10;
		v.y = 0;
		v.z = 0;
		v.rotacaoZ(-75);

		if (desenharAtributos) {
			int i = objeto.getQtdAtributos();
			if (i > 0) {
				int g = 180 / i;
				for (Atributo a : objeto.getAtributos()) {
					g2.drawString(a.toString(), X + vetor.x + v.x, Y + vetor.y + v.y);
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