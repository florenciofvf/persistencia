package br.com.persist.plugins.conexao;

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

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;

public class ConexaoEditorStatus extends JPanel implements TableCellEditor {
	private final transient List<CellEditorListener> listeners;
	private static final long serialVersionUID = 1L;
	public static final int TOTAL_CLICKS = 1;
	private ChangeEvent changeEvent;
	private JTable tabela;
	private int linha;

	public ConexaoEditorStatus() {
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

			if (mouseEvent.getClickCount() >= TOTAL_CLICKS && tabela != null) {
				TableModel model = tabela.getModel();

				if (model instanceof ConexaoModelo) {
					Conexao conexao = ConexaoProvedor.getConexao(linha);

					try {
						ConexaoProvedor.getConnection2(conexao);
					} catch (Exception ex) {
						Util.stackTraceAndMessage(Constantes.ERRO, ex, tabela);
					}
				}

				stopCellEditing();
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