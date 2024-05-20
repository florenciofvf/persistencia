package br.com.persist.plugins.ponto;

import java.awt.Graphics;
import java.awt.Graphics2D;

import br.com.persist.componente.Panel;

public class PontoArea extends Panel implements PontoListener {
	private static final long serialVersionUID = 1L;
	private transient Ponto[] pontos = new Ponto[1];

	public PontoArea() {
		super(null);
		Ponto p = new Ponto(this);
		p.setX(300);
		p.setY(300);
		p.setLargura(50);
		p.setAltura(30);
		p.setFocus(true);
		pontos[0] = p;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		for (Ponto p : pontos) {
			p.desenhar(g2);
		}
	}

	@Override
	public void desenhar(Ponto p) {
		repaint();
	}
}