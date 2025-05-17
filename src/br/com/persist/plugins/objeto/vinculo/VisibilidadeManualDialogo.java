package br.com.persist.plugins.objeto.vinculo;

import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;

public class VisibilidadeManualDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final VisibilidadeManualContainer container;

	private VisibilidadeManualDialogo(String titulo, VisibilidadeListener listener) {
		super((Dialog) null, titulo);
		container = new VisibilidadeManualContainer(this, listener);
		setTitle(listener.getReferencias().size() + " - " + getTitle());
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Component c, String titulo, VisibilidadeListener listener) {
		VisibilidadeManualDialogo dialog = new VisibilidadeManualDialogo(titulo, listener);
		dialog.pack();
		dialog.setLocationRelativeTo(Util.getViewParent(c));
		dialog.setVisible(true);
	}
}

class VisibilidadeManualContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final transient VisibilidadeListener listener;
	private final Toolbar toolbar = new Toolbar();

	public VisibilidadeManualContainer(Janela janela, VisibilidadeListener listener) {
		this.listener = Objects.requireNonNull(listener);
		toolbar.ini(janela);
		montarLayout();
	}

	private void montarLayout() {
		JTable table = new JTable(new VisibilidadeManualModelo(listener.getReferencias()));
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

class VisibilidadeManualModelo extends AbstractTableModel {
	private static final Class<?>[] COLUNAS_CLASS = { String.class, Boolean.class };
	private static final String[] COLUNAS = { "TO_STRING", "VAZIO_INVISIVEL" };
	private static final long serialVersionUID = 1L;
	private final transient List<Referencia> lista;

	public VisibilidadeManualModelo(List<Referencia> lista) {
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
		return COLUNAS_CLASS[columnIndex];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Referencia item = lista.get(rowIndex);
		if (columnIndex == 0) {
			return item.toString();
		} else if (columnIndex == 1) {
			return item.isVazioInvisivel();
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (aValue == null) {
			return;
		}
		Referencia item = lista.get(rowIndex);
		if (columnIndex == 1) {
			String string = aValue.toString();
			if (!Util.isEmpty(string)) {
				item.setVazioInvisivel(Boolean.parseBoolean(string));
			}
		}
	}
}