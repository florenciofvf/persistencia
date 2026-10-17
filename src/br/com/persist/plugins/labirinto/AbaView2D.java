package br.com.persist.plugins.labirinto;

import static br.com.persist.componente.BarraButtonEnum.BAIXAR;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.io.File;

import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.marca.XML;

public class AbaView2D extends Panel {
	private PainelDesenho painelDesenho = new PainelDesenho();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private final File file;

	public AbaView2D(File file) {
		this.file = file;
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, painelDesenho);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		private Toolbar() {
			super.ini(new Nil(), BAIXAR);
		}

		@Override
		protected void baixar() {
			try {
				LabirintoXMLHandler handler = new LabirintoXMLHandler();
				XML.processar(file, handler);
				painelDesenho.matriz = handler.matriz;
				painelDesenho.repaint();
			} catch (Exception ex) {
				Util.stackTraceAndMessage(LabirintoConstantes.PAINEL_LABIRINTO, ex, AbaView2D.this);
			}
		}
	}

	class PainelDesenho extends Panel {
		private static final long serialVersionUID = 1L;
		private transient Matriz matriz;

		public PainelDesenho() {
			addMouseMotionListener(new OuvinteMouseMotion());
			addMouseListener(new OuvinteMouse());
		}

		private class OuvinteMouseMotion extends MouseMotionAdapter {
		}

		private class OuvinteMouse extends MouseAdapter {
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			if (matriz != null) {
				matriz.desenhar2D(g2);
			}
		}
	}
}