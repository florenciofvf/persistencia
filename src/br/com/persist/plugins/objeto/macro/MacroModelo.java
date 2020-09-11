package br.com.persist.plugins.objeto.macro;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

class MacroModelo implements ListModel<MacroProvedor.Instrucao> {
	private static final Logger LOG = Logger.getGlobal();

	@Override
	public int getSize() {
		return MacroProvedor.getInstrucoes().size();
	}

	@Override
	public MacroProvedor.Instrucao getElementAt(int index) {
		return MacroProvedor.getInstrucao(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		LOG.log(Level.FINEST, "addListDataListener");
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		LOG.log(Level.FINEST, "removeListDataListener");
	}
}