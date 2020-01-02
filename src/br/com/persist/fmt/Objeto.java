package br.com.persist.fmt;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import br.com.persist.util.Constantes;
import br.com.persist.util.Util;

public class Objeto extends Tipo {
	private static final MutableAttributeSet att2;
	private static final MutableAttributeSet att;
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

	@Override
	public void toString(AbstractDocument doc, boolean comTab, int tab) throws BadLocationException {
		super.toString(doc, comTab, tab);
		insert(doc, "{" + Constantes.QL, att);

		Iterator<Map.Entry<String, Tipo>> it = atributos.entrySet().iterator();
		boolean virgula = false;

		while (it.hasNext()) {
			if (virgula) {
				insert(doc, "," + Constantes.QL, att);
			}

			Map.Entry<String, Tipo> entry = it.next();
			insert(doc, getTab(tab + 1) + citar(entry.getKey()) + ": ", att2);

			Tipo t = entry.getValue();
			t.toString(doc, false, tab + 1);

			virgula = true;
		}

		insert(doc, Constantes.QL + getTab(tab) + "}", att);
	}

	static {
		att2 = new SimpleAttributeSet();
		att = new SimpleAttributeSet();
		StyleConstants.setForeground(att, Color.black);
		StyleConstants.setForeground(att2, new Color(125, 0, 0));
	}
}