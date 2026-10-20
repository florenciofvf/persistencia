package br.com.persist.plugins.labirinto;

class Controle {
	final Face front = new Face();
	final Face back = new Face();

	void calcularLadoFrontE() {
		front.lado = LabirintoUtil.altura(front.ponto1, front.ponto4);
	}

	void calcularLadoFrontD() {
		front.lado = LabirintoUtil.altura(front.ponto2, front.ponto3);
	}

	void calcularLadoBackE() {
		back.lado = LabirintoUtil.altura(back.ponto1, back.ponto4);
	}
}