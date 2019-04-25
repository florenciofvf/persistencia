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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import br.com.persist.banco.Conexao;
import br.com.persist.comp.Popup;
import br.com.persist.container.ObjetoContainer;
import br.com.persist.formulario.ObjetoContainerFormularioInterno;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Action;
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

	protected void larguraTotal() {
		int largura = getSize().width - 20;

		for (JInternalFrame frame : getAllFrames()) {
			frame.setLocation(0, frame.getY());
			frame.setSize(largura, frame.getHeight());
		}

		centralizar();
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

	protected void ajusteDimension() {
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

		setPreferredSize(new Dimension(largura, altura));
		SwingUtilities.updateComponentTreeUI(getParent());
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
		String novo = JOptionPane.showInputDialog(this, "Largura,Altura", string);

		if (Util.estaVazio(novo)) {
			return;
		}

		String[] strings = novo.split(",");

		if (strings != null && strings.length == 2) {
			try {
				int largura = Integer.parseInt(strings[0].trim());
				int altura = Integer.parseInt(strings[1].trim());

				setPreferredSize(new Dimension(largura, altura));
				SwingUtilities.updateComponentTreeUI(getParent());
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "ERRO", e);
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
		private Action centralAcao = Action.actionMenu("label.centralizar", Icones.CENTRALIZAR);
		private Action larTotalAcao = Action.actionMenu("label.largura_total", Icones.LARGURA);
		private Action distribuirAcao = Action.actionMenu("label.distribuir", Icones.LARGURA);
		private Action dimenAcao = Action.actionMenu("label.dimensao", Icones.RECT);

		DesktopPopup() {
			addMenuItem(larTotalAcao);
			addMenuItem(true, distribuirAcao);
			addMenuItem(true, centralAcao);
			addMenuItem(true, dimenAcao);

			eventos();
		}

		private void eventos() {
			distribuirAcao.setActionListener(e -> distribuir(0));
			larTotalAcao.setActionListener(e -> larguraTotal());
			dimenAcao.setActionListener(e -> ajusteDimension());
			centralAcao.setActionListener(e -> centralizar());
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
}