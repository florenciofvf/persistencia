package br.com.persist.formulario;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JDesktopPane;

import br.com.persist.Objeto;
import br.com.persist.banco.Conexao;
import br.com.persist.objeto.FormularioInterno;
import br.com.persist.util.Util;

public class Desktop extends JDesktopPane {
	private static final long serialVersionUID = 1L;
	private final Formulario formulario;

	public Desktop(Formulario formulario) {
		new DropTarget(this, listener);
		this.formulario = formulario;
	}

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
					Conexao conexao = (Conexao) array[1];
					Objeto objeto = (Objeto) array[0];
					FormularioInterno form = new FormularioInterno(formulario, objeto, getGraphics(), conexao);
					add(form);
				} catch (Exception ex) {
					Util.stackTraceAndMessage("SOLTAR OBJETO", ex, Desktop.this);
				}
			}

			e.dropComplete(true);
		}
	};

	private boolean validoSoltar(DropTargetDragEvent e) {
		return (e.getDropAction() & DnDConstants.ACTION_COPY) != 0;
	}

	private boolean validoSoltar(DropTargetDropEvent e) {
		return (e.getDropAction() & DnDConstants.ACTION_COPY) != 0;
	}
}