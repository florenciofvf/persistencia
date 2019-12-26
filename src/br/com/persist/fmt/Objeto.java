package br.com.persist.fmt;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import br.com.persist.util.Constantes;

public class Objeto extends Valor {
	private final Map<String, Valor> atributos;

	public Objeto() {
		super("Objeto");
		atributos = new LinkedHashMap<>();
	}

	public Objeto atributo(String nome, Valor valor) {
		if (nome == null || nome.trim().isEmpty() || valor == null) {
			return this;
		}

		atributos.put(nome, valor);

		return this;
	}

	@Override
	public void fmt(StringBuilder sb, int tab) {
		sb.append(getTab(tab) + "{" + Constantes.QL);

		Iterator<Map.Entry<String, Valor>> it = atributos.entrySet().iterator();
		boolean virgula = false;

		while (it.hasNext()) {
			if (virgula) {
				sb.append("," + Constantes.QL);
			}

			Map.Entry<String, Valor> entry = it.next();
			sb.append(getTab(tab + 1) + citar(entry.getKey()) + ": ");

			Valor v = entry.getValue();
			v.fmt(sb, tab + 1);

			virgula = true;
		}

		sb.append(Constantes.QL + getTab(tab) + "}");
	}
}