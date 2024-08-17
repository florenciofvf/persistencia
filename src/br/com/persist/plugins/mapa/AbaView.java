package br.com.persist.plugins.mapa;

import static br.com.persist.componente.BarraButtonEnum.BAIXAR;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.ButtonPopup;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.componente.TextField;
import br.com.persist.plugins.mapa.organiza.Organizador;

public class AbaView extends Panel {
	private static final long serialVersionUID = 1L;
	private PanelView panelView = new PanelView();
	private PanelMenu panelMenu = new PanelMenu();
	private final Toolbar toolbar = new Toolbar();
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

	public void carregar(File file) {
		panelMenu.removeAll();
		panelView.reiniciar();
		try {
			mapaHandler = MontaObjeto.montarObjeto(file);
			configuracoes();
			for (Objeto obj : mapaHandler.getObjetos()) {
				obj.resolverReferencias(mapaHandler);
				if (obj.getMenu() != null) {
					panelMenu.add(new LabelMenu(obj.getMenu(), obj, panelView));
				}
			}
		} catch (Exception ex) {
			Util.stackTraceAndMessage(MapaConstantes.PAINEL_MAPA, ex, AbaView.this);
		}
		SwingUtilities.updateComponentTreeUI(this);
	}

	private void configuracoes() {
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

	private class Toolbar extends BarraButton implements ActionListener {
		private final TextField txtPesquisa = new TextField(35);
		private static final long serialVersionUID = 1L;

		private Toolbar() {
			super.ini(new Nil(), BAIXAR);
			add(true, new ButtonStatus());
			txtPesquisa.setToolTipText(Mensagens.getString("label.pesquisar"));
			txtPesquisa.addActionListener(this);
			add(txtPesquisa);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				panelView.localizar(txtPesquisa.getText());
			}
		}

		@Override
		protected void baixar() {
			carregar(file);
		}

		private class ButtonStatus extends ButtonPopup {
			private Action desenharObjetoCentroAcao = acaoMenu("label.desenhar_objeto_centro");
			private Action desenharAssociacaoAcao = acaoMenu("label.desenhar_associacao");
			private Action desenharAtributoAcao = acaoMenu("label.desenhar_atributo");
			private Action desenharGrade2Acao = acaoMenu("label.desenhar_grade2");
			private Action desenharGradeAcao = acaoMenu("label.desenhar_grade");
			private Action velocidadeAcao = acaoMenu("label.velocidade");
			private Action girarAcao = acaoMenu("label.girar");
			private static final long serialVersionUID = 1L;

			private ButtonStatus() {
				super("label.status", Icones.TAG2);
				addItem(new JCheckBoxMenuItem(desenharObjetoCentroAcao));
				addItem(new JCheckBoxMenuItem(desenharAssociacaoAcao));
				addItem(new JCheckBoxMenuItem(desenharAtributoAcao));
				addItem(new JCheckBoxMenuItem(desenharGrade2Acao));
				addItem(new JCheckBoxMenuItem(desenharGradeAcao));
				addItem(new JCheckBoxMenuItem(girarAcao));
				addMenuItem(velocidadeAcao);
				eventos();
			}

			Action acaoMenu(String chave, Icon icon) {
				return Action.acaoMenu(MapaMensagens.getString(chave), icon);
			}

			Action acaoMenu(String chave) {
				return acaoMenu(chave, null);
			}

			private void eventos() {
				desenharObjetoCentroAcao.setActionListener(e -> {
					Config.setDesenharObjetoCentro(isSelected(e));
					panelView.repaint();
				});
				desenharAssociacaoAcao.setActionListener(e -> {
					panelView.desenharAssociacoes = isSelected(e);
					panelView.repaint();
				});
				desenharAtributoAcao.setActionListener(e -> {
					Config.setDesenharAtributos(isSelected(e));
					panelView.repaint();
				});
				desenharGradeAcao.setActionListener(e -> {
					panelView.desenharGrade = isSelected(e);
					panelView.repaint();
				});
				desenharGrade2Acao.setActionListener(e -> {
					panelView.desenharGrade2 = isSelected(e);
					panelView.repaint();
				});
				girarAcao.setActionListener(e -> panelView.rotacionar(isSelected(e)));
				velocidadeAcao.setActionListener(e -> velocidade());
			}

			private void velocidade() {
				String s = JOptionPane.showInputDialog(AbaView.this, MapaMensagens.getString("label.velocidade"),
						"" + Config.getIntervaloRotacao());
				Config.setIntervaloRotacao(Util.getInt(s, Config.getIntervaloRotacao()));
			}

			private boolean isSelected(ActionEvent e) {
				return ((JCheckBoxMenuItem) e.getSource()).isSelected();
			}
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

	class PanelView extends Panel implements Runnable {
		private transient Logger log = Logger.getGlobal();
		private static final long serialVersionUID = 1L;
		private transient Associacao[] associacoes;
		private boolean desenharAssociacoes;
		private transient Objeto[] objetos;
		private transient Thread thread;
		private boolean desenharGrade2;
		private boolean desenharGrade;
		int xUltimoClick;
		int yUltimoClick;

		public PanelView() {
			addMouseMotionListener(new OuvinteMouseMotion());
			addMouseListener(new OuvinteMouse());
			reiniciar();
		}

		public void reiniciar() {
			associacoes = new Associacao[0];
			objetos = new Objeto[0];
		}

		private class OuvinteMouseMotion extends MouseMotionAdapter {
			@Override
			public void mouseDragged(MouseEvent e) {
				for (Objeto objeto : objetos) {
					objeto.vetor.rotacaoX(yUltimoClick - e.getY());
					objeto.vetor.rotacaoY(xUltimoClick - e.getX());
				}
				xUltimoClick = e.getX();
				yUltimoClick = e.getY();
				ordenarObjetos();
				repaint();
			}
		}

		private class OuvinteMouse extends MouseAdapter {
			@Override
			public void mousePressed(MouseEvent e) {
				xUltimoClick = e.getX();
				yUltimoClick = e.getY();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				Objeto objeto = getObjeto(e);
				if (objeto != null) {
					montar(objeto);
				}
			}

			private Objeto getObjeto(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				int xOrigem = getWidth() / 2;
				int yOrigem = getHeight() / 2;
				for (int i = objetos.length - 1; i >= 0; i--) {
					objetos[i].xOrigem = xOrigem;
					objetos[i].yOrigem = yOrigem;
					if (objetos[i].contem(x, y)) {
						return objetos[i];
					}
				}
				return null;
			}
		}

		public synchronized void rotacionar(boolean b) {
			if (b) {
				iniciar();
			} else {
				parar();
			}
		}

		private void iniciar() {
			if (thread == null) {
				thread = new Thread(this);
				thread.start();
			}
		}

		private void parar() {
			if (thread != null) {
				thread.interrupt();
				thread = null;
			}
		}

		public void run() {
			while (thread != null && !Thread.currentThread().isInterrupted()) {
				for (Objeto objeto : objetos) {
					objeto.vetor.rotacaoY(1);
				}
				ordenarObjetos();
				repaint();
				try {
					Thread.sleep(Config.getIntervaloRotacao());
				} catch (InterruptedException ex) {
					log.log(Level.SEVERE, ex.getMessage());
					Thread.currentThread().interrupt();
				}
			}
		}

		@Override
		public synchronized void paint(Graphics g) {
			super.paint(g);
			int largura = getWidth();
			int altura = getHeight();
			int metadeAltura = altura / 2;
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
			if (Config.isDesenharObjetoCentro()) {
				for (Objeto objeto : objetos) {
					objeto.xOrigem = xOrigem;
					objeto.yOrigem = yOrigem;
					objeto.desenhar(g2);
				}
			} else {
				for (Objeto objeto : objetos) {
					objeto.xOrigem = xOrigem;
					objeto.yOrigem = yOrigem;
					if (objeto.centro) {
						continue;
					}
					objeto.desenhar(g2);
				}
			}
		}

		private void ordenarObjetos() {
			for (int i = 0; i < objetos.length; i++) {
				for (int j = 0; j < objetos.length; j++) {
					if (objetos[i].vetor.z < objetos[j].vetor.z) {
						Objeto objI = objetos[i];
						Objeto objJ = objetos[j];
						objetos[i] = objJ;
						objetos[j] = objI;
					}
				}
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
			if (s != null) {
				for (Objeto objeto : objetos) {
					if (objeto.nome.equalsIgnoreCase(s)) {
						objeto.setCorGradiente1(Color.GREEN);
					}
				}
				repaint();
			}
		}

		private synchronized void montar(Objeto objeto) {
			configOrganizador(objeto);

			objeto.preDesenhar(0, 0, 0, Config.getDiametroObjetoCentro());
			List<Objeto> listaObjeto = new ArrayList<>();
			listaObjeto.add(objeto);

			for (Objeto obj : objeto.getFilhos()) {
				obj.preDesenhar(Config.getDistanciaCentro(), 0, 0, Config.getDiametroObjeto());
				objeto.getOrganizador().organizar(obj);
				listaObjeto.add(obj);
			}

			List<Associacao> listaAssociacao = new ArrayList<>();
			listaAssociacao.addAll(objeto.criarAssociacoes(objeto.getFilhos()));
			for (Objeto obj : objeto.getFilhos()) {
				listaAssociacao.addAll(obj.criarAssociacoes(objeto.getFilhos()));
			}

			associacoes = listaAssociacao.toArray(new Associacao[0]);
			objetos = listaObjeto.toArray(new Objeto[0]);
			for (Objeto obj : objetos) {
				obj.centro = false;
			}
			objeto.centro = true;
			repaint();
		}

		private void configOrganizador(Objeto objeto) {
			if (objeto.getOrganizador() != null) {
				objeto.getOrganizador().reiniciar();
				return;
			}
			Organizador organizador = null;
			Atributo atributo = objeto.getAtributo("organizador");
			if (atributo != null) {
				organizador = Organizador.get(atributo.getValor());
				if (organizador != null) {
					atributo = objeto.getAtributo("organizadorParametros");
					if (atributo != null) {
						try {
							organizador.parametros(atributo.getValor());
						} catch (MapaException | ArgumentoException ex) {
							Util.mensagem(AbaView.this, ex.getMessage());
						}
					}
				}
			}
			if (organizador == null) {
				organizador = Organizador.get(objeto);
			}
			objeto.setOrganizador(organizador);
			organizador.reiniciar();
		}
	}

	class PanelMenu extends Panel {
		private static final long serialVersionUID = 1L;

		PanelMenu() {
			super(new GridLayout(0, 1));
		}
	}
}