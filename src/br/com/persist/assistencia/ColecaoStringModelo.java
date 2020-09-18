package br.com.persist.assistencia;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractListModel;

public class ColecaoStringModelo extends AbstractListModel<String> {
	private static final long serialVersionUID = 1L;
	private final List<String> strings;

	public ColecaoStringModelo(Set<String> strings) {
		this.strings = new ArrayList<>(strings);
	}

	@Override
	public int getSize() {
		return strings.size();
	}

	@Override
	public String getElementAt(int index) {
		return strings.get(index);
	}
}