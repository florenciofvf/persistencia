package br.com.persist.fmt;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import br.com.persist.util.Constantes;
import br.com.persist.util.Util;

public class Objeto extends Tipo {
	private final Map<String, Tipo> atributos;

	public Objeto() {
		atributos = new LinkedHashMap<>();
	}

	public Objeto atributo(String nome, Tipo tipo) {
		if (Util.estaVazio(nome) || tipo == null) {
			throw new IllegalArgumentException();
		}

		atributos.put(nome, tipo);
		tipo.pai = this;

		return this;
	}

	public Tipo getValor(String att) {
		return atributos.get(att);
	}

	public Objeto atributo(String nome, String valor) {
		return atributo(nome, new Texto(valor));
	}

	public Objeto atributo(String nome, Boolean valor) {
		return atributo(nome, new Logico(valor));
	}

	public Objeto atributo(String nome, Number valor) {
		return atributo(nome, new Numero(valor));
	}

	@Override
	public void toString(StringBuilder sb, boolean comTab, int tab) {
		super.toString(sb, comTab, tab);
		sb.append("{" + Constantes.QL);

		Iterator<Map.Entry<String, Tipo>> it = atributos.entrySet().iterator();
		boolean virgula = false;

		while (it.hasNext()) {
			if (virgula) {
				sb.append("," + Constantes.QL);
			}

			Map.Entry<String, Tipo> entry = it.next();
			sb.append(getTab(tab + 1) + citar(entry.getKey()) + ": ");

			Tipo t = entry.getValue();
			t.toString(sb, false, tab + 1);

			virgula = true;
		}

		sb.append(Constantes.QL + getTab(tab) + "}");
	}
}