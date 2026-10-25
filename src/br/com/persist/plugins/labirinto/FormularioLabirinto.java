package br.com.persist.plugins.labirinto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import br.com.persist.assistencia.Constantes;
import br.com.persist.componente.LabelTemp;
import br.com.persist.componente.Panel;

public class FormularioLabirinto extends JFrame {
	private static final long serialVersionUID = -1355576882746374912L;
	private PainelDesenho painelDesenho = new PainelDesenho();
	private transient FormularioLabirintoListener listener;
	private LabelTemp status = new LabelTemp();
	private final transient Matriz matriz;

	public FormularioLabirinto(Matriz matriz, GraphicsConfiguration gc) {
		super(LabirintoMensagens.getString(LabirintoConstantes.LABEL_LABIRINTO), gc);
		this.matriz = Objects.requireNonNull(matriz);
		init();
	}

	public FormularioLabirintoListener getListener() {
		return listener;
	}

	public void setListener(FormularioLabirintoListener listener) {
		this.listener = listener;
	}

	private void notificar() {
		if (listener != null) {
			listener.alterado(matriz, matriz.pivo);
		}
	}

	void processar() {
		painelDesenho.processar();
	}

	private void init() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, painelDesenho);
		add(BorderLayout.SOUTH, status);
		setSize(Constantes.SIZE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				processar();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				if (listener != null) {
					listener.fechado(FormularioLabirinto.this);
				}
			}
		});
	}

	class GirarAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		private final boolean esquerda;

		public GirarAction(boolean esquerda) {
			this.esquerda = esquerda;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (esquerda) {
				painelDesenho.girarEsquerdo();
			} else {
				painelDesenho.girarDireito();
			}
			notificar();
		}
	}

	class CaminharAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			Pivo pivo = matriz.pivo;
			if (pivo.podeCaminharNoPiso()) {
				pivo.caminhar();
				painelDesenho.processar();
				notificar();
				return;
			}
			List<Bloco> blocos = matriz.getBlocosNaFrente(pivo);
			if (blocos.size() < 2) {
				status.mensagem(LabirintoMensagens.getString("msg.sem_blocos_a_frente"));
				return;
			}
			Bloco proximoPiso = blocos.get(1);
			if (!proximoPiso.vazio) {
				status.mensagem(LabirintoMensagens.getString("msg.bloco_nao_vazio"));
				return;
			}
			pivo.ocupar(proximoPiso);
			painelDesenho.processar();
			notificar();
		}
	}

	class PainelDesenho extends Panel implements ActionListener {
		private final transient GiroControle giroControle = new GiroControle();
		private transient List<Bloco> blocosEsquerdo = new ArrayList<>();
		private transient List<Bloco> blocosDireito = new ArrayList<>();
		private transient List<Bloco> blocosFrente = new ArrayList<>();
		private final transient Terreno terreno = new Terreno();
		private final transient Ponto origem = new Ponto();
		private final transient Vetor vetor1 = new Vetor();
		private final transient Vetor vetor2 = new Vetor();
		private final transient Vetor vetor3 = new Vetor();
		private final transient Vetor vetor4 = new Vetor();
		private static final long serialVersionUID = 1L;
		private int velocidadeGiro = 20;
		private Timer timerGiroControle;

		PainelDesenho() {
			inputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "para_esquerda");
			inputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "para_direita");
			inputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "caminhar");
			getActionMap().put("para_esquerda", new GirarAction(true));
			getActionMap().put("para_direita", new GirarAction(false));
			getActionMap().put("caminhar", new CaminharAction());
			addMouseListener(new OuvinteMouse());
		}

		private class OuvinteMouse extends MouseAdapter {
			@Override
			public void mousePressed(MouseEvent e) {
				processar();
			}
		}

		class GiroControle {
			Ponto origem = new Ponto();
			Vetor vetor1 = new Vetor();
			Vetor vetor2 = new Vetor();
			Vetor vetor3 = new Vetor();
			Vetor vetor4 = new Vetor();
			int parcelaMaximo = 5;
			boolean processando;
			boolean esquerdo;
			int repeticoes;
			int parcela;

			void processarEsquerdo() {
				int largura = getHeight() - (repeticoes * parcela);
				configurarFacesEsquerdo(largura);
				origem.x += parcela;
				repeticoes++;
			}

			void configurarFacesEsquerdo(int largura) {
				initVetoresEsquerdo(largura);
				for (int linha = Terreno.LINHAS - 1; linha >= 0; linha--) {
					terreno.calcular3DGiroE(origem, vetor2, vetor3, linha);
				}
				initVetoresEsquerdo(largura);
				for (Bloco item : blocosFrente) {
					item.calcular3DFrenteGiroE(origem, vetor2, vetor3);
				}
				initVetoresEsquerdo(largura);
				for (Bloco item : blocosDireito) {
					item.calcular3DDireito(origem, vetor2, vetor3);
				}
				repaint();
			}

			private void initVetoresEsquerdo(int largura) {
				vetor2.init(largura, -origem.y);
				vetor3.init(largura, origem.y);
			}

			void processarDireito() {
				int largura = getHeight() - (repeticoes * parcela);
				configurarFacesDireito(largura);
				origem.x -= parcela;
				repeticoes++;
			}

			void configurarFacesDireito(int largura) {
				initVetoresDireito(largura);
				for (int linha = Terreno.LINHAS - 1; linha >= 0; linha--) {
					terreno.calcular3DGiroD(origem, vetor1, vetor4, linha, getWidth());
				}
				initVetoresDireito(largura);
				for (Bloco item : blocosFrente) {
					item.calcular3DFrenteGiroD(origem, vetor1, vetor4, getWidth());
				}
				initVetoresDireito(largura);
				for (Bloco item : blocosEsquerdo) {
					item.calcular3DEsquerdo(origem, vetor1, vetor4);
				}
				repaint();
			}

			private void initVetoresDireito(int largura) {
				vetor1.init(-largura, -origem.y);
				vetor4.init(-largura, origem.y);
			}
		}

		void girarEsquerdo() {
			if (giroControle.processando) {
				return;
			}
			giroControle.processando = true;
			giroControle.esquerdo = true;
			giroControle.repeticoes = 0;

			Pivo pivo = matriz.pivo;
			pivo.antiHorario();
			blocosDireito = matriz.getBlocosLadoDireito(pivo);
			blocosFrente = matriz.getBlocosNaFrente(pivo);

			int metadeLargura = getWidth() / 2;
			giroControle.parcela = metadeLargura / giroControle.parcelaMaximo;
			giroControle.origem.x = 0;
			giroControle.origem.y = getHeight() / 2;

			if (timerGiroControle == null) {
				timerGiroControle = new Timer(velocidadeGiro, this);
			}
			timerGiroControle.start();
		}

		void girarDireito() {
			if (giroControle.processando) {
				return;
			}
			giroControle.processando = true;
			giroControle.esquerdo = false;
			giroControle.repeticoes = 0;

			Pivo pivo = matriz.pivo;
			pivo.horario();
			blocosEsquerdo = matriz.getBlocosLadoEsquerdo(pivo);
			blocosFrente = matriz.getBlocosNaFrente(pivo);

			int metadeLargura = getWidth() / 2;
			giroControle.parcela = metadeLargura / giroControle.parcelaMaximo;
			giroControle.origem.x = getWidth();
			giroControle.origem.y = getHeight() / 2;

			if (timerGiroControle == null) {
				timerGiroControle = new Timer(velocidadeGiro, this);
			}
			timerGiroControle.start();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (giroControle.repeticoes >= giroControle.parcelaMaximo) {
				giroControle.processando = false;
				timerGiroControle.stop();
				processar();
			} else {
				if (giroControle.esquerdo) {
					giroControle.processarEsquerdo();
				} else {
					giroControle.processarDireito();
				}
			}
		}

		void processar() {
			selecionarBlocos();
			configurarFaces();
		}

		void selecionarBlocos() {
			Pivo pivo = matriz.pivo;
			blocosEsquerdo = matriz.getBlocosLadoEsquerdo(pivo);
			blocosDireito = matriz.getBlocosLadoDireito(pivo);
			blocosFrente = matriz.getBlocosNaFrente(pivo);
		}

		void configurarFaces() {
			origem.x = getWidth() / 2;
			origem.y = getHeight() / 2;

			double comprimento = LabirintoUtil.comprimento(origem.x, origem.y, getWidth(), getHeight());
			double comprimentoFracao = comprimento * .1;

			Pivo pivo = matriz.pivo;
			pivo.comprimentoFracao = comprimentoFracao / 2;
			int comprimentoVetores = (int) pivo.calibrar(comprimento);

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
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, getWidth(), getHeight());
			matriz.setLado(10);
			if (giroControle.processando) {
				desenharGiro(g2);
			} else {
				desenhar(g2);
			}
		}

		private void desenharGiro(Graphics2D g2) {
			if (giroControle.esquerdo) {
				terreno.desenhar3DGiroE(g2);
				for (int i = blocosDireito.size() - 1; i >= 0; i--) {
					Bloco item = blocosDireito.get(i);
					item.desenhar3DDireito(g2);
				}
				for (int i = blocosFrente.size() - 1; i >= 0; i--) {
					Bloco item = blocosFrente.get(i);
					item.desenhar3DFrente(g2);
				}
				matriz.desenhar2D(g2);
			} else {
				terreno.desenhar3DGiroD(g2);
				for (int i = blocosEsquerdo.size() - 1; i >= 0; i--) {
					Bloco item = blocosEsquerdo.get(i);
					item.desenhar3DEsquerdo(g2);
				}
				for (int i = blocosFrente.size() - 1; i >= 0; i--) {
					Bloco item = blocosFrente.get(i);
					item.desenhar3DFrente(g2);
				}
				matriz.desenhar2D(g2);
			}
		}

		private void desenhar(Graphics2D g2) {
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