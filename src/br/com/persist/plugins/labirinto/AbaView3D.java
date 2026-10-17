package br.com.persist.plugins.labirinto;

import static br.com.persist.componente.BarraButtonEnum.BAIXAR;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;

public class AbaView3D extends Panel {
	private PainelDesenho painelDesenho = new PainelDesenho();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private final AbaView2D abaView2D;

	public AbaView3D(AbaView2D abaView2D) {
		this.abaView2D = abaView2D;
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
			Matriz matriz = abaView2D.getMatriz();
			if (matriz == null) {
				Util.mensagem(AbaView3D.this, LabirintoMensagens.getString("msg.favor_construir_matriz"));
				return;
			}
		}
	}

	class PainelDesenho extends Panel {
		private static final long serialVersionUID = 1L;

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

		}
	}
}