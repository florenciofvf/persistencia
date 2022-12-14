package br.com.persist.painel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class Painel extends JPanel {
	private static final long serialVersionUID = 1L;
	private transient Area nor = new Area();
	private transient Area sul = new Area();
	private transient Area les = new Area();
	private transient Area oes = new Area();

	public Painel() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				prePaint();
				repaint();
			}
		});
	}

	public void prePaint() {
		nor.local('N');
		sul.local('S');
		les.local('L');
		oes.local('O');
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		nor.paint(g);
		sul.paint(g);
		les.paint(g);
		oes.paint(g);
	}

	private class Area {
		int larAlt = 40;
		int meta = larAlt / 2;
		Dimension d;
		int metadeLargura;
		int metadeAltura;
		int x;
		int y;

		void local(char setor) {
			d = getSize();
			metadeLargura = d.width / 2;
			metadeAltura = d.height / 2;
			if (setor == 'N') {
				x = metadeLargura - meta;
				y = meta;
			} else if (setor == 'S') {
				x = metadeLargura - meta;
				y = d.height - larAlt - meta;
			} else if (setor == 'L') {
				x = d.width - larAlt - meta;
				y = metadeAltura - meta;
			} else if (setor == 'O') {
				x = meta;
				y = metadeAltura - meta;
			}
		}

		boolean contem(int X, int Y) {
			return (X >= x && X <= x + larAlt) && (Y >= y && Y <= y + larAlt);
		}

		void paint(Graphics g) {
			g.drawRect(x, y, larAlt, larAlt);
		}
	}
}