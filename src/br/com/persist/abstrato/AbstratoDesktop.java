package br.com.persist.abstrato;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.Menu;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.FicharioHandler;

public abstract class AbstratoDesktop extends JDesktopPane implements WindowHandler, FicharioHandler {
	protected final transient Distribuicao distribuicao = new Distribuicao();
	protected final transient Alinhamento alinhamento = new Alinhamento();
	protected final transient MenuLargura menuLargura = new MenuLargura();
	protected final transient MenuAjustar menuAjustar = new MenuAjustar();
	protected final transient MenuAjuste menuAjuste = new MenuAjuste();
	protected final transient Larguras larguras = new Larguras();
	protected final transient Ajustar ajustar = new Ajustar();
	protected final transient Ajuste ajuste = new Ajuste();
	private static final long serialVersionUID = 1L;
	private boolean ajusteLarguraForm;

	public class Distribuicao {
		public void distribuir(int delta) {
			int largura = (getSize().width - 20) + delta;
			int altura = Constantes.TREZENTOS_QUARENTA_UM;
			distribuir(largura, altura);
			alinhamento.centralizar();
			ajustar.usarFormularios(true);
		}

		private void distribuir(int largura, int altura) {
			int y = 10;
			for (JInternalFrame frame : getAllFrames()) {
				if (frame.isVisible()) {
					frame.setSize(largura, altura);
					frame.setLocation(0, y);
					y += altura + 20;
				}
			}
		}
	}

	public class Alinhamento {
		public void alinhar(JInternalFrame ref, DesktopAlinhamento alinhar) {
			if (DesktopAlinhamento.ESQUERDO == alinhar) {
				esquerdo(ref);
			} else if (DesktopAlinhamento.DIREITO == alinhar) {
				direito(ref);
			} else if (DesktopAlinhamento.COMPLETAR_DIREITO == alinhar) {
				completarDireito(ref);
			}
		}

		private void esquerdo(JInternalFrame ref) {
			int x = ref.getX();
			for (JInternalFrame frame : getAllFrames()) {
				if (frame.isVisible()) {
					frame.setLocation(x, frame.getY());
				}
			}
		}

		private void direito(JInternalFrame ref) {
			int xLargura = ref.getX() + ref.getWidth();
			for (JInternalFrame frame : getAllFrames()) {
				if (frame.isVisible()) {
					int xLargura2 = frame.getX() + frame.getWidth();
					int diff = xLargura - xLargura2;
					frame.setLocation(frame.getX() + diff, frame.getY());
				}
			}
		}

		private void completarDireito(JInternalFrame ref) {
			int xlargura = ref.getX() + ref.getWidth();
			for (JInternalFrame frame : getAllFrames()) {
				if (frame.isVisible()) {
					int xLargura2 = frame.getX() + frame.getWidth();
					int diff = xlargura - xLargura2;
					int novaLargura = frame.getWidth() + diff;
					if (novaLargura > Constantes.DEZ) {
						frame.setSize(novaLargura, frame.getHeight());
					}
				}
			}
		}

		public void centralizar() {
			double largura = getSize().getWidth();
			for (JInternalFrame frame : getAllFrames()) {
				if (frame.isVisible()) {
					if (frame.getWidth() >= largura) {
						frame.setLocation(0, frame.getY());
					} else {
						frame.setLocation((int) ((largura - frame.getWidth()) / 2), frame.getY());
					}
				}
			}
		}
	}

	public class Larguras {
		public void mesma(JInternalFrame ref) {
			int largura = ref.getWidth();
			for (JInternalFrame frame : getAllFrames()) {
				if (frame.isVisible()) {
					frame.setSize(largura, frame.getHeight());
				}
			}
		}

		public void configurar(DesktopLargura larguraEnum) {
			configurar(larguraEnum, null);
		}

		public void configurar(DesktopLargura larguraEnum, JInternalFrame internal) {
			int largura = getSize().width - 20;
			if (internal != null) {
				for (JInternalFrame frame : getAllFrames()) {
					if (frame.isVisible() && frame == internal) {
						configurar(larguraEnum, largura, frame);
					}
				}
			} else {
				for (JInternalFrame frame : getAllFrames()) {
					if (frame.isVisible()) {
						configurar(larguraEnum, largura, frame);
					}
				}
			}
			if (DesktopLargura.TOTAL == larguraEnum) {
				alinhamento.centralizar();
			}
		}

		private void configurar(DesktopLargura larguraEnum, int largura, JInternalFrame frame) {
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
	}

	public abstract void empilharFormulariosImpl();

	public abstract void aproximarObjetoFormularioImpl(boolean objetoAoFormulario, boolean updateTree);

	public void addTotalDireitoAuto() {
		menuLargura.addTotalDireitoAuto();
	}

	public void setTotalDireitoAuto(boolean b) {
		menuLargura.setTotalDireitoAuto(b);
	}

	protected class MenuLargura extends Menu {
		private Action direitoAutoAcao = acaoMenu("label.total_direito_auto", Icones.ALINHA_DIREITO);
		private Action esquerdoAcao = acaoMenu("label.total_esquerdo", Icones.ALINHA_ESQUERDO);
		private Action direitoAcao = acaoMenu("label.total_direito", Icones.ALINHA_DIREITO);
		private Action totalAcao = actionMenu("label.total", Icones.LARGURA);
		private static final long serialVersionUID = 1L;
		private JCheckBoxMenuItem checkDireitoAuto;

		protected MenuLargura() {
			super(AbstratoMensagens.getString("label.largura"), false, Icones.RECT);
			addMenuItem(totalAcao);
			addMenuItem(direitoAcao);
			addMenuItem(esquerdoAcao);
			direitoAutoAcao.setActionListener(e -> ajusteLarguraForm((JCheckBoxMenuItem) e.getSource()));
			esquerdoAcao.setActionListener(e -> larguras.configurar(DesktopLargura.TOTAL_A_ESQUERDA));
			direitoAcao.setActionListener(e -> larguras.configurar(DesktopLargura.TOTAL_A_DIREITA));
			totalAcao.setActionListener(e -> larguras.configurar(DesktopLargura.TOTAL));
		}

		private void ajusteLarguraForm(JCheckBoxMenuItem check) {
			setAjusteLarguraForm(check.isSelected());
			if (check.isSelected()) {
				larguras.configurar(DesktopLargura.TOTAL_A_DIREITA);
			}
		}

		public void addTotalDireitoAuto() {
			if (checkDireitoAuto == null) {
				checkDireitoAuto = new JCheckBoxMenuItem(direitoAutoAcao);
				add(true, checkDireitoAuto);
			}
		}

		public void setTotalDireitoAuto(boolean b) {
			if (checkDireitoAuto != null) {
				checkDireitoAuto.setSelected(b);
			}
		}

		public void habilitar(boolean b) {
			direitoAutoAcao.setEnabled(b);
			esquerdoAcao.setEnabled(b);
			direitoAcao.setEnabled(b);
			totalAcao.setEnabled(b);
			setEnabled(b);
		}
	}

	protected class MenuAjustar extends Menu {
		private Action usarFormularioAcao = acaoMenu("label.usar_formularios");
		private Action dimensaoManualAcao = acaoMenu("label.dimensao_manual");
		private Action retirarRolagemAcao = acaoMenu("label.retirar_rolagem");
		private static final long serialVersionUID = 1L;

		protected MenuAjustar() {
			super(AbstratoMensagens.getString("label.ajustar"), false, Icones.RECT);
			addMenuItem(dimensaoManualAcao);
			addMenuItem(usarFormularioAcao);
			addMenuItem(retirarRolagemAcao);
			usarFormularioAcao.setActionListener(e -> ajustar.usarFormularios(true));
			retirarRolagemAcao.setActionListener(e -> ajustar.retirarRolagem());
			dimensaoManualAcao.setActionListener(e -> ajustar.ajusteManual());
		}

		public void habilitar(boolean b) {
			usarFormularioAcao.setEnabled(b);
		}
	}

	static Action acaoMenu(String chave, Icon icon) {
		return Action.acaoMenu(AbstratoMensagens.getString(chave), icon);
	}

	static Action acaoMenu(String chave) {
		return acaoMenu(chave, null);
	}

	protected class MenuAjuste extends Menu {
		private Action aproximarFormAoObjetoAcao = acaoMenu("label.aproximar_form_ao_objeto");
		private Action aproximarObjetoAoFormAcao = acaoMenu("label.aproximar_objeto_ao_form");
		private Action distribuirAcao = actionMenu("label.distribuir", Icones.CENTRALIZAR);
		private Action centralizarAcao = actionMenu("label.centralizar", Icones.LARGURA);
		private Action empilharAcao = acaoMenu("label.empilhar_formularios");
		private static final long serialVersionUID = 1L;

		protected MenuAjuste() {
			super(AbstratoMensagens.getString("label.ajuste"), false, Icones.RECT);
			addMenuItem(aproximarFormAoObjetoAcao);
			addMenuItem(aproximarObjetoAoFormAcao);
			addMenuItem(empilharAcao);
			addMenuItem(centralizarAcao);
			addMenuItem(distribuirAcao);
			aproximarFormAoObjetoAcao.setActionListener(e -> ajuste.aproximarObjetoFormulario(false, false));
			aproximarObjetoAoFormAcao.setActionListener(e -> ajuste.aproximarObjetoFormulario(true, false));
			empilharAcao.setActionListener(e -> ajuste.empilharFormularios());
			centralizarAcao.setActionListener(e -> alinhamento.centralizar());
			distribuirAcao.setActionListener(e -> distribuicao.distribuir(0));
		}

		public void habilitar(boolean b) {
			aproximarFormAoObjetoAcao.setEnabled(b);
			aproximarObjetoAoFormAcao.setEnabled(b);
			centralizarAcao.setEnabled(b);
			distribuirAcao.setEnabled(b);
			empilharAcao.setEnabled(b);
			setEnabled(b);
		}
	}

	public class Ajustar {
		public void ajusteManual() {
			String string = getWidth() + "," + getHeight();
			Object resp = Util.getValorInputDialog(AbstratoDesktop.this, "label.largura_altura", string, string);
			if (resp != null && !Util.estaVazio(resp.toString())) {
				ajustarManual(resp);
			}
		}

		private void ajustarManual(Object resp) {
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
				if (frame.isVisible()) {
					int xl = frame.getX() + frame.getWidth();
					int ya = frame.getY() + frame.getHeight();
					if (xl > largura) {
						largura = xl;
					}
					if (ya > altura) {
						altura = ya;
					}
					frame.moveToFront();
				}
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

	@Override
	public void tabActivatedHandler(Fichario fichario) {
	}

	@Override
	public void windowActivatedHandler(Window window) {
	}

	@Override
	public void windowClosingHandler(Window window) {
	}

	@Override
	public void windowOpenedHandler(Window window) {
	}

	public boolean isAjusteLarguraForm() {
		return ajusteLarguraForm;
	}

	public void setAjusteLarguraForm(boolean ajusteLarguraForm) {
		this.ajusteLarguraForm = ajusteLarguraForm;
	}
}