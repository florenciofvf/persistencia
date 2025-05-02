package br.com.persist.assistencia;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TextPool {
	private final List<Text> listaText;

	public TextPool() {
		listaText = new ArrayList<>();
	}

	public void init() {
		listaText.clear();
	}

	public List<Text> getListaText() {
		return listaText;
	}

	public void none(String string) {
		add("none", string);
	}

	public void info(String string) {
		add("info", string);
	}

	public void warn(String string) {
		add("warn", string);
	}

	public void erro(String string) {
		add("erro", string);
	}

	public void nota(String string) {
		add("nota", string);
	}

	public void show(String string) {
		add("show", string);
	}

	private void add(String idStyle, String string) {
		if (string != null) {
			listaText.add(new Text(idStyle, new String(string.getBytes(), StandardCharsets.UTF_8)));
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < listaText.size(); i++) {
			Text item = listaText.get(i);
			sb.append(item.toString());
			if (i + 1 < listaText.size()) {
				sb.append(Constantes.QL);
			}
		}
		return sb.toString();
	}
}