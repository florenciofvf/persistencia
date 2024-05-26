package br.com.persist.plugins.ponto;

import java.awt.Graphics2D;

import br.com.persist.assistencia.HoraUtil;
import br.com.persist.assistencia.HoraUtilException;

public class Hora {
	private final Ponto horas;
	private final Ponto minuto;

	public Hora(PontoListener listener) {
		horas = new Ponto(listener);
		minuto = new Ponto(listener);
	}

	public Ponto get(int x, int y) {
		if (horas.contem(x, y)) {
			return horas;
		}
		if (minuto.contem(x, y)) {
			return minuto;
		}
		return null;
	}

	public void desenhar(Graphics2D g2, int larChar) {
		horas.desenhar(g2, larChar);
		g2.drawString(":", horas.x + horas.largura - 6, horas.y + horas.altura - 6);
		minuto.desenhar(g2, larChar);
	}

	public int getTotalSegundos() throws HoraUtilException {
		if (horas.s != null && minuto.s != null) {
			return HoraUtil.getSegundos(horas.s + ":" + minuto.s + ":00");
		}
		return 0;
	}

	public void setX(int x) {
		horas.setX(x);
		minuto.setX(x + 50);
	}

	public void setY(int y) {
		horas.setY(y);
		minuto.setY(y);
	}

	public int getLargura() {
		return minuto.x + minuto.largura;
	}

	public int getY() {
		return horas.y;
	}

	public int getAltura() {
		return horas.altura;
	}

	public String getString() {
		StringBuilder sb = new StringBuilder();
		if (horas.s != null) {
			sb.append("h=" + horas.s);
		}
		if (minuto.s != null) {
			sb.append("m=" + minuto.s);
		}
		return sb.toString();
	}
}