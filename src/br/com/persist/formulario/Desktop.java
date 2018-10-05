package br.com.persist.formulario;

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
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import br.com.persist.Objeto;
import br.com.persist.banco.Conexao;
import br.com.persist.comp.MenuItem;
import br.com.persist.comp.Popup;
import br.com.persist.objeto.FormularioInterno;
import br.com.persist.util.Acao;
import br.com.persist.util.Icones;
import br.com.persist.util.Util;

public class Desktop extends JDesktopPane {
	private static final long serialVersionUID = 1L;
	private DesktopPopup popup = new DesktopPopup();
	protected final Formulario formulario;

	public Desktop(Formulario formulario, boolean superficie) {
		if (!superficie) {
			addMouseListener(mouseAdapter);
		}
		new DropTarget(this, listener);
		this.formulario = formulario;
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

	protected void configDimension() {
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
			}
		}
	}

	private MouseAdapter mouseAdapter = new MouseAdapter() {
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

	private DropTargetListener listener = new DropTargetListener() {
		@Override
		public void dropActionChanged(DropTargetDragEvent e) {
			if (!validoSoltar(e)) {
				e.rejectDrag();
				return;
			}
		}

		@Override
		public void dragEnter(DropTargetDragEvent e) {
			if (!validoSoltar(e)) {
				e.rejectDrag();
				return;
			}
		}

		@Override
		public void dragOver(DropTargetDragEvent dtde) {
		}

		@Override
		public void dragExit(DropTargetEvent dte) {
		}

		@Override
		public void drop(DropTargetDropEvent e) {
			if (!validoSoltar(e)) {
				e.rejectDrop();
				return;
			}

			e.acceptDrop(DnDConstants.ACTION_COPY);
			Transferable transferable = e.getTransferable();

			if (transferable == null) {
				return;
			}

			DataFlavor[] flavors = transferable.getTransferDataFlavors();
			if (flavors == null || flavors.length < 1) {
				return;
			}

			DataFlavor flavor = flavors[0];

			if (Transferidor.flavor.equals(flavor)) {
				try {
					Object[] array = (Object[]) transferable.getTransferData(flavor);
					addForm(array, e.getLocation(), null);
				} catch (Exception ex) {
					Util.stackTraceAndMessage("SOLTAR OBJETO", ex, Desktop.this);
				}
			}

			e.dropComplete(true);
		}
	};

	public void addForm(Object[] array, Point point, Graphics g) {
		Dimension dimension = (Dimension) array[2];
		Conexao conexao = (Conexao) array[1];
		Objeto objeto = (Objeto) array[0];
		FormularioInterno form = new FormularioInterno(formulario, objeto, g != null ? g : getGraphics(), conexao);
		form.setLocation(point);
		form.setSize(dimension);
		add(form);
		try {
			form.setSelected(true);
		} catch (PropertyVetoException e) {
		}
	}

	private boolean validoSoltar(DropTargetDragEvent e) {
		return (e.getDropAction() & DnDConstants.ACTION_COPY) != 0;
	}

	private boolean validoSoltar(DropTargetDropEvent e) {
		return (e.getDropAction() & DnDConstants.ACTION_COPY) != 0;
	}

	private class DesktopPopup extends Popup {
		private static final long serialVersionUID = 1L;
		MenuItem itemCentralizar = new MenuItem(new CentralizarAcao());
		MenuItem itemDimensoes = new MenuItem(new DimensaoAcao());

		DesktopPopup() {
			add(itemCentralizar);
			addSeparator();
			add(itemDimensoes);
		}
	}

	private class CentralizarAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public CentralizarAcao() {
			super(true, "label.centralizar", Icones.CENTRALIZAR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			centralizar();
		}
	}

	private class DimensaoAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public DimensaoAcao() {
			super(true, "label.dimensao", Icones.RECT);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			configDimension();
		}
	}
}