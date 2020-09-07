package br.com.persist.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractListModel;

public class ListaStringModelo extends AbstractListModel<String> {
	private static final long serialVersionUID = 1L;
	private final List<String> lista;

	public ListaStringModelo(Collection<String> collection) {
		lista = new ArrayList<>(collection);
	}

	@Override
	public int getSize() {
		return lista.size();
	}

	@Override
	public String getElementAt(int index) {
		return lista.get(index);
	}
}