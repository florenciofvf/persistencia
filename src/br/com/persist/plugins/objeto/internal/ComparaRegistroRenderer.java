package br.com.persist.plugins.objeto.internal;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.persistencia.Coluna;
import br.com.persist.plugins.persistencia.OrdenacaoModelo;
import br.com.persist.plugins.persistencia.tabela.TabelaPersistencia;

public class ComparaRegistroRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		TabelaPersistencia tabelaPersistencia = (TabelaPersistencia) table;
		OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
		OrdenacaoModelo backup = tabelaPersistencia.getModeloBackup();
		Coluna colunaModelo = modelo.getColuna(column);
		Coluna colunaBackup = backup.getColuna(colunaModelo.getNome());

		if (colunaBackup == null) {
			setBackground(Color.BLACK);
			setForeground(Color.WHITE);
			return this;
		} else {
			String strModelo = modelo.getValueAt(0, colunaModelo.getIndice()).toString();
			String strBackup = backup.getValueAt(0, colunaBackup.getIndice()).toString();
			if (!Util.isEmpty(strModelo) && Util.isEmpty(strBackup)) {
				setBackground(Color.GREEN);
				setForeground(Color.BLACK);
			} else if (Util.isEmpty(strModelo) && !Util.isEmpty(strBackup)) {
				setBackground(Color.RED);
				setForeground(Color.BLACK);
			} else if (!Util.isEmpty(strModelo) && !Util.isEmpty(strBackup) && !strModelo.equals(strBackup)) {
				setBackground(Color.ORANGE);
				setForeground(Color.BLACK);
			}
		}

		return this;
	}
}