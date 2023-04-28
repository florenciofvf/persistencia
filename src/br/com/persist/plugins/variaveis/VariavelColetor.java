package br.com.persist.plugins.variaveis;

import java.util.ArrayList;
import java.util.List;

public class VariavelColetor {
	private List<Variavel> lista;

	public List<Variavel> getLista() {
		if (lista == null) {
			lista = new ArrayList<>();
		}
		return lista;
	}

	public void setLista(List<Variavel> lista) {
		this.lista = lista;
	}

	public boolean estaVazio() {
		return getLista().isEmpty();
	}

	public int size() {
		return getLista().size();
	}

	public Variavel get(int i) {
		return getLista().get(i);
	}
}