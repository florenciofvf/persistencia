package br.com.persist.plugins.objeto;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.TextPool;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLException;
import br.com.persist.marca.XMLUtil;
import br.com.persist.plugins.instrucao.biblionativo.Lista;
import br.com.persist.plugins.objeto.vinculo.ArquivoVinculo;
import br.com.persist.plugins.objeto.vinculo.Pesquisa;
import br.com.persist.plugins.objeto.vinculo.Vinculacao;
import br.com.persist.plugins.variaveis.VariavelProvedor;

public class ObjetoUtil {
	private ObjetoUtil() {
	}

	public static Lista criarLista(Objeto... objetos) {
		Lista lista = new Lista();
		if (objetos != null) {
			for (Objeto item : objetos) {
				lista.add(item);
			}
		}
		return lista;
	}

	public static Lista criarLista(List<Objeto> objetos) {
		return criarLista(objetos.toArray(new Objeto[0]));
	}

	public static String getSelBiblio(Component comp, String string) {
		String[] strings = string.split(",");
		if (strings.length == 1) {
			return string;
		}
		Object resp = Util.getValorInputDialogSelect(comp, strings);
		return resp == null ? null : resp.toString();
	}

	public static String getStringResposta(List<Object> lista) {
		if (lista == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (Object obj : lista) {
			if (obj == null) {
				sb.append("null\n");
				continue;
			}
			String string = obj.toString();
			if (string != null) {
				sb.append(new String(string.getBytes(), StandardCharsets.UTF_8) + "\n");
			}
		}
		if (sb.length() > 0) {
			sb.delete(sb.length() - 1, sb.length());
		}
		return sb.toString();
	}

	public static boolean isTextPool(List<Object> resp) {
		return resp != null && !resp.isEmpty() && resp.get(0) instanceof TextPool;
	}

	public static Map<String, List<String>> criarMapaCampoNomes(String string) {
		Map<String, List<String>> mapa = new HashMap<>();
		if (!Util.isEmpty(string)) {
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
			String campo = strings[0].trim().toLowerCase();
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
		if (!Util.isEmpty(string)) {
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
				instrucao = instrucao.replaceAll(Constantes.SEP + entry.getKey().toUpperCase() + Constantes.SEP, valor);
				instrucao = instrucao.replaceAll(Constantes.SEP + entry.getKey().toLowerCase() + Constantes.SEP, valor);
				instrucao = instrucao.replaceAll(Constantes.SEP + entry.getKey() + Constantes.SEP, valor);
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

	public static void abrirArquivoVinculado(Component componente, ArquivoVinculo av)
			throws XMLException, ObjetoException {
		if (av == null || !av.valido()) {
			return;
		}
		File file = av.getFile();
		if (!file.exists()) {
			Vinculacao.criarArquivoVinculado(av);
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

	public static Vinculacao getVinculacao(Component componente, ArquivoVinculo av, boolean criarSeInexistente)
			throws XMLException, ObjetoException {
		if (av != null && av.valido()) {
			File file = av.getFile();
			if (!file.exists()) {
				if (criarSeInexistente) {
					Vinculacao.criarArquivoVinculado(av);
				} else {
					return null;
				}
			}
			Vinculacao vinculacao = new Vinculacao();
			vinculacao.abrir(av, componente);
			return vinculacao;
		}
		return null;
	}

	public static String getHintTotalRefValidaManipul() {
		return ObjetoMensagens.getString("hint.pesquisa.total.ref_manipul");
	}
}