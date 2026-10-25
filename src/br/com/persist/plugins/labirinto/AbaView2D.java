package br.com.persist.plugins.labirinto;

import static br.com.persist.componente.BarraButtonEnum.BAIXAR;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import br.com.persist.assistencia.Icone;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.marca.XML;

public class AbaView2D extends Panel implements FormularioLabirintoListener {
	public static final String MSG_FAVOR_CONSTRUIR_MATRIZ = "msg.favor_construir_matriz";
	private PainelDesenho painelDesenho = new PainelDesenho();
	private FormularioLabirinto formularioLabirinto;
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private final AbaViewXML abaViewXML;
	private final File file;

	public AbaView2D(AbaViewXML abaViewXML, File file) {
		this.abaViewXML = abaViewXML;
		this.file = file;
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, painelDesenho);
	}

	static Action acaoIcone(String chave, Icone icone) {
		return Action.acaoIcon(LabirintoMensagens.getString(chave), icone);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action gerarConteudoAcao = acaoIcone("label.gerar_arquivo", Icones.ALINHA_ESQUERDO);
		private Action destacarAcao = acaoIcone("label.destacar", Icones.ARRASTAR2);

		private Toolbar() {
			super.ini(new Nil(), BAIXAR);
			addButton(gerarConteudoAcao);
			addButton(destacarAcao);
			gerarConteudoAcao.setActionListener(e -> gerarConteudo());
			destacarAcao.setActionListener(e -> destacar());
		}

		private void gerarConteudo() {
			if (painelDesenho.matriz == null) {
				Util.mensagem(AbaView2D.this, LabirintoMensagens.getString(MSG_FAVOR_CONSTRUIR_MATRIZ));
				return;
			}
			String string = painelDesenho.matriz.gerarStringFile();
			abaViewXML.textEditor.setText(string);
		}

		private void destacar() {
			Matriz matriz = getMatriz();
			if (matriz == null) {
				Util.mensagem(AbaView2D.this, LabirintoMensagens.getString(MSG_FAVOR_CONSTRUIR_MATRIZ));
				return;
			}
			formularioLabirinto = new FormularioLabirinto(matriz, getGraphicsConfiguration());
			formularioLabirinto.setListener(AbaView2D.this);
			formularioLabirinto.setVisible(true);
		}

		@Override
		protected void baixar() {
			try {
				LabirintoXMLHandler handler = new LabirintoXMLHandler();
				XML.processar(file, handler);
				painelDesenho.matriz = handler.getMatriz();
				painelDesenho.validarMatriz();
				painelDesenho.repaint();
			} catch (Exception ex) {
				Util.stackTraceAndMessage(LabirintoConstantes.PAINEL_LABIRINTO, ex, AbaView2D.this);
			}
		}
	}

	Matriz getMatriz() {
		return painelDesenho.matriz;
	}

	class GirarAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		private final boolean esquerda;

		public GirarAction(boolean esquerda) {
			this.esquerda = esquerda;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (formularioLabirinto != null && formularioLabirinto.isVisible()) {
				if (esquerda) {
					formularioLabirinto.girarEsquerdo();
				} else {
					formularioLabirinto.girarDireito();
				}
			}
		}
	}

	class CaminharAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (formularioLabirinto != null && formularioLabirinto.isVisible()) {
				formularioLabirinto.caminhar();
			}
		}
	}

	class PainelDesenho extends Panel {
		private static final long serialVersionUID = 1L;
		private transient Matriz matriz;

		public PainelDesenho() {
			inputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "para_esquerda");
			inputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "para_direita");
			inputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "caminhar");
			getActionMap().put("para_esquerda", new GirarAction(true));
			getActionMap().put("para_direita", new GirarAction(false));
			getActionMap().put("caminhar", new CaminharAction());
			addMouseListener(new OuvinteMouse());
		}

		void validarMatriz() {
			if (matriz.pivo == null) {
				matriz.criarPivo();
			}
		}

		private class OuvinteMouse extends MouseAdapter {
			@Override
			public void mousePressed(MouseEvent e) {
				if (matriz == null) {
					return;
				}
				matriz.ladoPadrao();
				int x = e.getX();
				int y = e.getY();
				if (e.isShiftDown()) {
					clickPivo(x, y);
				} else {
					clickBloco(x, y);
				}
			}

			private void clickBloco(int x, int y) {
				if (matriz.clickBloco(x, y)) {
					notificar();
					repaint();
				}
			}

			private void clickPivo(int x, int y) {
				if (matriz.clickPivo(x, y)) {
					notificar();
					repaint();
				}
			}

			private void notificar() {
				if (formularioLabirinto != null && formularioLabirinto.isVisible()) {
					formularioLabirinto.processar();
				}
			}
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			if (matriz != null) {
				matriz.ladoPadrao();
				matriz.desenhar2D(g2);
			}
		}
	}

	@Override
	public void fechado(FormularioLabirinto formulario) {
		this.formularioLabirinto = null;
	}

	@Override
	public void alterado(Matriz matriz, Pivo pivo) {
		painelDesenho.repaint();
	}
}