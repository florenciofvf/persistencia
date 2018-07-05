package br.com.persist;

import java.awt.Graphics2D;
import java.io.PrintWriter;
import java.util.Objects;

import org.xml.sax.Attributes;

public class Relacao {
	static int diametro = 6;
	static int m = diametro / 2;
	final boolean ponto1;
	final boolean ponto2;
	final Objeto objeto1;
	final Objeto objeto2;

	public Relacao(Objeto objeto1, Objeto objeto2) {
		this(objeto1, false, objeto2, false);
	}

	public Relacao(Objeto objeto1, boolean ponto1, Objeto objeto2) {
		this(objeto1, ponto1, objeto2, false);
	}

	public Relacao(Objeto objeto1, Objeto objeto2, boolean ponto2) {
		this(objeto1, false, objeto2, ponto2);
	}

	public Relacao(Objeto objeto1, boolean ponto1, Objeto objeto2, boolean ponto2) {
		Objects.requireNonNull(objeto1);
		Objects.requireNonNull(objeto2);
		this.objeto1 = objeto1;
		this.objeto2 = objeto2;
		this.ponto1 = ponto1;
		this.ponto2 = ponto2;
	}

	public void desenhar(Graphics2D g2) {
		int raio = Objeto.diametro / 2;

		int x1 = objeto1.x + raio;
		int y1 = objeto1.y + raio;
		int x2 = objeto2.x + raio;
		int y2 = objeto2.y + raio;
		g2.drawLine(x1, y1, x2, y2);

		if (ponto1 || ponto2) {
			int x = x2 - x1;
			int y = y2 - y1;
			double h = Math.sqrt(x * x + y * y);
			double X = x / h;
			double Y = y / h;

			if (ponto1) {
				int _x1 = (int) (X * raio);
				int _y1 = (int) (Y * raio);
				g2.fillOval(x1 + _x1 - m, y1 + _y1 - m, diametro, diametro);
			}

			if (ponto2) {
				int _x2 = (int) (X * (h - raio));
				int _y2 = (int) (Y * (h - raio));
				g2.fillOval(x1 + _x2 - m, y1 + _y2 - m, diametro, diametro);
			}
		}
	}

	public void aplicar(Attributes attr) {
	}

	public void salvar(PrintWriter pw) {
	}
}