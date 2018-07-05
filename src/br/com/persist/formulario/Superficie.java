package br.com.persist.formulario;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JDesktopPane;

import br.com.persist.Objeto;
import br.com.persist.Relacao;

public class Superficie extends JDesktopPane {
	private static final long serialVersionUID = 1L;
	private final Formulario formulario;
	private Objeto selecionado;
	private Relacao[] relacoes;
	private Objeto[] objetos;

	public Superficie(Formulario formulario) {
		this.formulario = formulario;
		limpar();

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (selecionado != null) {
					selecionado.x = e.getX() - Objeto.diametro / 2;
					selecionado.y = e.getY() - Objeto.diametro / 2;
					repaint();
				}
			}
		});

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				selecionado = null;
				int x = e.getX();
				int y = e.getY();

				for (Objeto objeto : objetos) {
					objeto.setSelecionado(false);
				}

				for (Objeto objeto : objetos) {
					if (objeto.contem(x, y)) {
						objeto.setSelecionado(true);
						selecionado = objeto;
						break;
					}
				}

				repaint();
			}
		});
	}

	public Formulario getFormulario() {
		return formulario;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for (Relacao relacao : relacoes) {
			relacao.desenhar(g2);
		}

		for (Objeto objeto : objetos) {
			objeto.desenhar(this, g2);
		}
	}

	public void addObjeto(Objeto objeto) {
		if (objeto == null) {
			return;
		}

		Objeto[] bkp = objetos;
		objetos = new Objeto[bkp.length + 1];
		System.arraycopy(bkp, 0, objetos, 0, bkp.length);
		objetos[bkp.length] = objeto;
	}

	public void addRelacao(Relacao relacao) {
		if (relacao == null) {
			return;
		}

		Relacao[] bkp = relacoes;
		relacoes = new Relacao[bkp.length + 1];
		System.arraycopy(bkp, 0, relacoes, 0, bkp.length);
		relacoes[bkp.length] = relacao;
	}

	public void limpar() {
		relacoes = new Relacao[0];
		objetos = new Objeto[0];
	}
}