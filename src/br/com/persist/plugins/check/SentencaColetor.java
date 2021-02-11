package br.com.persist.plugins.check;

import java.util.ArrayList;
import java.util.List;

public class SentencaColetor {
	private final List<Sentenca> sentencas;

	public SentencaColetor() {
		sentencas = new ArrayList<>();
	}

	public void init() {
		sentencas.clear();
	}

	public List<Sentenca> getSentencas() {
		return sentencas;
	}
}