package br.com.persist.plugins.labirinto;

import java.awt.Color;
import java.awt.Graphics2D;

public class Pivo {
	Sentido sentido;
	Bloco piso;

	Pivo() {
		sentido = Sentido.ACIMA;
	}

	void ocupar(Bloco bloco) {
		if (bloco == null) {
			return;
		}
		if (piso != null) {
			piso.pivo = null;
		}
		bloco.pivo = this;
		bloco.offset = 0;
		piso = bloco;
	}

	boolean caminhar() {
		if (piso.offset < 200) {
			piso.offset += 20;
			return true;
		}
		return false;
	}

	void horario() {
		if (sentido == Sentido.ACIMA) {
			sentido = Sentido.DIREITA;
		} else if (sentido == Sentido.DIREITA) {
			sentido = Sentido.ABAIXO;
		} else if (sentido == Sentido.ABAIXO) {
			sentido = Sentido.ESQUERDA;
		} else if (sentido == Sentido.ESQUERDA) {
			sentido = Sentido.ACIMA;
		}
	}

	void antiHorario() {
		if (sentido == Sentido.ACIMA) {
			sentido = Sentido.ESQUERDA;
		} else if (sentido == Sentido.ESQUERDA) {
			sentido = Sentido.ABAIXO;
		} else if (sentido == Sentido.ABAIXO) {
			sentido = Sentido.DIREITA;
		} else if (sentido == Sentido.DIREITA) {
			sentido = Sentido.ACIMA;
		}
	}

	public void desenhar2D(int x, int y, int lado, Graphics2D g2) {
		g2.setColor(Color.RED);
		lado /= 2;
		int m = lado / 2;
		g2.fillOval(x + m, y + m, lado, lado);
	}
}