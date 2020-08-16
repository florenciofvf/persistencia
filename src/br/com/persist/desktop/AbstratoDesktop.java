package br.com.persist.desktop;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

import br.com.persist.util.Constantes;

public class AbstratoDesktop extends JDesktopPane {
	private static final long serialVersionUID = 1L;
	protected final transient Distribuicao distribuicao = new Distribuicao();
	protected final transient Alinhamento alinhamento = new Alinhamento();
	protected final transient Larguras larguras = new Larguras();
	protected final transient Ajuste ajuste = new Ajuste();

	public class Distribuicao {
		public void distribuir(int delta) {
			int largura = (getSize().width - 20) + delta;
			int altura = Constantes.TREZENTOS_QUARENTA_UM;
			int y = 10;

			for (JInternalFrame frame : getAllFrames()) {
				frame.setSize(largura, altura);
				frame.setLocation(0, y);
				y += altura + 20;
			}

			alinhamento.centralizar();
			ajuste.ajusteDesktopUsandoForms();
		}
	}

	public class Alinhamento {
		public void alinhar(Alinhar alinhar) {
			if (Alinhar.ESQUERDO == alinhar) {
				esquerdo();
			} else if (Alinhar.DIREITO == alinhar) {
				direito();
			} else if (Alinhar.SOMENTE_DIREITO == alinhar) {
				somenteDireito();
			} else if (Alinhar.CENTRALIZAR == alinhar) {
				centralizar();
			}
		}

		private void esquerdo() {
			JInternalFrame[] frames = getAllFrames();

			if (frames.length > 0) {
				int x = frames[0].getX();

				for (int i = 1; i < frames.length; i++) {
					frames[i].setLocation(x, frames[i].getY());
				}
			}
		}

		private void direito() {
			JInternalFrame[] frames = getAllFrames();

			if (frames.length > 0) {
				int l = frames[0].getWidth();
				int x = frames[0].getX();
				int xlAux = x + l;

				for (int i = 1; i < frames.length; i++) {
					JInternalFrame frame = frames[i];
					int lAux = frame.getWidth();
					int xAux = frame.getX();
					int xlAux2 = xAux + lAux;
					int diff = xlAux - xlAux2;

					frame.setLocation(xAux + diff, frame.getY());
				}
			}
		}

		private void somenteDireito() {
			JInternalFrame[] frames = getAllFrames();

			if (frames.length > 0) {
				int l = frames[0].getWidth();
				int x = frames[0].getX();
				int xlAux = x + l;

				for (int i = 1; i < frames.length; i++) {
					JInternalFrame frame = frames[i];
					int lAux = frame.getWidth();
					int xAux = frame.getX();
					int xlAux2 = xAux + lAux;
					int diff = xlAux - xlAux2;
					int newL = lAux + diff;

					if (newL <= Constantes.DEZ) {
						continue;
					}

					frame.setSize(newL, frame.getHeight());
				}
			}
		}

		private void centralizar() {
			double largura = getSize().getWidth();

			for (JInternalFrame frame : getAllFrames()) {
				if (frame.getWidth() >= largura) {
					frame.setLocation(0, frame.getY());
				} else {
					frame.setLocation((int) ((largura - frame.getWidth()) / 2), frame.getY());
				}
			}
		}
	}

	public class Larguras {
		public void mesma() {
			JInternalFrame[] frames = getAllFrames();

			if (frames.length > 0) {
				int largura = frames[0].getWidth();

				for (int i = 1; i < frames.length; i++) {
					frames[i].setSize(largura, frames[i].getHeight());
				}
			}
		}

		public void configurar(Largura larguraEnum) {
			int largura = getSize().width - 20;

			for (JInternalFrame frame : getAllFrames()) {
				Dimension size = frame.getSize();
				Point local = frame.getLocation();

				if (Largura.TOTAL == larguraEnum) {
					frame.setLocation(0, local.y);
					frame.setSize(largura, size.height);

				} else if (Largura.TOTAL_A_DIREITA == larguraEnum) {
					frame.setSize(largura - local.x, size.height);

				} else if (Largura.TOTAL_A_ESQUERDA == larguraEnum) {
					int total = (local.x + size.width) - 10;
					frame.setSize(total, size.height);
					frame.setLocation(10, local.y);
				}
			}

			if (Largura.TOTAL == larguraEnum) {
				alinhamento.centralizar();
			}
		}
	}

	public class Ajuste {
		public void ajusteDesktopRetirarRolagem() {
			setPreferredSize(new Dimension(1, 1));
			SwingUtilities.updateComponentTreeUI(getParent());
		}

		public void ajusteDesktopUsandoForms() {
			int largura = 0;
			int altura = 0;

			for (JInternalFrame frame : getAllFrames()) {
				int x = frame.getX();
				int y = frame.getY();
				int l = frame.getWidth();
				int a = frame.getHeight();

				if (x + l > largura) {
					largura = x + l;
				}

				if (y + a > altura) {
					altura = y + a;
				}

				frame.moveToFront();
			}

			setPreferredSize(new Dimension(largura, altura + Constantes.QUARENTA_UM));
		}
	}

	public Distribuicao getDistribuicao() {
		return distribuicao;
	}

	public Alinhamento getAlinhamento() {
		return alinhamento;
	}

	public Larguras getLarguras() {
		return larguras;
	}

	public Ajuste getAjuste() {
		return ajuste;
	}
}