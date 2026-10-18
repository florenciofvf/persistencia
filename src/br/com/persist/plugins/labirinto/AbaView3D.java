package br.com.persist.plugins.labirinto;

import static br.com.persist.componente.BarraButtonEnum.BAIXAR;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import br.com.persist.assistencia.Icone;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;

public class AbaView3D extends Panel {
	private transient List<Bloco> blocosEsquerdo = new ArrayList<>();
	private transient List<Bloco> blocosDireito = new ArrayList<>();
	private transient List<Bloco> blocosFrente = new ArrayList<>();
	private PainelDesenho painelDesenho = new PainelDesenho();
	private final transient Ponto origem = new Ponto();
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

	static Action acaoIcone(String chave, Icone icone) {
		return Action.acaoIcon(LabirintoMensagens.getString(chave), icone);
	}

	private class Toolbar extends BarraButton {
		private Action esquerdaAcao = acaoIcone("label.virar_esquerda", Icones.ALINHA_ESQUERDO);
		private static final String MSG_FAVOR_CONSTRUIR_MATRIZ = "msg.favor_construir_matriz";
		private Action direitaAcao = acaoIcone("label.virar_direita", Icones.ALINHA_DIREITO);
		private Action updateAcao = Action.actionIconUpdate();
		private static final long serialVersionUID = 1L;

		private Toolbar() {
			super.ini(new Nil(), BAIXAR);
			addButton(updateAcao);
			addButton(esquerdaAcao);
			addButton(direitaAcao);
			updateAcao.setActionListener(e -> caminhar());
			esquerdaAcao.setActionListener(e -> girar(true));
			direitaAcao.setActionListener(e -> girar(false));
		}

		private void girar(boolean esquerda) {
			Matriz matriz = abaView2D.getMatriz();
			if (matriz == null) {
				Util.mensagem(AbaView3D.this, LabirintoMensagens.getString(MSG_FAVOR_CONSTRUIR_MATRIZ));
				return;
			}
			Pivo pivo = matriz.pivo;
			if (esquerda) {
				pivo.antiHorario();
			} else {
				pivo.horario();
			}
			selecionarBlocos(matriz);
		}

		private void caminhar() {
			Matriz matriz = abaView2D.getMatriz();
			if (matriz == null) {
				Util.mensagem(AbaView3D.this, LabirintoMensagens.getString(MSG_FAVOR_CONSTRUIR_MATRIZ));
				return;
			}
			Pivo pivo = matriz.pivo;
			List<Bloco> blocos = matriz.getBlocosNaFrente(pivo);
			if (blocos.isEmpty() || blocos.size() == 1) {
				Util.mensagem(AbaView3D.this, "Sem blocos a frente");
				return;
			}
			Bloco proximoPiso = blocos.get(1);
			if (!proximoPiso.vazio) {
				Util.mensagem(AbaView3D.this, "O bloco nao esta vazio");
				return;
			}
			pivo.ocupar(proximoPiso);
			selecionarBlocos(matriz);
		}

		@Override
		protected void baixar() {
			Matriz matriz = abaView2D.getMatriz();
			if (matriz == null) {
				Util.mensagem(AbaView3D.this, LabirintoMensagens.getString(MSG_FAVOR_CONSTRUIR_MATRIZ));
				return;
			}
			selecionarBlocos(matriz);
		}

		private void selecionarBlocos(Matriz matriz) {
			Pivo pivo = matriz.pivo;
			blocosEsquerdo = matriz.getBlocosLadoEsquerdo(pivo);
			blocosDireito = matriz.getBlocosLadoDireito(pivo);
			blocosFrente = matriz.getBlocosNaFrente(pivo);
			painelDesenho.configurarFaces();
			painelDesenho.repaint();
		}
	}

	class GirarAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		private final boolean esquerda;

		public GirarAction(boolean esquerda) {
			this.esquerda = esquerda;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			toolbar.girar(esquerda);
		}
	}

	class PainelDesenho extends Panel {
		private static final long serialVersionUID = 1L;
		private transient Vetor vetor1 = new Vetor();
		private transient Vetor vetor2 = new Vetor();
		private transient Vetor vetor3 = new Vetor();
		private transient Vetor vetor4 = new Vetor();

		PainelDesenho() {
			inputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "para_esquerda");
			inputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "para_direita");
			getActionMap().put("para_esquerda", new GirarAction(true));
			getActionMap().put("para_direita", new GirarAction(false));
		}

		void configurarFaces() {
			int metadeLargura = getWidth() / 2;
			int metadeAltura = getHeight() / 2;

			origem.x = metadeLargura;
			origem.y = metadeAltura;

			initVetores(metadeLargura);
			for (Bloco item : blocosFrente) {
				item.calcular3DFrente(origem, vetor1, vetor2, vetor3, vetor4);
			}

			initVetores(metadeLargura);
			for (Bloco item : blocosEsquerdo) {
				item.calcular3DEsquerdo(origem, vetor1, vetor4);
			}

			initVetores(metadeLargura);
			for (Bloco item : blocosDireito) {
				item.calcular3DDireito(origem, vetor2, vetor3);
			}
		}

		private void initVetores(int comprimento) {
			vetor1.init(-comprimento);
			vetor2.init(comprimento);
			vetor3.init(comprimento);
			vetor4.init(-comprimento);

			int grau = 35;
			vetor1.rotacionar(grau);
			vetor2.rotacionar(-grau);
			vetor3.rotacionar(grau);
			vetor4.rotacionar(-grau);
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			for (int i = blocosEsquerdo.size() - 1; i >= 0; i--) {
				Bloco item = blocosEsquerdo.get(i);
				item.desenhar3DEsquerdo(g2);
			}
			for (int i = blocosDireito.size() - 1; i >= 0; i--) {
				Bloco item = blocosDireito.get(i);
				item.desenhar3DDireito(g2);
			}
			for (int i = blocosFrente.size() - 1; i >= 0; i--) {
				Bloco item = blocosFrente.get(i);
				item.desenhar3DFrente(g2);
			}
		}
	}
}