package br.com.persist.painel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class Painel extends JPanel {
	private static final long serialVersionUID = 1L;
	private transient Area nor = new Area('N');
	private transient Area sul = new Area('S');
	private transient Area les = new Area('L');
	private transient Area oes = new Area('O');

	public Painel() {
		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				nor.selecionado = nor.contem(x, y);
				sul.selecionado = sul.contem(x, y);
				les.selecionado = les.contem(x, y);
				oes.selecionado = oes.contem(x, y);
				repaint();
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				prePaint();
				repaint();
			}
		});
	}

	public void prePaint() {
		nor.localizar();
		sul.localizar();
		les.localizar();
		oes.localizar();
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
		boolean selecionado;
		int metadeLargura;
		int metadeAltura;
		final char setor;
		Dimension d;
		int x;
		int y;

		Area(char setor) {
			this.setor = setor;
		}

		void localizar() {
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

		boolean contem(int xX, int yY) {
			return (xX >= x && xX <= x + larAlt) && (yY >= y && yY <= y + larAlt);
		}

		void paint(Graphics g) {
			g.drawRect(x, y, larAlt, larAlt);
			if (selecionado) {
				if (setor == 'N') {
					g.drawRect(1, 1, d.width - 3, metadeAltura);
				} else if (setor == 'S') {
					g.drawRect(1, metadeAltura, d.width - 3, metadeAltura - 2);
				} else if (setor == 'L') {
					g.drawRect(metadeLargura, 1, metadeLargura - 2, d.height - 3);
				} else if (setor == 'O') {
					g.drawRect(1, 1, metadeLargura, d.height - 3);
				}
			}
		}
	}
}