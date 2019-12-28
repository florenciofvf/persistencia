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

	public Tipo getValor(int i) {
		return lista.get(i);
	}

	public Array adicionar(String valor) {
		return adicionar(new Texto(valor));
	}

	public Array adicionar(Boolean valor) {
		return adicionar(new Logico(valor));
	}

	public Array adicionar(Number valor) {
		return adicionar(new Numero(valor));
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