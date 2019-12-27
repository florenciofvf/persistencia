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
		if (tipo != null) {
			lista.add(tipo);
		}

		return this;
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