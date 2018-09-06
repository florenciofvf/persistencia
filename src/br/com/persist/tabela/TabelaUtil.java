package br.com.persist.tabela;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import br.com.persist.modelo.OrdenacaoModelo;
import br.com.persist.util.Util;

public class TabelaUtil {
	private TabelaUtil() {
	}

	public static void ajustar(JTable table, Graphics graphics, int ajuste) {
		DefaultTableColumnModel columnModel = (DefaultTableColumnModel) table.getColumnModel();
		FontMetrics fontMetrics = graphics.getFontMetrics();

		for (int col = 0; col < table.getColumnCount(); col++) {
			String coluna = table.getColumnName(col);
			int largura = fontMetrics.stringWidth(coluna);

			for (int lin = 0; lin < table.getRowCount(); lin++) {
				TableCellRenderer renderer = table.getCellRenderer(lin, col);

				Component component = renderer.getTableCellRendererComponent(table, table.getValueAt(lin, col), false,
						false, lin, col);

				largura = Math.max(largura, component.getPreferredSize().width);
			}

			TableColumn column = columnModel.getColumn(col);
			column.setPreferredWidth(largura + ajuste);
		}
	}

	public static List<String> getValoresColuna(Tabela tabela, int coluna) {
		OrdenacaoModelo modelo = (OrdenacaoModelo) tabela.getModel();
		List<String> resposta = new ArrayList<>();
		int[] linhas = tabela.getSelectedRows();
		int total = modelo.getRowCount();

		for (int i = 0; i < total; i++) {
			Object obj = null;

			if (linhas == null || linhas.length == 0) {
				obj = modelo.getValueAt(i, coluna);

				if (obj != null && !Util.estaVazio(obj.toString())) {
					resposta.add(obj.toString());
				}
			} else {
				boolean contem = false;

				for (int j : linhas) {
					if (j == i) {
						contem = true;
					}
				}

				if (contem) {
					obj = modelo.getValueAt(i, coluna);

					if (obj != null && !Util.estaVazio(obj.toString())) {
						resposta.add(obj.toString());
					}
				}
			}
		}

		return resposta;
	}
}