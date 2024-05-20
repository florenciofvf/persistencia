package br.com.persist.plugins.ponto;

import java.awt.AWTEvent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import br.com.persist.assistencia.Constantes;
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

	@Override
	public void requestFocus(Ponto p) {
		if (selecionado != null) {
			selecionado.focusOut();
		}
		selecionado = p;
		selecionado.focusIn();
	}

	private class MouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent evento) {
			int x = evento.getX();
			int y = evento.getY();
			for (Ponto p : pontos) {
				if (p.contem(x, y)) {
					p.requestFocus();
				} else {
					p.focusOut();
				}
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
		p.setLargura(30);
		p.setAltura(30);
		p.setX(x);
		p.setY(y);
		return p;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(Constantes.STROKE_PADRAO);
		g2.setFont(PontoConstantes.FONT);
		for (Ponto p : pontos) {
			p.desenhar(g2);
		}
	}

	@Override
	public void desenhar(Ponto p) {
		repaint();
	}

	@Override
	public void tabular(Ponto p) {
		int indice = 0;
		for (int i = 0; i < pontos.length; i++) {
			if (pontos[i] == p) {
				indice = i + 1;
				break;
			}
		}
		if (indice < pontos.length) {
			pontos[indice].requestFocus();
		} else if (pontos.length > 1) {
			pontos[0].requestFocus();
		} else {
			p.focusOut();
		}
	}
}