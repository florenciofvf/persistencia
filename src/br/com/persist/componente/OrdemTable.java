package br.com.persist.componente;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import br.com.persist.assistencia.Constantes;

public class OrdemTable extends JTable {
	private static final long serialVersionUID = 1L;

	public OrdemTable(OrdemModel model) {
		super(model);
		tableHeader.addMouseListener(headerListenerInner);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}

	@Override
	public void setModel(TableModel dataModel) {
		super.setModel(dataModel);
		configHeader((OrdemModel) dataModel);
	}

	private void configHeader(OrdemModel model) {
		TableColumnModel columnModel = getColumnModel();
		int colunas = model.getColumnCount();
		for (int i = 0; i < colunas; i++) {
			TableColumn tableColumn = columnModel.getColumn(i);
			String nome = model.getColumnName(i);
			OrdemHeader header = new OrdemHeader(model, nome, i);
			tableColumn.setHeaderRenderer(header);
		}
	}

	private transient MouseListener headerListenerInner = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() >= Constantes.DOIS) {
				int tableColuna = columnAtPoint(e.getPoint());
				TableColumnModel columnModel = getColumnModel();
				TableColumn tableColumn = columnModel.getColumn(tableColuna);
				OrdemHeader header = (OrdemHeader) tableColumn.getHeaderRenderer();
				int resto = getResto(e.getX(), tableColumn);
				if (header.isOrdenacao(resto)) {
					header.ordenar();
				}
			}
		}

		private int getResto(int x, TableColumn tableColumn) {
			TableColumnModel columnModel = getColumnModel();
			int total = columnModel.getColumnCount();
			int soma = 0;
			for (int c = 0; c < total; c++) {
				TableColumn coluna = columnModel.getColumn(c);
				if (tableColumn == coluna) {
					return x - soma;
				}
				soma += coluna.getWidth();
			}
			return -1;
		}
	};
}