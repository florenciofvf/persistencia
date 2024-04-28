package br.com.persist.plugins.objeto.vinculo;

import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;

public class OrdenarManualDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final OrdenarManualContainer container;

	private OrdenarManualDialogo(String titulo, OrdenarListener listener) {
		super((Dialog) null, titulo);
		container = new OrdenarManualContainer(this, listener);
		setTitle(listener.getPesquisas().size() + " - " + getTitle());
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Component c, String titulo, OrdenarListener listener) {
		OrdenarManualDialogo dialog = new OrdenarManualDialogo(titulo, listener);
		dialog.pack();
		dialog.setLocationRelativeTo(Util.getViewParent(c));
		dialog.setVisible(true);
	}
}

class OrdenarManualContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final transient OrdenarListener listener;
	private final Toolbar toolbar = new Toolbar();

	public OrdenarManualContainer(Janela janela, OrdenarListener listener) {
		this.listener = Objects.requireNonNull(listener);
		toolbar.ini(janela);
		montarLayout();
	}

	private void montarLayout() {
		JTable table = new JTable(new OrdenarManualModelo(listener.getPesquisas()));
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(1).setCellRenderer(new OrdenarManualRenderer());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		add(BorderLayout.CENTER, new ScrollPane(table));
		add(BorderLayout.NORTH, toolbar);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, SALVAR);
		}

		@Override
		protected void salvar() {
			listener.salvar();
			fechar();
		}
	}
}

class OrdenarManualModelo extends AbstractTableModel {
	private static final String[] COLUNAS = { "ORDEM", "NOME" };
	private static final long serialVersionUID = 1L;
	private final transient List<Pesquisa> lista;

	public OrdenarManualModelo(List<Pesquisa> lista) {
		this.lista = lista == null ? new ArrayList<>() : lista;
	}

	@Override
	public int getRowCount() {
		return lista.size();
	}

	@Override
	public int getColumnCount() {
		return COLUNAS.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return COLUNAS[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Pesquisa pesquisa = lista.get(rowIndex);
		if (columnIndex == 0) {
			return String.valueOf(pesquisa.getOrdem());
		} else if (columnIndex == 1) {
			return pesquisa.getNomeParaMenuItem();
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (aValue == null) {
			return;
		}
		Pesquisa pesquisa = lista.get(rowIndex);
		if (columnIndex == 0) {
			String string = aValue.toString();
			if (!Util.isEmpty(string)) {
				pesquisa.setOrdem(Util.getInt(string, pesquisa.getOrdem()));
			}
		}
	}
}

class OrdenarManualRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		setBackground(Color.LIGHT_GRAY);
		return this;
	}
}