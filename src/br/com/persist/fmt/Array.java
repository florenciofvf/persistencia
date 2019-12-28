package br.com.persist.fmt;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.util.Constantes;

public class Array extends Tipo {
	private final List<Tipo> lista;

	public Array() {
		lista = new ArrayList<>();
	}

	public Array adicionar(Tipo tipo) {
		if (tipo == null) {
			throw new IllegalArgumentException();
		}

		lista.add(tipo);
		tipo.pai = this;

		return this;
	}

	public Array adicionar(Object tipo) {
		if (tipo instanceof String) {
			return adicionar((String) tipo);
		}

		if (tipo instanceof Boolean) {
			return adicionar((Boolean) tipo);
		}

		if (tipo instanceof Number) {
			return adicionar((Number) tipo);
		}

		return this;
	}

	public List<Tipo> getLista() {
		return lista;
	}

	public Tipo getValor(int i) {
		return lista.get(i);
	}

	public Array adicionar(String tipo) {
		return adicionar(new Texto(tipo));
	}

	public Array adicionar(Boolean tipo) {
		return adicionar(new Logico(tipo));
	}

	public Array adicionar(Number tipo) {
		return adicionar(new Numero(tipo));
	}

	@Override
	public void toString(StringBuilder sb, boolean comTab, int tab) {
		super.toString(sb, comTab, tab);
		sb.append("[" + Constantes.QL);

		for (int i = 0; i < lista.size(); i++) {
			if (i > 0) {
				sb.append("," + Constantes.QL);
			}

			Tipo t = lista.get(i);
			t.toString(sb, true, tab + 1);
		}

		sb.append(Constantes.QL + getTab(tab) + "]");
	}
}