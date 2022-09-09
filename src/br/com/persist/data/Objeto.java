package br.com.persist.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Objeto implements Tipo {
	private final List<Par> atributos;

	public Objeto() {
		atributos = new ArrayList<>();
	}

	public List<Par> getAtributos() {
		return atributos;
	}

	public void addAtributo(String nome, Tipo valor) {
		if (getAtributo(nome) == null) {
			atributos.add(new Par(nome, valor));
		}
	}

	public Tipo getAtributo(String nome) {
		for (Par par : atributos) {
			if (par.nome.equals(nome)) {
				return par.valor;
			}
		}
		return null;
	}

	class Par {
		final String nome;
		final Tipo valor;

		Par(String nome, Tipo valor) {
			this.nome = Objects.requireNonNull(nome);
			this.valor = Objects.requireNonNull(valor);
		}
	}
}