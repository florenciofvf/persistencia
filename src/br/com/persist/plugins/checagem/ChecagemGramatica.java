package br.com.persist.plugins.checagem;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.assistencia.Util;

public class ChecagemGramatica {
	private static Map<String, String> map = new HashMap<>();

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

	public static void mapear(String arquivo) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo)))) {
			String string = br.readLine();
			String chave = null;
			while (string != null) {
				if (!Util.estaVazio(string)) {
					if (Util.estaVazio(chave)) {
						chave = string.trim().toLowerCase();
					} else {
						map.put(chave, string.trim());
					}
				}
				string = br.readLine();
			}
		}
	}
}