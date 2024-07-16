package br.com.persist.plugins.ponto;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Objects;

public class Ponto {
	private final PontoListener listener;
	private final Cursor cursor;
	private Thread thread;
	private Ponto proximo;
	int largura;
	int altura;
	String s;
	int x;
	int y;

	public Ponto(PontoListener listener) {
		this.listener = Objects.requireNonNull(listener);
		cursor = new Cursor();
		largura = 44;
		altura = 30;
	}

	public Ponto getProximo() {
		return proximo;
	}

	public void setProximo(Ponto proximo) {
		this.proximo = proximo;
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

	public void desenhar(Graphics2D g2, int larChar) {
		g2.setColor(Color.WHITE);
		g2.fillRect(x, y, largura, altura);
		g2.setColor(Color.GRAY);
		g2.drawRect(x, y, largura, altura);
		if (s != null) {
			g2.setColor(Color.GRAY);
			g2.drawString(s, x + 3, y + altura - 5);
		}
		cursor.desenhar(g2, larChar);
	}

	class Cursor {
		boolean visivel;
		boolean focus;

		void desenhar(Graphics2D g2, int larChar) {
			if (focus) {
				g2.setColor(PontoConstantes.COR_FOCUS);
				g2.drawRect(x, y, largura, altura);
			}
			if (visivel) {
				g2.setColor(Color.BLACK);
				if (s == null) {
					g2.drawLine(x + 3, y + 3, x + 3, y + altura - 3);
				} else {
					int pos = larChar * s.length() + 3;
					g2.drawLine(x + pos, y + 3, x + pos, y + altura - 3);
				}
			}
		}

		void in() {
			visivel = true;
			focus = true;
		}

		void out() {
			visivel = false;
			focus = false;
		}
	}

	public void requestFocus() {
		listener.requestFocus(this);
	}

	void focusIn() {
		cursor.in();
		iniciar();
	}

	void focusOut() {
		cursor.out();
		parar();
	}

	public void setChar(char c) {
		if (c >= '0' && c <= '9') {
			if (s == null) {
				s = "" + c;
			} else if (s.length() < 2) {
				s += c;
				proximoFocus();
			}
			repaint();
		} else if (c == '\b') {
			if (s == null) {
				repaint();
			} else if (s.length() > 0) {
				s = s.substring(0, s.length() - 1);
				if (s.length() == 0) {
					s = null;
				}
				repaint();
			}
		} else if (c == '\t' && proximo != null) {
			proximo.requestFocus();
		}
	}

	private void proximoFocus() {
		if (s.length() == 2 && proximo != null) {
			proximo.requestFocus();
		}
	}

	private synchronized void iniciar() {
		if (thread == null) {
			thread = new Thread(new Processar());
			thread.start();
		}
	}

	private synchronized void parar() {
		if (thread != null) {
			thread.interrupt();
			thread = null;
		}
	}

	private void repaint() {
		listener.desenhar(this);
	}

	class Processar implements Runnable {
		@Override
		public void run() {
			int delay = 500;
			repaint();
			sleep(delay);
			while (!Thread.currentThread().isInterrupted()) {
				cursor.visivel = !cursor.visivel;
				repaint();
				sleep(delay);
			}
			focusOut();
		}

		private void sleep(final int delay) {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}