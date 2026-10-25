package br.com.persist.plugins.labirinto;

import java.awt.Color;
import java.awt.Graphics2D;

public class Pivo {
	public static final int TOTAL_PASSOS_PISO = 8;
	double comprimentoFracao;
	private int passos;
	Sentido sentido;
	Bloco piso;

	Pivo() {
		sentido = Sentido.ACIMA;
	}

	double calibrar(double comprimento) {
		return comprimento + comprimentoFracao + (passos * comprimentoFracao);
	}

	boolean podeCaminharNoPiso() {
		return passos < TOTAL_PASSOS_PISO;
	}

	void caminhar() {
		if (podeCaminharNoPiso()) {
			passos++;
		}
	}

	void ocupar(Bloco bloco) {
		if (bloco == null) {
			return;
		}
		if (piso != null) {
			piso.pivo = null;
		}
		bloco.pivo = this;
		piso = bloco;
		passos = 0;
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
		int met = lado / 2;
		int m = met / 2;
		g2.fillOval(x + m, y + m, met, met);
		sentido(x + m, y + m, met, g2);
	}

	private void sentido(int x, int y, int lado, Graphics2D g2) {
		g2.setColor(Color.BLACK);
		int met = lado / 2;
		int m = met / 2;
		if (sentido == Sentido.ACIMA) {
			g2.fillRect(x + m, y, met, met);
		} else if (sentido == Sentido.DIREITA) {
			g2.fillRect(x + met, y + m, met, met);
		} else if (sentido == Sentido.ABAIXO) {
			g2.fillRect(x + m, y + met, met, met);
		} else if (sentido == Sentido.ESQUERDA) {
			g2.fillRect(x, y + m, met, met);
		}
	}

	public String toString() {
		return "Pivo[sentido=" + sentido + ", passos=" + passos + "]";
	}
}