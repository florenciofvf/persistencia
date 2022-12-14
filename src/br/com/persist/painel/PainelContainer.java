package br.com.persist.painel;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class PainelContainer extends JPanel {
	private static final long serialVersionUID = 1L;
	private transient PainelSetor nor = new PainelSetor('N');
	private transient PainelSetor sul = new PainelSetor('S');
	private transient PainelSetor les = new PainelSetor('L');
	private transient PainelSetor oes = new PainelSetor('O');

	public PainelContainer() {
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
				localizar();
				repaint();
			}
		});
	}

	public void localizar() {
		nor.localizar(this);
		sul.localizar(this);
		les.localizar(this);
		oes.localizar(this);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		nor.paint(g);
		sul.paint(g);
		les.paint(g);
		oes.paint(g);
	}
}