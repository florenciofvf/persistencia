package br.com.persist.plugins.checagem;

import java.util.ArrayList;
import java.util.List;

public class ChecagemGramatica {

	private ChecagemGramatica() {
	}

	public static void montarGramatica(String chaveSentencas, Checagem checagem) {
		List<String> sentencasString = lerSentencasString(chaveSentencas);
		List<Sentenca> sentencas = criarHierarquiaSentencas(sentencasString);
		checagem.map.put(chaveSentencas, sentencas);
	}

	private static List<String> lerSentencasString(String chaveSentencas) {
		return new ArrayList<>();
	}

	private static List<Sentenca> criarHierarquiaSentencas(List<String> sentencasString) {
		return new ArrayList<>();
	}

	public static void mapear(String arquivo) {
		// TODO
	}
}