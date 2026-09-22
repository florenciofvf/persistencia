package br.com.persist.componente;

import javax.swing.JTable;
import javax.swing.table.TableModel;

public class Table extends JTable {
	public static final int AUTO_RESIZE_ALL_COLUMNS = JTable.AUTO_RESIZE_ALL_COLUMNS;
	private static final long serialVersionUID = 6564805930109491479L;
	public static final int AUTO_RESIZE_OFF = JTable.AUTO_RESIZE_OFF;

	public Table(TableModel dm) {
		super(dm);
	}
}