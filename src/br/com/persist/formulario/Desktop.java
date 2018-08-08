package br.com.persist.formulario;

import java.awt.Dimension;
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
	private final Formulario formulario;

	public Desktop(Formulario formulario) {
		addMouseListener(mouseAdapter);
		new DropTarget(this, listener);
		this.formulario = formulario;
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

	private MouseAdapter mouseAdapter = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popup.show(Desktop.this, e.getX(), e.getY());
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger()) {
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
					addForm(array, e.getLocation());
				} catch (Exception ex) {
					Util.stackTraceAndMessage("SOLTAR OBJETO", ex, Desktop.this);
				}
			}

			e.dropComplete(true);
		}
	};

	public void addForm(Object[] array, Point point) {
		Dimension dimension = (Dimension) array[2];
		Conexao conexao = (Conexao) array[1];
		Objeto objeto = (Objeto) array[0];
		FormularioInterno form = new FormularioInterno(formulario, objeto, getGraphics(), conexao);
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

		DesktopPopup() {
			add(itemCentralizar);
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
}