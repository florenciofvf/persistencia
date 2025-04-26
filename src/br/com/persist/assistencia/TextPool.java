package br.com.persist.assistencia;

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

	private void add(String idStyle, String string) {
		if (string != null) {
			listaText.add(new Text(idStyle, string));
		}
	}
}