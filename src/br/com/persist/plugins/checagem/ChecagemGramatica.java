package br.com.persist.plugins.checagem;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XML;
import br.com.persist.marca.XMLUtil;

public class ChecagemGramatica {
	private static Map<String, String> map = new HashMap<>();
	private static final Logger LOG = Logger.getGlobal();

	private ChecagemGramatica() {
	}

	public static void montarGramatica(String chaveSentencas, Checagem checagem) throws ChecagemException {
		List<String> sentencasString = lerSentencasString(chaveSentencas);
		List<Sentenca> sentencas = criarHierarquiaSentencas(sentencasString);
		checagem.map.put(chaveSentencas, sentencas);
	}

	private static List<String> lerSentencasString(String chaveSentencas) {
		ChecagemHandler handler = new ChecagemHandler();
		try {
			File file = new File(chaveSentencas);
			if (file.exists() && file.canRead()) {
				String conteudo = Util.conteudo(file);
				StringWriter sw = new StringWriter();
				XMLUtil util = new XMLUtil(sw);
				util.prologo();
				util.abrirTag2("sentencas");
				util.print(conteudo).ql();
				util.finalizarTag("sentencas");
				util.close();
				XML.processar(new ByteArrayInputStream(sw.toString().getBytes()), handler);
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
		return handler.getSentencas();
	}

	private static List<Sentenca> criarHierarquiaSentencas(List<String> sentencasString) throws ChecagemException {
		List<Sentenca> sentencas = new ArrayList<>();
		for (String set : sentencasString) {
			sentencas.add(criarSentenca(set));
		}
		return sentencas;
	}

	private static Sentenca criarSentenca(String set) throws ChecagemException {
		ChecagemToken checagemToken = new ChecagemToken(set);
		Sentenca selecionado = null;
		Sentenca raiz = null;
		Token token = checagemToken.proximoToken();
		while (token != null) {
			if (token.isParenteseAbrir()) {

			} else if (token.isParenteseFechar()) {

			}
			token = checagemToken.proximoToken();
		}
		if (raiz == null) {
			throw new ChecagemException("sentenca raiz null.");
		}
		return raiz;
	}

	public static void mapear(String arquivo) throws IOException, ClassNotFoundException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo)))) {
			String string = br.readLine();
			String chave = null;
			while (string != null) {
				if (!Util.estaVazio(string)) {
					if (Util.estaVazio(chave)) {
						chave = string.trim().toLowerCase();
					} else {
						Class.forName(string.trim());
						map.put(chave, string.trim());
						chave = null;
					}
				}
				string = br.readLine();
			}
		}
	}
}