package br.com.persist.plugins.labirinto;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import br.com.persist.assistencia.Constantes;
import br.com.persist.componente.LabelTemp;
import br.com.persist.componente.Panel;

public class FormularioLabirinto extends JFrame {
	private static final long serialVersionUID = -1355576882746374912L;
	private PainelDesenho painelDesenho = new PainelDesenho();
	private LabelTemp status = new LabelTemp();
	private final transient Matriz matriz;

	public FormularioLabirinto(Matriz matriz, GraphicsConfiguration gc) {
		super(LabirintoMensagens.getString(LabirintoConstantes.LABEL_LABIRINTO), gc);
		this.matriz = Objects.requireNonNull(matriz);
		matriz.setLado(10);
		ini();
	}

	private void ini() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, painelDesenho);
		add(BorderLayout.SOUTH, status);
		setSize(Constantes.SIZE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				new InitAction().actionPerformed(null);
			}

			@Override
			public void windowClosing(WindowEvent e) {
				matriz.setLado(40);
			}
		});
	}

	class InitAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			painelDesenho.init();
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
			Pivo pivo = matriz.pivo;
			if (esquerda) {
				pivo.antiHorario();
			} else {
				pivo.horario();
			}
			painelDesenho.selecionarBlocos();
			painelDesenho.configurarFaces(0);
		}
	}

	int comprimentoDelta = 50;

	class CaminharAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			Pivo pivo = matriz.pivo;
			if (pivo.podeCaminharNoPiso()) {
				pivo.caminhar();
				painelDesenho.selecionarBlocos();
				painelDesenho.configurarFaces(comprimentoDelta);
				return;
			}
			List<Bloco> blocos = matriz.getBlocosNaFrente(pivo);
			if (blocos.isEmpty() || blocos.size() == 1) {
				status.mensagem("Sem blocos a frente");
				return;
			}
			Bloco proximoPiso = blocos.get(1);
			if (!proximoPiso.vazio) {
				status.mensagem("O bloco nao esta vazio");
				return;
			}
			pivo.ocupar(proximoPiso);
			painelDesenho.initOcupado();
			painelDesenho.selecionarBlocos();
			painelDesenho.configurarFaces(0);
		}
	}

	class PainelDesenho extends Panel {
		private transient List<Bloco> blocosEsquerdo = new ArrayList<>();
		private transient List<Bloco> blocosDireito = new ArrayList<>();
		private transient List<Bloco> blocosFrente = new ArrayList<>();
		private final transient Terreno terreno = new Terreno();
		private final transient Ponto origem = new Ponto();
		private static final long serialVersionUID = 1L;
		private transient Vetor vetor1 = new Vetor();
		private transient Vetor vetor2 = new Vetor();
		private transient Vetor vetor3 = new Vetor();
		private transient Vetor vetor4 = new Vetor();
		private int comprimentoVetores;

		PainelDesenho() {
			inputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "para_esquerda");
			inputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "para_direita");
			inputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "caminhar");
			inputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "init");
			getActionMap().put("para_esquerda", new GirarAction(true));
			getActionMap().put("para_direita", new GirarAction(false));
			getActionMap().put("caminhar", new CaminharAction());
			getActionMap().put("init", new InitAction());
		}

		void init() {
			int metadeLargura = getWidth() / 2;
			int metadeAltura = getHeight() / 2;
			origem.x = metadeLargura;
			origem.y = metadeAltura;
			comprimentoVetores = LabirintoUtil.comprimento(origem.x, origem.y, getWidth(), getHeight())
					+ Pivo.COMPRIMENTO;
			selecionarBlocos();
			configurarFaces(0);
			Pivo pivo = matriz.pivo;
			pivo.passos = 1;
		}

		void initOcupado() {
			comprimentoVetores = LabirintoUtil.comprimento(origem.x, origem.y, getWidth(), getHeight())
					+ Pivo.COMPRIMENTO;
			Pivo pivo = matriz.pivo;
			pivo.passos = 0;
		}

		void selecionarBlocos() {
			Pivo pivo = matriz.pivo;
			blocosEsquerdo = matriz.getBlocosLadoEsquerdo(pivo);
			blocosDireito = matriz.getBlocosLadoDireito(pivo);
			blocosFrente = matriz.getBlocosNaFrente(pivo);
		}

		void configurarFaces(int comprimentoDelta) {
			comprimentoVetores += comprimentoDelta;

			initVetores(comprimentoVetores);
			for (int linha = Terreno.LINHAS - 1; linha >= 0; linha--) {
				terreno.calcular3D(origem, vetor1, vetor2, vetor3, vetor4, linha);
			}

			initVetores(comprimentoVetores);
			for (Bloco item : blocosFrente) {
				item.calcular3DFrente(origem, vetor1, vetor2, vetor3, vetor4);
			}

			initVetores(comprimentoVetores);
			for (Bloco item : blocosEsquerdo) {
				item.calcular3DEsquerdo(origem, vetor1, vetor4);
			}

			initVetores(comprimentoVetores);
			for (Bloco item : blocosDireito) {
				item.calcular3DDireito(origem, vetor2, vetor3);
			}

			repaint();
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
			terreno.desenhar3D(g2);
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
			matriz.desenhar2D(g2);
		}
	}
}