package br.com.persist.painel;

import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTabbedPane;

public class PainelFichario extends JTabbedPane {
	private static final long serialVersionUID = 7100686282883066124L;
	private static final Logger LOG = Logger.getGlobal();

	public PainelFichario() {
		DragSource dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, dge -> {
			Component c = getSelectedComponent();
			if (c instanceof PainelTransferable) {
				dge.startDrag(null, (Transferable) c, listenerArrasto);
			}
		});
	}

	public int getTotalAbas() {
		return getTabCount();
	}

	private transient DragSourceListener listenerArrasto = new DragSourceListener() {
		@Override
		public void dropActionChanged(DragSourceDragEvent dsde) {
			LOG.log(Level.FINEST, "dropActionChanged");
		}

		@Override
		public void dragEnter(DragSourceDragEvent dsde) {
			LOG.log(Level.FINEST, "dragEnter");
		}

		@Override
		public void dragOver(DragSourceDragEvent dsde) {
			LOG.log(Level.FINEST, "dragOver");
		}

		@Override
		public void dragExit(DragSourceEvent dse) {
			LOG.log(Level.FINEST, "dragExit");
		}

		@Override
		public void dragDropEnd(DragSourceDropEvent dsde) {
			if (dsde.getDropSuccess()) {
				Component c = getSelectedComponent();
				removeTabAt(getIndice(c));
			}
		}
	};

	public synchronized int getIndice(Component c) {
		for (int i = 0; i < getTabCount(); i++) {
			if (getComponentAt(i) == c) {
				return i;
			}
		}
		return -1;
	}
}