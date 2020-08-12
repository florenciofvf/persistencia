package br.com.persist.banco;

import java.awt.Component;
import java.sql.Connection;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import br.com.persist.icone.Icones;
import br.com.persist.util.Util;

public class ConexaoStatusRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	public ConexaoStatusRenderer() {
		setHorizontalAlignment(CENTER);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		TableModel model = table.getModel();

		if (model instanceof ConexaoModelo) {
			ConexaoModelo modelo = (ConexaoModelo) model;
			Conexao conexao = modelo.getConexao(row);

			try {
				Connection conn = Conexao.get(conexao);

				if (conn == null || conn.isClosed()) {
					setIcon(Icones.BANCO_DESCONECTA);
				} else {
					setIcon(Icones.CONECTA);
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage("ERRO STATUS CONN", ex, this);
			}
		}

		return this;
	}
}