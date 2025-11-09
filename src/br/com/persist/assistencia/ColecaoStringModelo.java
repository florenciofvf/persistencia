package br.com.persist.assistencia;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.swing.AbstractListModel;

public class ColecaoStringModelo extends AbstractListModel<String> {
	private static final long serialVersionUID = 1L;
	private final List<String> strings;

	public ColecaoStringModelo(List<String> strings) {
		this.strings = Objects.requireNonNull(strings);
	}

	public ColecaoStringModelo(Set<String> strings) {
		this.strings = new ArrayList<>(strings);
	}

	public List<String> getStrings() {
		return strings;
	}

	public void incluir(int i, String string) {
		strings.add(i, string);
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