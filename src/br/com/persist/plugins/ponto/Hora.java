package br.com.persist.plugins.ponto;

import java.awt.Graphics2D;
import java.util.List;

import br.com.persist.assistencia.HoraUtil;
import br.com.persist.assistencia.HoraUtilException;
import br.com.persist.assistencia.Util;

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

	public void abrir(String string) {
		int posH = string.indexOf("h=");
		int posM = string.indexOf("m=");
		if (posH != -1) {
			if (posM != -1) {
				horas.s = string.substring(posH + 2, posM);
			} else {
				horas.s = string.substring(posH + 2);
			}
		}
		if (posM != -1) {
			minuto.s = string.substring(posM + 2);
		}
	}

	public void limpar() {
		horas.s = null;
		minuto.s = null;
	}

	public String getString() {
		StringBuilder sb = new StringBuilder();
		if (!Util.isEmpty(horas.s)) {
			sb.append("h=" + horas.s);
		}
		if (!Util.isEmpty(minuto.s)) {
			sb.append("m=" + minuto.s);
		}
		return sb.toString();
	}

	public void addPontos(List<Ponto> pontos) {
		pontos.add(horas);
		pontos.add(minuto);
	}
}