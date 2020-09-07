package br.com.persist.abstrato;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

import br.com.persist.util.Constantes;

public abstract class AbstratoEditor extends JPanel implements TableCellEditor {
	private final transient List<CellEditorListener> listeners;
	private static final long serialVersionUID = 1L;
	private ChangeEvent changeEvent;
	private JTable tabela;
	private int linha;

	public AbstratoEditor() {
		changeEvent = new ChangeEvent(this);
		listeners = new ArrayList<>();
	}

	@Override
	public Object getCellEditorValue() {
		return null;
	}

	@Override
	public boolean isCellEditable(EventObject evento) {
		if (evento instanceof MouseEvent) {
			return ((MouseEvent) evento).getClickCount() >= Constantes.DOIS;
		}

		return false;
	}

	@Override
	public boolean shouldSelectCell(EventObject evento) {
		if (evento instanceof MouseEvent) {
			MouseEvent mouseEvent = (MouseEvent) evento;

			if (mouseEvent.getClickCount() >= Constantes.DOIS && tabela != null) {
				abrirModalEdicaoValor(tabela, linha);
				cancelCellEditing();
			}
		}

		return false;
	}

	public abstract void abrirModalEdicaoValor(JTable table, int row);

	@Override
	public boolean stopCellEditing() {
		List<CellEditorListener> lista = new ArrayList<>(listeners);

		for (CellEditorListener listener : lista) {
			listener.editingStopped(changeEvent);
		}

		return true;
	}

	@Override
	public void cancelCellEditing() {
		List<CellEditorListener> lista = new ArrayList<>(listeners);

		for (CellEditorListener listener : lista) {
			listener.editingCanceled(changeEvent);
		}
	}

	@Override
	public void addCellEditorListener(CellEditorListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeCellEditorListener(CellEditorListener listener) {
		listeners.remove(listener);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		this.tabela = table;
		this.linha = row;
		return this;
	}
}