package br.com.persist.plugins.objeto;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.variaveis.VariavelProvedor;
import br.com.persist.util.Util;

public class ObjetoUtil {
	private ObjetoUtil() {
	}

	public static Map<String, List<String>> criarMapaCampoNomes(String string) {
		Map<String, List<String>> mapa = new HashMap<>();

		if (!Util.estaVazio(string)) {
			String[] strings = string.split(";");

			if (strings != null) {
				for (String s : strings) {
					aux(s, mapa);
				}
			}
		}

		return mapa;
	}

	private static void aux(String string, Map<String, List<String>> mapa) {
		String[] strings = string.split("=");

		if (strings != null && strings.length > 1) {
			String campo = strings[0].trim();

			List<String> lista = mapa.computeIfAbsent(campo, t -> new ArrayList<>());

			String nomes = strings[1];
			String[] strNomes = nomes.split(",");

			for (String nome : strNomes) {
				lista.add(nome.trim());
			}
		}
	}

	public static Map<String, String> criarMapaCampoChave(String string) {
		Map<String, String> mapa = new HashMap<>();

		if (!Util.estaVazio(string)) {
			String[] strings = string.split(";");

			if (strings != null) {
				for (String chaveValor : strings) {
					String[] stringsCV = chaveValor.split("=");

					if (stringsCV != null && stringsCV.length > 1) {
						mapa.put(stringsCV[0].trim(), stringsCV[1].trim());
					}
				}
			}
		}

		return mapa;
	}

	public static Map<String, String> criarMapaSequencias(String string) {
		Map<String, String> mapa = new HashMap<>();

		if (!Util.estaVazio(string)) {
			String[] strings = string.split(";");

			if (strings != null) {
				for (String chaveValor : strings) {
					String[] stringsCV = chaveValor.split("=");

					if (stringsCV != null && stringsCV.length > 1) {
						mapa.put(stringsCV[0].trim().toLowerCase(), stringsCV[1].trim());
					}
				}
			}
		}

		return mapa;
	}

	public static Object[] criarArray(Conexao conexao, Objeto objeto, String apelido) {
		return criarArray(conexao, objeto, new Dimension(400, 250), apelido);
	}

	public static Object[] criarArray(Conexao conexao, Objeto objeto, Dimension dimension, String apelido) {
		ObjetoSuperficie.setComplemento(conexao, objeto);
		return new Object[] { objeto, conexao, dimension, apelido };
	}

	public static String substituir(String instrucao, Map<String, String> mapaChaveValor) {
		if (instrucao == null) {
			return null;
		}

		if (mapaChaveValor == null) {
			mapaChaveValor = new HashMap<>();
		}

		Iterator<Map.Entry<String, String>> it = mapaChaveValor.entrySet().iterator();

		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			instrucao = instrucao.replaceAll("#" + entry.getKey() + "#", entry.getValue());
		}

		instrucao = VariavelProvedor.substituir(instrucao);
		return instrucao;
	}
}