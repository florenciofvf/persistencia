package br.com.persist.plugins.ponto;

import java.awt.AWTEvent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import br.com.persist.componente.Panel;

public class PontoArea extends Panel implements PontoListener, AWTEventListener {
	private static final long serialVersionUID = 1L;
	private transient Ponto[] pontos = new Ponto[2];
	private transient Ponto selecionado;

	public PontoArea() {
		super(null);
	}

	public void init() {
		Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
		addMouseListener(new MouseListener());
		pontos[0] = criarPonto(300, 200);
		pontos[1] = criarPonto(400, 200);
	}

	private class MouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent evento) {
			int x = evento.getX();
			int y = evento.getY();
			selecionado = null;
			for (Ponto p : pontos) {
				if (p.contem(x, y)) {
					p.setFocus(true);
					selecionado = p;
				} else {
					p.setFocus(false);
				}
			}
			if (selecionado == null) {
				repaint();
			}
		}
	}

	@Override
	public void eventDispatched(AWTEvent event) {
		if (event instanceof KeyEvent) {
			KeyEvent evento = (KeyEvent) event;
			if (selecionado != null) {
				selecionado.setChar(evento.getKeyChar());
			}
		}
	}

	private Ponto criarPonto(int x, int y) {
		Ponto p = new Ponto(this);
		p.setLargura(50);
		p.setAltura(30);
		p.setX(x);
		p.setY(y);
		return p;
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