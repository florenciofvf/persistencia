package br.com.persist.plugins.mapa;

import static br.com.persist.componente.BarraButtonEnum.BAIXAR;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;

import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.componente.TextField;
import br.com.persist.plugins.mapa.organiza.Organizador;
import br.com.persist.plugins.mapa.organiza.OrganizadorBola;
import br.com.persist.plugins.mapa.organiza.OrganizadorCircular;
import br.com.persist.plugins.mapa.organiza.OrganizadorRandomico;
import br.com.persist.plugins.mapa.organiza.OrganizadorSequencia;

public class AbaView extends Panel {
	private transient Organizador organizadorPadrao = new OrganizadorRandomico();
	private static Map<String, Organizador> organizadores = new HashMap<>();
	private final ToolbarParametro toolbar = new ToolbarParametro();
	private static final long serialVersionUID = 1L;
	private PanelView panelView = new PanelView();
	private PanelMenu panelMenu = new PanelMenu();
	private transient MapaHandler mapaHandler;
	private final File file;

	public AbaView(File file) {
		this.file = file;
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, panelView);
		add(BorderLayout.EAST, panelMenu);
	}

	static {
		organizadores.put("sequencia", new OrganizadorSequencia());
		organizadores.put("randomico", new OrganizadorRandomico());
		organizadores.put("circular", new OrganizadorCircular());
		organizadores.put("bola", new OrganizadorBola());
	}

	Organizador getOrganizador(Objeto objeto) {
		return objeto.getOrganizador() != null ? objeto.getOrganizador() : organizadorPadrao;
	}

	public static Organizador getOrganizador(String nome) {
		if (nome != null) {
			nome = nome.trim().toLowerCase();
		}
		return organizadores.get(nome);
	}

	public void carregar(File file) {
		panelMenu.removeAll();
		panelView.reiniciar();
		try {
			mapaHandler = MontaObjeto.montarObjeto(file);
			configRaiz();
			for (Objeto obj : mapaHandler.getObjetos()) {
				obj.resolverReferencias(mapaHandler);
				if (obj.getMenu() != null) {
					panelMenu.add(new LabelMenu(obj.getMenu(), obj, panelView));
				}
			}
		} catch (Exception ex) {
			Util.stackTraceAndMessage(MapaConstantes.PAINEL_MAPA, ex, AbaView.this);
		}
	}

	private class ToolbarParametro extends BarraButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private final TextField txtPesquisa = new TextField(35);

		private ToolbarParametro() {
			super.ini(new Nil(), BAIXAR);
			txtPesquisa.setToolTipText(Mensagens.getString("label.pesquisar"));
			txtPesquisa.addActionListener(this);
			add(txtPesquisa);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.estaVazio(txtPesquisa.getText())) {
				panelView.localizar(txtPesquisa.getText());
			}
		}

		@Override
		protected void baixar() {
			carregar(file);
		}
	}

	class LabelMenu extends JLabel {
		private static final long serialVersionUID = 1L;
		private static final String ESPACO = "     ";

		public LabelMenu(String rotulo, final Objeto objeto, final PanelView panelView) {
			super(ESPACO + rotulo + ESPACO);
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseExited(MouseEvent e) {
					setForeground(Color.BLACK);
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					setForeground(Color.BLUE);
				}

				@Override
				public void mouseClicked(MouseEvent e) {
					panelView.montar(objeto);
				}
			});
		}
	}

	private void configRaiz() {
		Objeto raiz = mapaHandler.getRaiz();
		if (raiz != null) {
			Atributo atributo = raiz.getAtributo("diametroObjeto");
			if (atributo != null) {
				Config.setDiametroObjeto(atributo.getValorInt());
			}
			atributo = raiz.getAtributo("diametroObjetoCentro");
			if (atributo != null) {
				Config.setDiametroObjetoCentro(atributo.getValorInt());
			}
			atributo = raiz.getAtributo("distanciaCentro");
			if (atributo != null) {
				Config.setDistanciaCentro(atributo.getValorInt());
			}
			atributo = raiz.getAtributo("intervaloRotacao");
			if (atributo != null) {
				Config.setIntervaloRotacao(atributo.getValorInt());
			}
		}
	}

	class PanelView extends Panel implements Runnable {
		private static final long serialVersionUID = 1L;
		private transient Logger log = Logger.getGlobal();
		private transient Evento evento = new Evento();
		private transient Associacao[] associacoes;
		private boolean desenharAssociacoes = true;
		private boolean desenharGrade2 = true;
		private boolean desenharGrade = true;
		private boolean rotacionado;
		private boolean rotacionar;
		private boolean montando;
		private boolean continua;
		transient Forma[] formas;
		int xUltimoClick;
		int yUltimoClick;

		public PanelView() {
			addMouseMotionListener(new OuvinteMouseMotion());
			addMouseListener(new OuvinteMouse());
			// new Thread(this).start();
			reiniciar();
		}

		public void reiniciar() {
			associacoes = new Associacao[0];
			formas = new Forma[0];
		}

		private class T extends Thread {
			Evento.THREAD thread;

			T(Evento.THREAD thread) {
				this.thread = thread;
			}

			@Override
			public void run() {
				Click click = thread.getClick();
				if (click == null) {
					return;
				}
				Forma forma = getForma(click);
				if (forma != null) {
					montar(forma.objeto);
				}
			}
		}

		private class OuvinteMouseMotion extends MouseMotionAdapter {
			@Override
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

		private class OuvinteMouse extends MouseAdapter {
			@Override
			public void mousePressed(MouseEvent e) {
				xUltimoClick = e.getX();
				yUltimoClick = e.getY();
				parar();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				continuar();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				evento.click(e);
				Evento.THREAD thread = evento.get();
				new T(thread).start();
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

		public void run() {
			while (true) {
				if (continua && rotacionar) {
					for (int i = 0; i < formas.length; i++) {
						if (formas[i] != null && formas[i].vetor != null) {
							formas[i].vetor.rotacaoY(1);
						}
					}
					rotacionado = true;
					repaint();
					break;
				}
				try {
					Thread.sleep(Config.getIntervaloRotacao());
				} catch (Exception ex) {
					log.log(Level.SEVERE, ex.getMessage());
				}
			}
		}

		public void continuar() {
			continua = true;
		}

		public void parar() {
			continua = false;
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
			if (desenharAssociacoes) {
				for (Associacao associacao : associacoes) {
					associacao.desenhar(g2);
				}
			}
			int xOrigem = largura / 2;
			int yOrigem = altura / 2;
			for (Forma forma : formas) {
				forma.xOrigem = xOrigem;
				forma.yOrigem = yOrigem;
				forma.desenhar(g2);
			}
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

		public void localizar(String s) {
			if (s == null) {
				return;
			}
			for (Forma f : formas) {
				Objeto o = f.getObjeto();
				if (o.getNome().equalsIgnoreCase(s)) {
					f.setCorGradiente1(Color.GREEN);
				}
			}
			repaint();
		}

		private Circulo criar(Objeto objeto, Organizador organizador) {
			Circulo c = new Circulo(Config.getDistanciaCentro(), 0, 0, Config.getDiametroObjeto(), objeto);
			// c.setCorGradienteRef(obj.ref);
			organizador.organizar(c);
			return c;
		}

		private Organizador configOrganizador(Objeto objeto) {
			if (objeto.getOrganizador() != null) {
				return objeto.getOrganizador();
			}
			Organizador organizador = null;
			Atributo atributo = objeto.getAtributo("organizador");
			if (atributo != null) {
				organizador = getOrganizador(atributo.getValor());
				if (organizador != null) {
					atributo = objeto.getAtributo("organizadorParametros");
					if (atributo != null) {
						organizador.parametros(atributo.getValor());
					}
				}
			}
			if (organizador == null) {
				organizador = getOrganizador(objeto);
			}
			objeto.setOrganizador(organizador);
			return organizador;
		}

		public void montar(Objeto objeto) {
			montando = true;
			Organizador organizador = configOrganizador(objeto);
			organizador.reiniciar();

			formas = new Forma[objeto.getQtdFilhos() + 1];
			Circulo c = new Circulo(0, 0, 0, Config.getDiametroObjetoCentro(), objeto);
			c.centro = true;
			formas[0] = c;

			int i = 0;
			for (Objeto obj : objeto.getFilhos()) {
				formas[++i] = criar(obj, organizador);
			}

			List<Associacao> lista = new ArrayList<>();
			lista.addAll(objeto.criarAssociacoes(objeto.getFilhos()));
			for (Objeto obj : objeto.getFilhos()) {
				lista.addAll(obj.criarAssociacoes(objeto.getFilhos()));
			}

			associacoes = lista.toArray(new Associacao[0]);
			montando = false;
			repaint();
		}
	}

	class PanelMenu extends Panel {
		private static final long serialVersionUID = 1L;
	}
}