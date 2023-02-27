package br.com.persist.plugins.mapa.form;

import java.awt.event.MouseEvent;

public class Click {
	final int clicks;
	final int x;
	final int y;

	public Click(MouseEvent evento) {
		this(evento.getX(), evento.getY(), evento.getClickCount());
	}

	public Click(int x, int y, int clicks) {
		this.clicks = clicks;
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public String toString() {
		return "x=" + x + " y=" + y + " clicks=" + clicks;
	}
}