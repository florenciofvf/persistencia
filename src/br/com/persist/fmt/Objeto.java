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

	public Objeto atributo(String nome, Object tipo) {
		if (tipo instanceof String) {
			return atributo(nome, (String) tipo);
		}

		if (tipo instanceof Boolean) {
			return atributo(nome, (Boolean) tipo);
		}

		if (tipo instanceof Number) {
			return atributo(nome, (Number) tipo);
		}

		return this;
	}

	public Map<String, Tipo> getAtributos() {
		return atributos;
	}

	public Map<String, String> getAtributosString() {
		Map<String, String> map = new LinkedHashMap<>();

		Iterator<Map.Entry<String, Tipo>> it = atributos.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<String, Tipo> entry = it.next();
			Tipo tipo = entry.getValue();

			if (tipo instanceof Texto) {
				map.put(entry.getKey(), tipo.toString());
			}
		}

		return map;
	}

	public Tipo getValor(String att) {
		return atributos.get(att);
	}

	public Objeto atributo(String nome, String tipo) {
		return atributo(nome, new Texto(tipo));
	}

	public Objeto atributo(String nome, Boolean tipo) {
		return atributo(nome, new Logico(tipo));
	}

	public Objeto atributo(String nome, Number tipo) {
		return atributo(nome, new Numero(tipo));
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