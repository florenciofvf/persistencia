package br.com.persist.desktop;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

import br.com.persist.componente.Action;
import br.com.persist.componente.Menu;
import br.com.persist.util.Icones;
import br.com.persist.util.Constantes;
import br.com.persist.util.Util;

public abstract class AbstratoDesktop extends JDesktopPane {
	private static final long serialVersionUID = 1L;
	protected final transient MenuAlinhamento menuAlinhamento = new MenuAlinhamento();
	protected final transient Distribuicao distribuicao = new Distribuicao();
	protected final transient Alinhamento alinhamento = new Alinhamento();
	protected final transient MenuLargura menuLargura = new MenuLargura();
	protected final transient MenuAjustar menuAjustar = new MenuAjustar();
	protected final transient MenuAjuste menuAjuste = new MenuAjuste();
	protected final transient Larguras larguras = new Larguras();
	protected final transient Ajustar ajustar = new Ajustar();
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
			ajustar.usarFormularios(true);
		}
	}

	public class Alinhamento {
		public void alinhar(DesktopAlinhamento alinhar) {
			if (DesktopAlinhamento.ESQUERDO == alinhar) {
				esquerdo();
			} else if (DesktopAlinhamento.DIREITO == alinhar) {
				direito();
			} else if (DesktopAlinhamento.SOMENTE_DIREITO == alinhar) {
				somenteDireito();
			} else if (DesktopAlinhamento.CENTRALIZAR == alinhar) {
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

		public void configurar(DesktopLargura larguraEnum) {
			int largura = getSize().width - 20;

			for (JInternalFrame frame : getAllFrames()) {
				Dimension size = frame.getSize();
				Point local = frame.getLocation();

				if (DesktopLargura.TOTAL == larguraEnum) {
					frame.setLocation(0, local.y);
					frame.setSize(largura, size.height);

				} else if (DesktopLargura.TOTAL_A_DIREITA == larguraEnum) {
					frame.setSize(largura - local.x, size.height);

				} else if (DesktopLargura.TOTAL_A_ESQUERDA == larguraEnum) {
					int total = (local.x + size.width) - 10;
					frame.setSize(total, size.height);
					frame.setLocation(10, local.y);
				}
			}

			if (DesktopLargura.TOTAL == larguraEnum) {
				alinhamento.centralizar();
			}
		}
	}

	public abstract void empilharFormulariosImpl();

	public abstract void aproximarObjetoFormularioImpl(boolean objetoAoFormulario, boolean updateTree);

	protected class MenuLargura extends Menu {
		private static final long serialVersionUID = 1L;
		private Action esquerdoAcao = Action.actionMenu("label.total_esquerdo", Icones.ALINHA_ESQUERDO);
		private Action direitoAcao = Action.actionMenu("label.total_direito", Icones.ALINHA_DIREITO);
		private Action distribuirAcao = Action.actionMenu("label.distribuir", Icones.CENTRALIZAR);
		private Action totalAcao = Action.actionMenu("label.total", Icones.LARGURA);

		protected MenuLargura() {
			super("label.largura", Icones.CENTRALIZAR);

			addMenuItem(totalAcao);
			addMenuItem(direitoAcao);
			addMenuItem(esquerdoAcao);
			addMenuItem(distribuirAcao);

			esquerdoAcao.setActionListener(e -> larguras.configurar(DesktopLargura.TOTAL_A_ESQUERDA));
			direitoAcao.setActionListener(e -> larguras.configurar(DesktopLargura.TOTAL_A_DIREITA));
			totalAcao.setActionListener(e -> larguras.configurar(DesktopLargura.TOTAL));
			distribuirAcao.setActionListener(e -> distribuicao.distribuir(0));
		}

		public void habilitar(boolean b) {
			distribuirAcao.setEnabled(b);
			esquerdoAcao.setEnabled(b);
			direitoAcao.setEnabled(b);
			totalAcao.setEnabled(b);
			setEnabled(b);
		}
	}

	protected class MenuAjustar extends Menu {
		private static final long serialVersionUID = 1L;
		private Action usarFormularioAcao = Action.actionMenu("label.usar_formularios", null);
		private Action dimensaoManualAcao = Action.actionMenu("label.dimensao_manual", null);
		private Action retirarRolagemAcao = Action.actionMenu("label.retirar_rolagem", null);

		protected MenuAjustar() {
			super("label.ajustar", Icones.RECT);

			addMenuItem(dimensaoManualAcao);
			addMenuItem(usarFormularioAcao);
			addMenuItem(retirarRolagemAcao);

			usarFormularioAcao.setActionListener(e -> ajustar.usarFormularios(true));
			retirarRolagemAcao.setActionListener(e -> ajustar.retirarRolagem());
			dimensaoManualAcao.setActionListener(e -> ajustar.ajusteManual());
		}

		public void habilitar(boolean b) {
			dimensaoManualAcao.setEnabled(b);
			retirarRolagemAcao.setEnabled(b);
			usarFormularioAcao.setEnabled(b);
			setEnabled(b);
		}
	}

	protected class MenuAjuste extends Menu {
		private static final long serialVersionUID = 1L;
		private Action aproximarFormAoObjetoAcao = Action.actionMenu("label.aproximar_form_ao_objeto", null);
		private Action aproximarObjetoAoFormAcao = Action.actionMenu("label.aproximar_objeto_ao_form", null);
		private Action empilharAcao = Action.actionMenu("label.empilhar_formularios", null);

		protected MenuAjuste() {
			super("label.ajuste", Icones.RECT);

			addMenuItem(aproximarFormAoObjetoAcao);
			addMenuItem(aproximarObjetoAoFormAcao);
			addMenuItem(empilharAcao);

			aproximarFormAoObjetoAcao.setActionListener(e -> ajuste.aproximarObjetoFormulario(false, false));
			aproximarObjetoAoFormAcao.setActionListener(e -> ajuste.aproximarObjetoFormulario(true, false));
			empilharAcao.setActionListener(e -> ajuste.empilharFormularios());
		}

		public void habilitar(boolean b) {
			aproximarFormAoObjetoAcao.setEnabled(b);
			aproximarObjetoAoFormAcao.setEnabled(b);
			empilharAcao.setEnabled(b);
			setEnabled(b);
		}
	}

	protected class MenuAlinhamento extends Menu {
		private static final long serialVersionUID = 1L;
		private Action somenteDireitoAcao = Action.actionMenu("label.somente_direito", Icones.ALINHA_DIREITO);
		private Action mesmaLarguraAcao = Action.actionMenu("label.mesma_largura", Icones.LARGURA);
		private Action esquerdoAcao = Action.actionMenu("label.esquerdo", Icones.ALINHA_ESQUERDO);
		private Action centralizarAcao = Action.actionMenu("label.centralizar", Icones.LARGURA);
		private Action direitoAcao = Action.actionMenu("label.direito", Icones.ALINHA_DIREITO);

		protected MenuAlinhamento() {
			super("label.alinhamento", Icones.LARGURA);

			addMenuItem(direitoAcao);
			addMenuItem(esquerdoAcao);
			addMenuItem(centralizarAcao);
			addMenuItem(mesmaLarguraAcao);
			addMenuItem(somenteDireitoAcao);

			somenteDireitoAcao.setActionListener(e -> alinhamento.alinhar(DesktopAlinhamento.SOMENTE_DIREITO));
			centralizarAcao.setActionListener(e -> alinhamento.alinhar(DesktopAlinhamento.CENTRALIZAR));
			esquerdoAcao.setActionListener(e -> alinhamento.alinhar(DesktopAlinhamento.ESQUERDO));
			direitoAcao.setActionListener(e -> alinhamento.alinhar(DesktopAlinhamento.DIREITO));
			mesmaLarguraAcao.setActionListener(e -> larguras.mesma());
		}

		public void habilitar(boolean b) {
			somenteDireitoAcao.setEnabled(b);
			mesmaLarguraAcao.setEnabled(b);
			centralizarAcao.setEnabled(b);
			esquerdoAcao.setEnabled(b);
			direitoAcao.setEnabled(b);
			setEnabled(b);
		}
	}

	public class Ajustar {
		public void ajusteManual() {
			String string = getWidth() + "," + getHeight();
			Object resp = Util.getValorInputDialog(AbstratoDesktop.this, "label.largura_altura", string, string);

			if (resp == null || Util.estaVazio(resp.toString())) {
				return;
			}

			String[] strings = resp.toString().split(",");

			if (strings != null && strings.length == 2) {
				int largura = Integer.parseInt(strings[0].trim());
				int altura = Integer.parseInt(strings[1].trim());

				setPreferredSize(new Dimension(largura, altura));
				SwingUtilities.updateComponentTreeUI(getParent());
			}
		}

		public void retirarRolagem() {
			setPreferredSize(new Dimension(1, 1));
			SwingUtilities.updateComponentTreeUI(getParent());
		}

		public void usarFormularios(boolean updateTree) {
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

			if (updateTree) {
				SwingUtilities.updateComponentTreeUI(getParent());
			}
		}
	}

	public class Ajuste {
		public void empilharFormularios() {
			empilharFormulariosImpl();
		}

		public void aproximarObjetoFormulario(boolean objetoAoFormulario, boolean updateTree) {
			aproximarObjetoFormularioImpl(objetoAoFormulario, updateTree);
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

	public Ajustar getAjustar() {
		return ajustar;
	}

	public Ajuste getAjuste() {
		return ajuste;
	}
}