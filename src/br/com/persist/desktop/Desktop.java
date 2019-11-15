package br.com.persist.desktop;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

import br.com.persist.Metadado;
import br.com.persist.banco.Conexao;
import br.com.persist.comp.MenuItem;
import br.com.persist.comp.Popup;
import br.com.persist.container.ObjetoContainer;
import br.com.persist.formulario.ObjetoContainerFormularioInterno;
import br.com.persist.modelo.VariaveisModelo;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Action;
import br.com.persist.util.ChaveValor;
import br.com.persist.util.Constantes;
import br.com.persist.util.IIni;
import br.com.persist.util.BuscaAuto.Grupo;
import br.com.persist.util.BuscaAuto.Tabela;
import br.com.persist.util.Icones;
import br.com.persist.util.LinkAuto.Link;
import br.com.persist.util.Transferidor;
import br.com.persist.util.Util;

public class Desktop extends JDesktopPane implements IIni {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getGlobal();
	private DesktopPopup popup = new DesktopPopup();
	private boolean ajusteAutomatico = true;
	protected final Formulario formulario;
	private boolean abortarFecharComESC;

	public Desktop(Formulario formulario, boolean superficie) {
		if (!superficie) {
			addMouseListener(mouseAdapter);
		}
		new DropTarget(this, listener);
		this.formulario = formulario;
	}

	@Override
	public void ini(Graphics graphics) {
		JInternalFrame[] frames = getAllFrames();

		for (JInternalFrame frame : frames) {
			if (frame instanceof ObjetoContainerFormularioInterno) {
				ObjetoContainerFormularioInterno interno = (ObjetoContainerFormularioInterno) frame;
				interno.ini(graphics);
			}
		}
	}

	protected void alinharDireito() {
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

	protected void alinharEsquerdo() {
		JInternalFrame[] frames = getAllFrames();

		if (frames.length > 0) {
			int x = frames[0].getX();

			for (int i = 1; i < frames.length; i++) {
				frames[i].setLocation(x, frames[i].getY());
			}
		}
	}

	protected void mesmaLargura() {
		JInternalFrame[] frames = getAllFrames();

		if (frames.length > 0) {
			int largura = frames[0].getWidth();

			for (int i = 1; i < frames.length; i++) {
				frames[i].setSize(largura, frames[i].getHeight());
			}
		}
	}

	protected void larguraTotal(int tipo) {
		int largura = getSize().width - 20;

		for (JInternalFrame frame : getAllFrames()) {
			Dimension size = frame.getSize();
			Point local = frame.getLocation();

			if (tipo == 0) {
				frame.setLocation(0, local.y);
				frame.setSize(largura, size.height);

			} else if (tipo == 1) {
				frame.setSize(largura - local.x, size.height);

			} else if (tipo == 2) {
				int total = (local.x + size.width) - 10;
				frame.setSize(total, size.height);
				frame.setLocation(10, local.y);
			}
		}

		if (tipo == 0) {
			centralizar();
		}
	}

	protected void centralizar() {
		double largura = getSize().getWidth();

		for (JInternalFrame frame : getAllFrames()) {
			if (frame.getWidth() >= largura) {
				frame.setLocation(0, frame.getY());
			} else {
				frame.setLocation((int) ((largura - frame.getWidth()) / 2), frame.getY());
			}
		}
	}

	public void ajusteDimension() {
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
		}

		setPreferredSize(new Dimension(largura, altura + 41));
	}

	public void ajusteFormulario() {
		JInternalFrame[] frames = getAllFrames();

		if (frames.length > 0) {
			boolean salvar = false;

			String chaveDeltaY = "DELTA_AJUSTE_FORM_DISTANCIA_VERTICAL";

			ChaveValor cvDeltaY = VariaveisModelo.get(chaveDeltaY);

			if (cvDeltaY == null) {
				cvDeltaY = new ChaveValor(chaveDeltaY, "40");
				VariaveisModelo.adicionar(cvDeltaY);
				salvar = true;
			}

			if (salvar) {
				VariaveisModelo.salvar();
				VariaveisModelo.inicializar();
			}

			Arrays.sort(frames, (o1, o2) -> o1.getY() - o2.getY());

			JInternalFrame frame = frames[0];
			int deltaY = cvDeltaY.getInteiro(40);
			int y = frame.getY() + frame.getHeight() + deltaY;

			for (int i = 1; i < frames.length; i++) {
				frame = frames[i];
				frame.setLocation(frame.getX(), y);
				y = frame.getY() + (frame.isIcon() ? 10 : frame.getHeight()) + deltaY;
			}
		}
	}

	public void distribuir(int delta) {
		int largura = (getSize().width - 20) + delta;
		int altura = 341;
		int y = 10;

		for (JInternalFrame frame : getAllFrames()) {
			frame.setSize(largura, altura);
			frame.setLocation(0, y);
			y += altura + 20;
		}

		centralizar();
		ajusteDimension();
	}

	protected void ajustarDimension() {
		String string = getWidth() + "," + getHeight();
		Object resp = Util.getValorInputDialog(this, "label.largura_altura", string, string);

		if (resp == null || Util.estaVazio(resp.toString())) {
			return;
		}

		String[] strings = resp.toString().split(",");

		if (strings != null && strings.length == 2) {
			try {
				int largura = Integer.parseInt(strings[0].trim());
				int altura = Integer.parseInt(strings[1].trim());

				setPreferredSize(new Dimension(largura, altura));
				SwingUtilities.updateComponentTreeUI(getParent());
			} catch (Exception e) {
				LOG.log(Level.SEVERE, Constantes.ERRO, e);
			}
		}
	}

	private transient MouseAdapter mouseAdapter = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger() && getAllFrames().length > 0) {
				popup.show(Desktop.this, e.getX(), e.getY());
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger() && getAllFrames().length > 0) {
				popup.show(Desktop.this, e.getX(), e.getY());
			}
		}
	};

	private transient DropTargetListener listener = new DropTargetListener() {
		@Override
		public void dropActionChanged(DropTargetDragEvent e) {
			if (!validoSoltar(e)) {
				e.rejectDrag();
			}
		}

		@Override
		public void dragEnter(DropTargetDragEvent e) {
			if (!validoSoltar(e)) {
				e.rejectDrag();
			}
		}

		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			LOG.log(Level.FINEST, "dragOver");
		}

		@Override
		public void dragExit(DropTargetEvent dte) {
			LOG.log(Level.FINEST, "dragExit");
		}

		@Override
		public void drop(DropTargetDropEvent e) {
			if (!validoSoltar(e)) {
				e.rejectDrop();
				return;
			}

			Transferable transferable = e.getTransferable();

			if (transferable == null) {
				return;
			}

			DataFlavor[] flavors = transferable.getTransferDataFlavors();
			if (flavors == null || flavors.length < 1) {
				return;
			}

			DataFlavor flavor = flavors[0];
			boolean completado = false;

			if (Transferidor.flavor.equals(flavor)) {
				try {
					Object[] array = (Object[]) transferable.getTransferData(flavor);
					Objeto objeto = (Objeto) array[Util.ARRAY_INDICE_OBJ];

					if (!contemReferencia(objeto)) {
						addForm(array, e.getLocation(), null, (String) array[Util.ARRAY_INDICE_APE], false);
						completado = true;
					}
				} catch (Exception ex) {
					Util.stackTraceAndMessage("SOLTAR OBJETO", ex, Desktop.this);
				}
			} else if (Metadado.flavor.equals(flavor)) {
				try {
					Metadado metadado = (Metadado) transferable.getTransferData(flavor);

					if (processadoMetadado(metadado, e.getLocation())) {
						completado = true;
					}
				} catch (Exception ex) {
					Util.stackTraceAndMessage("SOLTAR OBJETO", ex, Desktop.this);
				}
			}

			if (completado) {
				e.acceptDrop(DnDConstants.ACTION_COPY);
				e.dropComplete(true);
			} else {
				e.rejectDrop();
			}
		}

		private boolean validoSoltar(DropTargetDragEvent e) {
			return (e.getDropAction() & DnDConstants.ACTION_COPY) != 0;
		}

		private boolean validoSoltar(DropTargetDropEvent e) {
			return (e.getDropAction() & DnDConstants.ACTION_COPY) != 0;
		}
	};

	protected boolean contemReferencia(Objeto objeto) {
		return false;
	}

	protected boolean processadoMetadado(Metadado metadado, Point point) {
		return false;
	}

	public void addForm(Object[] array, Point point, Graphics g, String apelido, boolean buscaAuto) {
		Dimension dimension = (Dimension) array[Util.ARRAY_INDICE_DIM];
		Conexao conexao = (Conexao) array[Util.ARRAY_INDICE_CON];
		Objeto objeto = (Objeto) array[Util.ARRAY_INDICE_OBJ];

		if (g == null) {
			g = getGraphics();
		}

		ObjetoContainerFormularioInterno form = new ObjetoContainerFormularioInterno(formulario, conexao, objeto, g,
				buscaAuto);
		form.setAbortarFecharComESC(abortarFecharComESC);
		form.setApelido(apelido);
		form.setLocation(point);
		form.setSize(dimension);
		form.setVisible(true);

		add(form);
	}

	private class DesktopPopup extends Popup {
		private static final long serialVersionUID = 1L;
		private Action centralizarAcao = Action.actionMenu("label.centralizar", Icones.CENTRALIZAR);
		private Action larTotalEsqAcao = Action.actionMenu("label.largura_total_esq", Icones.ALINHA_ESQUERDO);
		private Action larTotalDirAcao = Action.actionMenu("label.largura_total_dir", Icones.ALINHA_DIREITO);
		private Action dimensaoAcao4 = Action.actionMenu("label.ajuste_formulario", Icones.RECT);
		private Action larTotalAcao = Action.actionMenu("label.largura_total", Icones.LARGURA);
		private Action distribuirAcao = Action.actionMenu("label.distribuir", Icones.LARGURA);
		private Action dimensaoAcao2 = Action.actionMenu("label.ajuste_objeto", Icones.RECT);
		private Action dimensaoAcao3 = Action.actionMenu("label.ajuste_form", Icones.RECT);
		private Action dimensaoAcao = Action.actionMenu("label.dimensao", Icones.RECT);
		private Action ajustarAcao = Action.actionMenu("label.ajustar", Icones.RECT);

		MenuItem itemCentralizar = new MenuItem(centralizarAcao);
		MenuItem itemDimensoes2 = new MenuItem(dimensaoAcao2);
		MenuItem itemDimensoes3 = new MenuItem(dimensaoAcao3);
		MenuItem itemDimensoes4 = new MenuItem(dimensaoAcao4);
		MenuItem itemDimensoes = new MenuItem(dimensaoAcao);
		MenuItem itemAjustes = new MenuItem(ajustarAcao);

		DesktopPopup() {
			addMenuItem(larTotalAcao);
			addMenuItem(larTotalDirAcao);
			addMenuItem(larTotalEsqAcao);
			addMenuItem(true, distribuirAcao);
			add(true, itemCentralizar);
			add(true, itemDimensoes4);
			add(itemDimensoes3);
			add(itemDimensoes2);
			add(itemDimensoes);
			add(true, itemAjustes);

			eventos();
		}

		private void eventos() {
			dimensaoAcao4.setActionListener(e -> ajusteObjetoFormulario(false));
			dimensaoAcao2.setActionListener(e -> ajusteObjetoFormulario(true));
			dimensaoAcao3.setActionListener(e -> ajusteFormulario());
			larTotalDirAcao.setActionListener(e -> larguraTotal(1));
			larTotalEsqAcao.setActionListener(e -> larguraTotal(2));
			ajustarAcao.setActionListener(e -> ajustarDimension());
			dimensaoAcao.setActionListener(e -> ajusteDimension());
			centralizarAcao.setActionListener(e -> centralizar());
			distribuirAcao.setActionListener(e -> distribuir(0));
			larTotalAcao.setActionListener(e -> larguraTotal(0));
		}
	}

	public void buscaAutomatica(Grupo grupo, String argumentos, ObjetoContainer container) {
		JInternalFrame[] frames = getAllFrames();

		for (JInternalFrame frame : frames) {
			if (frame instanceof ObjetoContainerFormularioInterno) {
				ObjetoContainerFormularioInterno interno = (ObjetoContainerFormularioInterno) frame;
				List<Tabela> tabelas = grupo.getTabelas();

				for (Tabela tabela : tabelas) {
					if (interno.ehTabela(tabela)) {
						interno.getObjetoContainer().getObjeto().setTabelaPesquisaAuto(tabela);
						interno.buscaAutomatica(tabela.getCampo(), argumentos);
						tabela.setProcessado(true);
					}
				}
			}
		}
	}

	public void linkAutomatico(Link link, String argumento, ObjetoContainer container) {
		JInternalFrame[] frames = getAllFrames();

		for (JInternalFrame frame : frames) {
			if (frame instanceof ObjetoContainerFormularioInterno) {
				ObjetoContainerFormularioInterno interno = (ObjetoContainerFormularioInterno) frame;
				List<br.com.persist.util.LinkAuto.Tabela> tabelas = link.getTabelas();

				for (br.com.persist.util.LinkAuto.Tabela tabela : tabelas) {
					if (interno.ehTabela(tabela)) {
						interno.linkAutomatico(tabela.getCampo(), argumento);
					}
				}
			}
		}
	}

	public boolean isAbortarFecharComESC() {
		return abortarFecharComESC;
	}

	public void setAbortarFecharComESC(boolean abortarFecharComESC) {
		this.abortarFecharComESC = abortarFecharComESC;
	}

	public void ajusteObjetoFormulario(boolean aoObjeto) {
		JInternalFrame[] frames = getAllFrames();

		boolean salvar = false;

		ChaveValor cvDeltaX = VariaveisModelo.get(Constantes.DELTA_X_AJUSTE_FORM_OBJETO);
		ChaveValor cvDeltaY = VariaveisModelo.get(Constantes.DELTA_Y_AJUSTE_FORM_OBJETO);

		if (cvDeltaX == null) {
			cvDeltaX = new ChaveValor(Constantes.DELTA_X_AJUSTE_FORM_OBJETO, "" + Constantes.TRINTA);
			VariaveisModelo.adicionar(cvDeltaX);
			salvar = true;
		}

		if (cvDeltaY == null) {
			cvDeltaY = new ChaveValor(Constantes.DELTA_Y_AJUSTE_FORM_OBJETO, "" + Constantes.TRINTA);
			VariaveisModelo.adicionar(cvDeltaY);
			salvar = true;
		}

		if (salvar) {
			VariaveisModelo.salvar();
			VariaveisModelo.inicializar();
		}

		for (JInternalFrame frame : frames) {
			if (frame instanceof ObjetoContainerFormularioInterno) {
				ObjetoContainerFormularioInterno interno = (ObjetoContainerFormularioInterno) frame;
				interno.ajusteObjetoFormulario(aoObjeto, cvDeltaX.getInteiro(Constantes.TRINTA),
						cvDeltaY.getInteiro(Constantes.TRINTA));
			}
		}

		repaint();
	}

	public boolean isAjusteAutomatico() {
		return ajusteAutomatico;
	}

	public void setAjusteAutomatico(boolean ajusteAutomatico) {
		this.ajusteAutomatico = ajusteAutomatico;
	}
}