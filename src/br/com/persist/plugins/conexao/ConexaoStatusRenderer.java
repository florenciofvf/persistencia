package br.com.persist.plugins.conexao;

import java.awt.Component;
import java.sql.Connection;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import br.com.persist.util.Icones;
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
			Conexao conexao = ConexaoProvedor.getConexao(row);

			try {
				Connection conn = ConexaoProvedor.get(conexao);

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