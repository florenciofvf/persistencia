package br.com.persist.assistencia;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.swing.AbstractListModel;

public class ColecaoStringModelo extends AbstractListModel<String> {
	private static final long serialVersionUID = 1L;
	private final List<String> lista;

	public ColecaoStringModelo(List<String> strings) {
		this.lista = Objects.requireNonNull(strings);
	}

	public ColecaoStringModelo(Set<String> strings) {
		this.lista = new ArrayList<>(strings);
	}

	public List<String> getLista() {
		return lista;
	}

	public void incluir(int i, String string) {
		lista.add(i, string);
	}

	public void adicionar(String string) {
		lista.add(string);
	}

	public void excluir(int i) {
		lista.remove(i);
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