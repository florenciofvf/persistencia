package br.com.persist.dialogo;

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
import javax.swing.table.TableModel;

import br.com.persist.banco.Conexao;
import br.com.persist.modelo.ConexaoModelo;
import br.com.persist.util.Util;

public class ConexaoStatusEditor extends JPanel implements TableCellEditor {
	private static final long serialVersionUID = 1L;
	private final List<CellEditorListener> listeners;
	public static final int TOTAL_CLICKS = 1;
	private ChangeEvent changeEvent;
	private JTable tabela;
	private int linha;

	public ConexaoStatusEditor() {
		listeners = new ArrayList<>();
		changeEvent = new ChangeEvent(this);
	}

	@Override
	public Object getCellEditorValue() {
		return null;
	}

	@Override
	public boolean isCellEditable(EventObject evento) {
		if (evento instanceof MouseEvent) {
			return ((MouseEvent) evento).getClickCount() >= TOTAL_CLICKS;
		}

		return false;
	}

	@Override
	public boolean shouldSelectCell(EventObject evento) {
		if (evento instanceof MouseEvent) {
			MouseEvent mouseEvent = (MouseEvent) evento;

			if (mouseEvent.getClickCount() >= TOTAL_CLICKS) {
				if (tabela != null) {
					TableModel model = tabela.getModel();

					if (model instanceof ConexaoModelo) {
						ConexaoModelo modelo = (ConexaoModelo) model;
						Conexao conexao = modelo.getConexao(linha);

						try {
							Conexao.getConnection2(conexao);
						} catch (Exception ex) {
							Util.stackTraceAndMessage("ERRO", ex, tabela);
						}
					}

					stopCellEditing();
				}
			}
		}

		return false;
	}

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