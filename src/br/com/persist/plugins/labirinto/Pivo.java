package br.com.persist.plugins.labirinto;

import java.awt.Color;
import java.awt.Graphics2D;

public class Pivo {
	public static final int TOTAL_PASSOS_PISO = 5;
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
		lado /= 2;
		int m = lado / 2;
		g2.fillOval(x + m, y + m, lado, lado);
	}

	public String toString() {
		return "Pivo[sentido=" + sentido + ", passos=" + passos + "]";
	}
}