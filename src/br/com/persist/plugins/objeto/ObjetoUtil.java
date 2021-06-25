package br.com.persist.plugins.objeto;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLException;
import br.com.persist.marca.XMLUtil;
import br.com.persist.plugins.objeto.vinculo.Pesquisa;
import br.com.persist.plugins.objeto.vinculo.Vinculacao;
import br.com.persist.plugins.variaveis.VariavelProvedor;

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
		return criarMapaSequencias(string);
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

	public static String substituir(String instrucao, Map<String, List<String>> mapaChaveValor) {
		if (instrucao == null) {
			return null;
		}
		if (mapaChaveValor != null) {
			Iterator<Map.Entry<String, List<String>>> it = mapaChaveValor.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, List<String>> entry = it.next();
				String valor = convert(entry.getValue());
				instrucao = instrucao.replaceAll("#" + entry.getKey().toUpperCase() + "#", valor);
				instrucao = instrucao.replaceAll("#" + entry.getKey().toLowerCase() + "#", valor);
				instrucao = instrucao.replaceAll("#" + entry.getKey() + "#", valor);
			}
		}
		return VariavelProvedor.substituir(instrucao);
	}

	private static String convert(List<String> lista) {
		StringBuilder sb = new StringBuilder();
		if (!lista.isEmpty()) {
			sb.append(lista.get(0));
			for (int i = 1; i < lista.size(); i++) {
				sb.append(", ");
				sb.append(lista.get(i));
			}
		}
		return sb.toString();
	}

	public static void abrirArquivoVinculado(Component componente, File file) throws XMLException {
		if (file == null) {
			return;
		}
		if (!file.exists()) {
			Vinculacao.criarArquivoVinculado(file);
		}
		try {
			Util.conteudo(componente, file);
		} catch (IOException e) {
			Util.mensagem(componente, e.getMessage());
		}
	}

	public static String getDescricao(Pesquisa pesquisa) throws XMLException {
		StringWriter sw = new StringWriter();
		XMLUtil util = new XMLUtil(sw);
		pesquisa.salvar(util, false);
		util.close();
		return sw.toString();
	}

	public static Vinculacao getVinculacao(Component componente, String arquivo, boolean criarSeInexistente)
			throws XMLException {
		if (arquivo != null) {
			File file = new File(arquivo);
			if (!file.exists()) {
				if (criarSeInexistente) {
					Vinculacao.criarArquivoVinculado(file);
				} else {
					return null;
				}
			}
			Vinculacao vinculacao = new Vinculacao();
			vinculacao.abrir(arquivo, componente);
			return vinculacao;
		}
		return null;
	}
}