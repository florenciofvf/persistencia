package br.com.persist.plugins.labirinto;

import static br.com.persist.componente.BarraButtonEnum.BAIXAR;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;

public class AbaView3D extends Panel {
	private transient List<Bloco> blocosEsquerdo = new ArrayList<>();
	private transient List<Bloco> blocosDireito = new ArrayList<>();
	private PainelDesenho painelDesenho = new PainelDesenho();
	private final transient Ponto origem = new Ponto();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private transient Bloco blocoFrente;
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
			selecionarBlocos(matriz);
		}

		private void selecionarBlocos(Matriz matriz) {
			Pivo pivo = matriz.pivo;
			List<Bloco> blocosNaFrente = matriz.getBlocosNaFrente(pivo);
			blocoFrente = LabirintoUtil.primeiro(blocosNaFrente);
			blocosEsquerdo = matriz.getBlocosLadoEsquerdo(pivo);
			blocosDireito = matriz.getBlocosLadoDireito(pivo);
			painelDesenho.configurarFaces();
			painelDesenho.repaint();
		}
	}

	class PainelDesenho extends Panel {
		private static final long serialVersionUID = 1L;

		void configurarFaces() {
			int metadeLargura = getWidth() / 2;
			int metadeAltura = getHeight() / 2;
			origem.x = metadeLargura;
			origem.y = metadeAltura;
			if (blocoFrente != null) {
				blocoFrente.calcular3DFrente(origem, metadeAltura);
			}
			for (Bloco item : blocosEsquerdo) {
				item.calcular3DEsquerdo(origem, metadeAltura);
			}
			for (Bloco item : blocosDireito) {
				item.calcular3DDireito(origem, metadeAltura);
			}
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			if (blocoFrente != null) {
				blocoFrente.desenhar3DFrente(g2);
			}
			for (int i = blocosEsquerdo.size() - 1; i >= 0; i--) {
				Bloco item = blocosEsquerdo.get(i);
				item.desenhar3DEsquerdo(g2);
			}
			for (int i = blocosDireito.size() - 1; i >= 0; i--) {
				Bloco item = blocosDireito.get(i);
				item.desenhar3DDireito(g2);
			}
		}
	}
}