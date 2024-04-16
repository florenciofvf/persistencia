package br.com.persist.plugins.atributo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class AtributoModelo extends AbstractTableModel {
	private static final Class<?>[] CLASS_COLUNAS = { Boolean.class, String.class, String.class, String.class,
			String.class, Boolean.class };
	private static final String[] COLUNAS = { "IGNORAR", "NOME", "ROTULO", "CLASSE", "VIEW_TO_BACK", "PARSE_DATE" };
	private static final long serialVersionUID = 1L;
	private final transient List<Atributo> lista;

	public AtributoModelo() {
		this(null);
	}

	public AtributoModelo(List<Atributo> lista) {
		this.lista = lista == null ? new ArrayList<>() : lista;
	}

	public List<Atributo> getLista() {
		return lista;
	}

	public Atributo getAtributo(int i) {
		if (i >= 0 && i < lista.size()) {
			return lista.get(i);
		}
		return null;
	}

	public int adicionar(Atributo atributo) {
		if (atributo != null) {
			lista.add(atributo);
			return lista.size() - 1;
		}
		return -1;
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
		return CLASS_COLUNAS[columnIndex];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Atributo atributo = lista.get(rowIndex);
		if (columnIndex == 0) {
			return atributo.isIgnorar();
		} else if (columnIndex == 1) {
			return atributo.getNome();
		} else if (columnIndex == 2) {
			return atributo.getRotulo();
		} else if (columnIndex == 3) {
			return atributo.getClasse();
		} else if (columnIndex == 4) {
			return atributo.getViewToBack();
		} else if (columnIndex == 5) {
			return atributo.getParseDateBoolean();
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (aValue == null) {
			return;
		}
		Atributo atributo = lista.get(rowIndex);
		if (columnIndex == 0 && aValue instanceof Boolean) {
			atributo.setIgnorar(((Boolean) aValue).booleanValue());
		} else if (columnIndex == 1) {
			atributo.setNome(aValue.toString());
		} else if (columnIndex == 2) {
			atributo.setRotulo(aValue.toString());
		} else if (columnIndex == 3) {
			atributo.setClasse(aValue.toString());
		} else if (columnIndex == 4) {
			atributo.setViewToBack(aValue.toString());
		} else if (columnIndex == 5) {
			atributo.setParseDate(aValue.toString());
		}
	}
}