package br.com.persist.plugins.mapa;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Arrays;

import br.com.persist.componente.Panel;
import br.com.persist.plugins.mapa.forma.Associacao;
import br.com.persist.plugins.mapa.forma.Forma;

public class AbaView extends Panel {
	private static final long serialVersionUID = 1L;
	public boolean desenharAssociacoes = true;
	public boolean desenharGrade2 = true;
	public boolean desenharGrade = true;
	private Associacao[] associacoes;
	private boolean rotacionado;
	public boolean rotacionar;
	private boolean montando;
	Forma[] formas;

	public AbaView() {
		// addMouseMotionListener(new OuvinteMouseMotion());
		// addMouseListener(new OuvinteMouse());
		associacoes = new Associacao[0];
		formas = new Forma[0];
		// new Thread(this).start();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		int largura = getWidth();
		int altura = getHeight();
		int metadeAltura = altura / 2;
		if (montando) {
			g.drawString("Montando objetos...", largura / 2, metadeAltura);
			return;
		}
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (rotacionado) {
			Arrays.sort(formas);
			rotacionado = false;
		}
		g.setColor(Color.LIGHT_GRAY);
		if (desenharGrade) {
			paintGrade(g, largura, altura, metadeAltura);
		}
		if (desenharGrade2) {
			paintGrade2(g, largura, altura);
		}
		prePaintForma(largura, altura);
		if (desenharAssociacoes) {
			paintAssociacoes(g2);
		}
		paintFormaCentro(g2);
	}

	private void paintGrade(Graphics g, int largura, int altura, int metadeAltura) {
		int j = largura / 10;
		int metadeLargura = largura / 2 - 100;
		for (int x = metadeLargura, y = 0; x < metadeLargura + 100; x += 3, y += j) {
			g.drawLine(x, 0, x, metadeAltura);
			g.drawLine(x, metadeAltura, y, altura);
		}
	}

	private void paintGrade2(Graphics g, int largura, int altura) {
		for (int x = 0; x < largura; x += 10) {
			g.drawLine(x, 0, x, altura);
		}
		for (int y = 0; y < altura; y += 10) {
			g.drawLine(0, y, largura, y);
		}
	}

	private void prePaintForma(int largura, int altura) {
		int xOrigem = largura / 2;
		int yOrigem = altura / 2;
		for (int i = 0; i < formas.length; i++) {
			if (formas[i] != null) {
				formas[i].xOrigem = xOrigem;
				formas[i].yOrigem = yOrigem;
			}
		}
	}

	private void paintAssociacoes(Graphics2D g2) {
		for (int i = 0; i < associacoes.length; i++) {
			if (associacoes[i] != null) {
				associacoes[i].desenhar(g2);
			}
		}
	}

	private void paintFormaCentro(Graphics2D g2) {
		if (Forma.DESENHAR_OBJETO_CENTRO) {
			for (int i = 0; i < formas.length; i++) {
				if (formas[i] != null) {
					formas[i].desenhar(g2);
				}
			}
		} else {
			for (int i = 0; i < formas.length; i++) {
				if (formas[i] != null && formas[i].centro) {
					continue;
				}
				formas[i].desenhar(g2);
			}
		}
	}
}