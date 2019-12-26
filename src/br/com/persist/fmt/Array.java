package br.com.persist.fmt;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.util.Constantes;

public class Array extends Valor {
	private final List<Valor> lista;

	public Array() {
		super("Array");
		lista = new ArrayList<>();
	}

	public Array adicionar(Valor valor) {
		if (valor != null) {
			lista.add(valor);
		}

		return this;
	}

	@Override
	public void fmt(StringBuilder sb, int tab) {
		sb.append("[" + Constantes.QL);

		for (int i = 0; i < lista.size(); i++) {
			if (i > 0) {
				sb.append("," + Constantes.QL);
			}

			Valor v = lista.get(i);
			v.fmt(sb, tab + 1);
		}

		sb.append(Constantes.QL + getTab(tab) + "]");
	}
}