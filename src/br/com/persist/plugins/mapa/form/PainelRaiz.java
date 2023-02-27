package br.com.persist.plugins.mapa.form;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;

import br.com.persist.plugins.mapa.Atributo;
import br.com.persist.plugins.mapa.Container;
import br.com.persist.plugins.mapa.Objeto;
import br.com.persist.plugins.mapa.config.Config;
import br.com.persist.plugins.mapa.forma.Associacao;
import br.com.persist.plugins.mapa.forma.Circulo;
import br.com.persist.plugins.mapa.forma.Forma;
import br.com.persist.plugins.mapa.organiza.Organizador;
import br.com.persist.plugins.mapa.organiza.OrganizadorRandomico;

public class PainelRaiz extends JPanel implements Runnable {
	public Organizador organizadorPadrao = new OrganizadorRandomico();
	private static final long serialVersionUID = 1L;
	private final Evento evento = new Evento();
	public boolean desenharAssociacoes = true;
	public boolean desenharGrade = true;
	private Associacao[] associacoes;
	public boolean desenharGrade2;
	private Objeto objetoMontado;
	private boolean rotacionado;
	public boolean rotacionar;
	private boolean continua;
	int xUltimoClick;
	int yUltimoClick;
	Forma[] formas;

	public PainelRaiz() {
		addMouseMotionListener(new OuvinteMouseMotion());
		addMouseListener(new OuvinteMouse());
		associacoes = new Associacao[0];
		formas = new Forma[0];
		new Thread(this).start();
	}

	public void run() {
		while (true) {
			if (continua & rotacionar) {
				for (int i = 0; i < formas.length; i++) {
					if (formas[i] != null && formas[i].vetor != null) {
						formas[i].vetor.rotacaoY(1);
					}
				}
				rotacionado = true;
				repaint();
			}
			try {
				Thread.sleep(Config.INTERVALO_ROTACAO);
			} catch (Exception e) {
			}
		}
	}

	public void continuar() {
		continua = true;
	}

	public void parar() {
		continua = false;
	}

	private boolean montando;

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
			int j = largura / 10;
			int metadeLargura = largura / 2 - 100;
			for (int x = metadeLargura, y = 0; x < metadeLargura + 100; x += 3, y += j) {
				g.drawLine(x, 0, x, metadeAltura);
				g.drawLine(x, metadeAltura, y, altura);
			}
		}

		if (desenharGrade2) {
			for (int x = 0; x < largura; x += 10) {
				g.drawLine(x, 0, x, altura);
			}
			for (int y = 0; y < altura; y += 10) {
				g.drawLine(0, y, largura, y);
			}
		}

		int xOrigem = largura / 2;
		int yOrigem = altura / 2;

		for (int i = 0; i < formas.length; i++) {
			if (formas[i] != null) {
				formas[i].xOrigem = xOrigem;
				formas[i].yOrigem = yOrigem;
			}
		}

		if (desenharAssociacoes) {
			for (int i = 0; i < associacoes.length; i++) {
				if (associacoes[i] != null) {
					associacoes[i].desenhar(g2);
				}
			}
		}

		if (Forma.desenharObjetoCentro) {
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

	public Forma getForma(Click e) {
		int x = e.getX();
		int y = e.getY();
		int xOrigem = getWidth() / 2;
		int yOrigem = getHeight() / 2;
		for (int i = formas.length - 1; i >= 0; i--) {
			if (formas[i] != null) {
				formas[i].xOrigem = xOrigem;
				formas[i].yOrigem = yOrigem;
				if (formas[i].contem(x, y)) {
					return formas[i];
				}
			}
		}
		return null;
	}

	private class T extends Thread {
		Evento.THREAD thread;

		T(Evento.THREAD thread) {
			this.thread = thread;
		}

		public void run() {
			Click click = thread.getClick();
			if (click == null) {
				return;
			}
			if (click.clicks > 1) {
				if (objetoMontado != null && objetoMontado.getPai() != null && formas != null) {
					Objeto objetoPai = (Objeto) objetoMontado.getPai();
					Forma formaCentro = null;
					for (int i = 0; i < formas.length; i++) {
						if (formas[i].centro) {
							formaCentro = formas[i];
						}
					}
					if (formaCentro != null) {
						formaCentro.xOrigem = getWidth() / 2;
						formaCentro.yOrigem = getHeight() / 2;
						if (formaCentro.objeto == objetoMontado && formaCentro.contem(click.getX(), click.getY())) {
							montar(objetoPai);
						}
					}
				}
			} else {
				Forma forma = getForma(click);
				if (forma != null) {
					montar(forma.objeto);
				}
			}
		}
	}

	private class OuvinteMouse extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			xUltimoClick = e.getX();
			yUltimoClick = e.getY();
			parar();
		}

		public void mouseReleased(MouseEvent e) {
			continuar();
		}

		public void mouseClicked(MouseEvent e) {
			evento.click(e);
			Evento.THREAD thread = evento.get();
			new T(thread).start();
		}
	}

	private class OuvinteMouseMotion extends MouseMotionAdapter {
		public void mouseDragged(MouseEvent e) {
			for (int i = 0; i < formas.length; i++) {
				if (formas[i] != null && formas[i].vetor != null) {
					formas[i].vetor.rotacaoX(yUltimoClick - e.getY());
					formas[i].vetor.rotacaoY(xUltimoClick - e.getX());
				}
			}
			rotacionado = true;
			xUltimoClick = e.getX();
			yUltimoClick = e.getY();
			repaint();
		}
	}

	private class Obj {
		Objeto obj;
		boolean ref;

		public Obj(Objeto o) {
			this(o, false);
		}

		public Obj(Objeto o, boolean r) {
			obj = o;
			ref = r;
		}
	}

	private Organizador getOrganizador(Objeto objeto) {
		Organizador organizador = null;
		Container container = objeto;
		while (container.getPai() != null) {
			organizador = container.getPai().getOrganizador();
			if (organizador != null) {
				break;
			}
			container = container.getPai();
		}
		return organizador;
	}

	public void montar(Objeto objeto) {
		montando = true;
		Organizador organizador = null;

		if (objeto.getOrganizador() != null) {
			organizador = objeto.getOrganizador();
		} else {
			Atributo atributo = objeto.getAtributo("organizador");
			if (atributo != null) {
				organizador = Formulario.getOrganizador(atributo.getValor());
				if (organizador != null) {
					atributo = objeto.getAtributo("organizadorParametros");
					if (atributo != null) {
						organizador.parametros(atributo.getValor());
					}
				}
			}
			if (organizador == null) {
				Organizador org = getOrganizador(objeto);
				if (org != null) {
					organizador = org;
				} else {
					organizador = Formulario.getOrganizador("randomico");
				}
			}
			objeto.setOrganizador(organizador);
		}

		organizador.reiniciar();

		List<Obj> listagem = new ArrayList<Obj>();
		objetoMontado = objeto;

		if (objeto.getPai() == null) {
			for (Container obj : objeto.getFilhos()) {
				Objeto o = (Objeto) obj;
				if (o.getMenu() != null) {
					listagem.add(new Obj(o));
				}
			}
		} else {
			for (Container obj : objeto.getFilhos()) {
				Objeto o = (Objeto) obj;
				listagem.add(new Obj(o));
			}
			for (Container obj : objeto.getReferencias()) {
				Objeto o = (Objeto) obj;
				listagem.add(new Obj(o, true));
			}
		}

		final int UM = 1;
		Circulo c = new Circulo(0, 0, 0, Config.DIAMETRO_OBJETO_CENTRO, objeto);
		formas = new Forma[listagem.size() + UM];
		c.centro = true;
		formas[0] = c;

		for (int i = 0; i < listagem.size(); i++) {
			Obj obj = listagem.get(i);
			formas[i + UM] = criar(obj, organizador);
		}

		List<Associacao> lista = new ArrayList<Associacao>();

		for (int i = 1; i < formas.length; i++) {
			Forma origem = formas[i];
			List<Container> refs = origem.objeto.getReferencias();
			List<Forma> list = getFormasReferenciadas(refs, formas);
			for (Forma f : list) {
				Associacao a = new Associacao(origem, f);
				lista.add(a);
			}
		}

		List<Container> refs = objeto.getReferencias();
		for (Container r : refs) {
			Forma forma = null;
			for (int i = 0; i < formas.length; i++) {
				if (formas[i].objeto == r) {
					forma = formas[i];
				}
			}
			if (forma == null) {
				break;
			}
			for (Forma f : formas) {
				if (r.equals(f.objeto)) {
					if (forma != f) {
						Associacao a = new Associacao(forma, f);
						lista.add(a);
					}
				}
			}
		}

		associacoes = lista.toArray(new Associacao[0]);

		montando = false;
		repaint();
	}

	private List<Forma> getFormasReferenciadas(List<Container> refs, Forma[] formas) {
		List<Forma> resp = new ArrayList<Forma>();
		for (Container c : refs) {
			for (Forma f : formas) {
				if (c.equals(f.objeto)) {
					resp.add(f);
				}
			}
		}
		return resp;
	}

	private Circulo criar(Obj obj, Organizador organizador) {
		Circulo c = new Circulo(Config.DISTANCIA_CENTRO, 0, 0, Config.DIAMETRO_OBJETO, obj.obj);
		c.setCorGradienteRef(obj.ref);
		organizador.organizar(c);
		return c;
	}

	public void localizar(String s) {
		if (s == null) {
			return;
		}
		for (Forma f : formas) {
			Objeto o = f.objeto;
			if (o.getNome().equalsIgnoreCase(s)) {
				f.setCorGradiente1(Color.GREEN);
			}
		}
		repaint();
	}
}