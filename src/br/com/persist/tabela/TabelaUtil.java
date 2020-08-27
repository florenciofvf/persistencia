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
import javax.swing.table.TableModel;

import br.com.persist.busca_auto.GrupoBuscaAuto;
import br.com.persist.busca_auto.TabelaBuscaAuto;
import br.com.persist.util.Coletor;
import br.com.persist.util.Constantes;
import br.com.persist.util.TransferidorDados;
import br.com.persist.util.Util;

public class TabelaUtil {

	private TabelaUtil() {
	}

	public static void ajustar(JTable table, Graphics graphics) {
		if (table == null || graphics == null) {
			return;
		}

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
			column.setPreferredWidth(largura + 40);
		}
	}

	public static int getIndiceColuna(Tabela tabela, String nome) {
		OrdenacaoModelo modelo = (OrdenacaoModelo) tabela.getModel();
		int qtdColunas = modelo.getColumnCount();

		for (int i = 0; i < qtdColunas; i++) {
			String coluna = modelo.getColumnName(i);

			if (coluna.equalsIgnoreCase(nome)) {
				return i;
			}
		}

		return -1;
	}

	public static List<String> getValoresColuna(Tabela tabela, int coluna) {
		OrdenacaoModelo modelo = (OrdenacaoModelo) tabela.getModel();
		List<Integer> linhas = getIndicesColuna(tabela);
		List<String> resposta = new ArrayList<>();

		for (int i : linhas) {
			Object obj = modelo.getValueAt(i, coluna);

			if (obj != null && !Util.estaVazio(obj.toString())) {
				resposta.add(obj.toString());
			}
		}

		return resposta;
	}

	public static List<Integer> getIndicesColuna(Tabela tabela) {
		OrdenacaoModelo modelo = (OrdenacaoModelo) tabela.getModel();
		List<Integer> resposta = new ArrayList<>();
		int[] linhas = tabela.getSelectedRows();
		int total = modelo.getRowCount();

		if (linhas == null || linhas.length == 0) {
			for (int i = 0; i < total; i++) {
				resposta.add(i);
			}
		} else {
			for (int i : linhas) {
				resposta.add(i);
			}
		}

		return resposta;
	}

	public static List<Integer> getIndices(JTable table) {
		List<Integer> resposta = new ArrayList<>();
		int[] linhas = table.getSelectedRows();
		int total = table.getRowCount();

		if (linhas == null || linhas.length == 0) {
			for (int i = 0; i < total; i++) {
				resposta.add(i);
			}
		} else {
			for (int i : linhas) {
				resposta.add(i);
			}
		}

		return resposta;
	}

	public static void checarColetores(Tabela tabela, int coluna, TabelaBuscaAuto tabelaBuscaAuto) {
		OrdenacaoModelo modelo = (OrdenacaoModelo) tabela.getModel();
		int total = modelo.getRowCount();

		for (int i = 0; i < total; i++) {
			Object obj = modelo.getValueAt(i, coluna);

			if (obj != null && !Util.estaVazio(obj.toString())) {
				tabelaBuscaAuto.checarColetores(obj.toString());
			}
		}
	}

	public static void atualizarLinhaColetores(Tabela tabela, int linha, int coluna, GrupoBuscaAuto grupo) {
		OrdenacaoModelo modelo = (OrdenacaoModelo) tabela.getModel();
		List<Object> registro = modelo.getRegistro(linha);
		String numero = registro.get(coluna).toString();
		StringBuilder sb = new StringBuilder();

		for (TabelaBuscaAuto t : grupo.getTabelas()) {
			Coletor coletor = t.getColetor(numero);

			if (coletor.getTotal() > 0) {
				sb.append(t.getNome() + " [" + coletor.getTotal() + "]   ");
			}
		}

		if (sb.length() > 0) {
			sb.delete(sb.length() - 3, sb.length());
		}

		registro.set(registro.size() - 1, sb.toString());
	}

	public static TransferidorDados getTransferidorDados(JTable table, List<Integer> indices) {
		if (table == null || indices == null) {
			return null;
		}

		TableModel model = table.getModel();

		if (model == null || model.getColumnCount() < 1 || model.getRowCount() < 1) {
			return null;
		}

		StringBuilder tabular = new StringBuilder();
		StringBuilder html = new StringBuilder();
		html.append("<html>").append(Constantes.QL2);
		html.append("<body>").append(Constantes.QL2);
		html.append("<table>").append(Constantes.QL2);
		html.append("<tr>").append(Constantes.QL2);

		int colunas = model.getColumnCount();

		for (int i = 0; i < colunas; i++) {
			String coluna = model.getColumnName(i);

			html.append("<th>" + coluna + "</th>").append(Constantes.QL2);
			tabular.append(coluna + Constantes.TAB);
		}

		html.append("</tr>").append(Constantes.QL2);
		tabular.deleteCharAt(tabular.length() - 1);
		tabular.append(Constantes.QL2);

		for (Integer i : indices) {
			html.append("<tr>").append(Constantes.QL2);

			for (int j = 0; j < colunas; j++) {
				Object obj = model.getValueAt(i, j);
				String val = obj == null ? Constantes.VAZIO : obj.toString();

				tabular.append(val + Constantes.TAB);
				html.append("<td>" + val + "</td>");
				html.append(Constantes.QL2);
			}

			html.append("</tr>").append(Constantes.QL2);
			tabular.deleteCharAt(tabular.length() - 1);
			tabular.append(Constantes.QL2);
		}

		html.append("</table>").append(Constantes.QL2);
		html.append("</body>").append(Constantes.QL2);
		html.append("</html>");
		tabular.deleteCharAt(tabular.length() - 1);

		return new TransferidorDados(html.toString(), tabular.toString());
	}
}