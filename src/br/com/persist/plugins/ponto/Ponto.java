package br.com.persist.plugins.ponto;

import java.awt.Color;
import java.awt.Graphics2D;

import br.com.persist.assistencia.Constantes;

public class Ponto {
	private PontoListener listener;
	private boolean focusSet;
	private boolean focus;
	private Thread thread;
	private int largura;
	private int altura;
	private String s;
	private int x;
	private int y;

	public Ponto(PontoListener listener) {
		this.listener = listener;
	}

	public int getLargura() {
		return largura;
	}

	public void setLargura(int largura) {
		this.largura = largura;
	}

	public int getAltura() {
		return altura;
	}

	public void setAltura(int altura) {
		this.altura = altura;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean contem(int x, int y) {
		return (x >= this.x && x <= this.x + largura) && (y >= this.y && y <= this.y + altura);
	}

	public void desenhar(Graphics2D g2) {
		g2.setStroke(Constantes.STROKE_PADRAO);
		g2.setColor(Color.WHITE);
		g2.fillRect(x, y, largura, altura);
		g2.setColor(Color.GRAY);
		g2.drawRect(x, y, largura, altura);
		if (focusSet) {
			g2.setColor(Color.BLACK);
			g2.drawLine(x + 3, y + 3, x + 3, y + altura - 3);
		}
		if (s != null) {
			g2.drawString(s, x + 3, y + 10);
		}
	}

	public PontoListener getListener() {
		return listener;
	}

	public void setListener(PontoListener listener) {
		this.listener = listener;
	}

	public boolean isFocus() {
		return focus;
	}

	public void setFocus(boolean focus) {
		this.focus = focus;
		if (focus) {
			iniciar();
		} else {
			parar();
		}
	}

	public void setChar(char c) {
		s = "" + c;
	}

	public synchronized void iniciar() {
		if (thread == null) {
			thread = new Thread(new Processar());
			thread.start();
		}
	}

	public synchronized void parar() {
		if (thread != null) {
			focus = false;
			thread.interrupt();
			thread = null;
		}
	}

	class Processar implements Runnable {
		@Override
		public void run() {
			while (focus && listener != null && !Thread.currentThread().isInterrupted()) {
				focusSet = !focusSet;
				listener.desenhar(Ponto.this);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}
}