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
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XML;
import br.com.persist.marca.XMLUtil;
import br.com.persist.plugins.checagem.atom.SentencaRaiz;
import br.com.persist.plugins.checagem.atom.TipoBoolean;
import br.com.persist.plugins.checagem.atom.TipoDouble;
import br.com.persist.plugins.checagem.atom.TipoField;
import br.com.persist.plugins.checagem.atom.TipoLong;
import br.com.persist.plugins.checagem.atom.TipoString;

public class ChecagemGramatica {
	private static final Logger LOG = Logger.getGlobal();
	static Map<String, String> map = new HashMap<>();

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
		AtomicReference<Sentenca> selecionado = new AtomicReference<>();
		ChecagemToken checagemToken = new ChecagemToken(set);
		SentencaRaiz sentencaRaiz = new SentencaRaiz();
		Token token = checagemToken.proximoToken();
		selecionado.set(sentencaRaiz);
		while (token != null) {
			if (token.isParenteseFechar()) {
				Sentenca sel = selecionado.get();
				selecionado.set(sel.pai);
			} else if (token.isVirgula()) {
				Sentenca sel = selecionado.get();
				if (sel.parametros.isEmpty()) {
					throw new ChecagemException("sentenca vazia >>> " + sel);
				}
			} else {
				Sentenca sentenca = criarSentenca(token);
				sentenca.checarProximo(token, checagemToken.proximoToken(), selecionado);
			}
			token = checagemToken.proximoToken();
		}
		if (sentencaRaiz.parametros.size() != 1) {
			throw new ChecagemException("sentenca invalida >>> " + set);
		}
		return sentencaRaiz.param0();
	}

	private static Sentenca criarSentenca(Token token) throws ChecagemException {
		if (token.isParenteseAbrir() || token.isParenteseFechar() || token.isVirgula()) {
			throw new ChecagemException("funcao invalida >>>" + token.getValor());
		}
		if (token.isBoolean()) {
			TipoBoolean tipo = new TipoBoolean();
			tipo.setValor(new Boolean(token.getValor()));
			return tipo;
		}
		if (token.isDouble()) {
			TipoDouble tipo = new TipoDouble();
			tipo.setValor(new Double(token.getValor()));
			return tipo;
		}
		if (token.isLong()) {
			TipoLong tipo = new TipoLong();
			tipo.setValor(new Long(token.getValor()));
			return tipo;
		}
		if (token.isString()) {
			if (!Util.estaVazio(token.getValor()) && token.getValor().startsWith("$")) {
				TipoField tipo = new TipoField();
				tipo.setValor(token.getValor().substring(1));
				return tipo;
			}
			TipoString tipo = new TipoString();
			tipo.setValor(token.getValor());
			return tipo;
		}
		throw new ChecagemException("funcao invalida >>>" + token.getValor());
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