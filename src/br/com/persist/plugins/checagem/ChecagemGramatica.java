package br.com.persist.plugins.checagem;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XML;
import br.com.persist.marca.XMLException;
import br.com.persist.marca.XMLUtil;

public class ChecagemGramatica {
	private static final Logger LOG = Logger.getGlobal();
	static Map<String, String> map = new HashMap<>();

	private ChecagemGramatica() {
	}

	public static void checarGramatica(String conteudo, Checagem checagem) throws XMLException, ChecagemException {
		List<Set> sentencas = sentencasString(conteudo);
		criarHierarquiaSentencas(sentencas);
	}

	public static void montarGramatica(String chaveSentencas, Checagem checagem) throws ChecagemException {
		List<Set> sentencas = sentencasFile(chaveSentencas);
		criarHierarquiaSentencas(sentencas);
		checagem.map.put(chaveSentencas, sentencas);
	}

	public static void atualizarGramatica(String chaveSentencas, String conteudo, Checagem checagem)
			throws ChecagemException, XMLException {
		List<Set> sentencas = sentencasString(conteudo);
		criarHierarquiaSentencas(sentencas);
		checagem.map.put(chaveSentencas, sentencas);
	}

	private static List<Set> sentencasFile(String chaveSentencas) {
		ChecagemHandler handler = new ChecagemHandler();
		try {
			File file = new File(ChecagemConstantes.CHECAGENS + Constantes.SEPARADOR + chaveSentencas);
			if (file.exists() && file.canRead()) {
				String conteudo = Util.conteudo(file);
				processarXMLSentencas(handler, conteudo);
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
		return handler.getSentencas();
	}

	private static List<Set> sentencasString(String conteudo) throws XMLException {
		ChecagemHandler handler = new ChecagemHandler();
		processarXMLSentencas(handler, conteudo);
		return handler.getSentencas();
	}

	private static void processarXMLSentencas(ChecagemHandler handler, String conteudo) throws XMLException {
		StringWriter sw = new StringWriter();
		XMLUtil util = new XMLUtil(sw);
		util.prologo();
		util.abrirTag2("sentencas");
		util.print(conteudo).ql();
		util.finalizarTag("sentencas");
		util.close();
		XML.processar(new ByteArrayInputStream(sw.toString().getBytes()), handler);
	}

	private static void criarHierarquiaSentencas(List<Set> sentencas) throws ChecagemException {
		for (Set set : sentencas) {
			criarSentenca(set);
		}
	}

	private static void criarSentenca(Set set) throws ChecagemException {
		ChecagemToken checagemToken = new ChecagemToken(set.getString());
		SentencaRaiz sentencaRaiz = new SentencaRaiz();
		Token token = checagemToken.proximoToken();
		TipoFuncao funcaoSelecionada = sentencaRaiz;
		while (token != null) {
			if (token.isParenteseAbrir()) {
				TipoAtomico atomico = (TipoAtomico) funcaoSelecionada.getUltimoParametro();
				TipoFuncao funcao = transformarEmFuncao(atomico);
				funcaoSelecionada.setUltimoParametro(funcao);
				funcaoSelecionada = funcao;
			} else if (token.isParenteseFechar()) {
				funcaoSelecionada.encerrar();
				funcaoSelecionada = (TipoFuncao) funcaoSelecionada.getPai();
			} else if (token.isVirgula()) {
				funcaoSelecionada.preParametro();
			} else {
				TipoAtomico atomico = Token.criarTipoAtomico(token);
				funcaoSelecionada.addParam(atomico);
			}
			token = checagemToken.proximoToken();
		}
		if (sentencaRaiz.getSentenca() == null) {
			throw new ChecagemException("Sentenca invalida >>> " + set);
		}
		if (sentencaRaiz.getSentenca() instanceof TipoFuncao) {
			((TipoFuncao) sentencaRaiz.getSentenca()).checarEncerrar();
		}
		set.setSentenca(sentencaRaiz.getSentenca());
	}

	private static TipoFuncao transformarEmFuncao(TipoAtomico atomico) throws ChecagemException {
		String chave = atomico.getValorString().toLowerCase().trim();
		String classe = ChecagemGramatica.map.get(chave);
		if (classe == null) {
			throw new ChecagemException("Nenhuma sentenca mapeada para >>> " + chave);
		}
		Class<?> klass = null;
		try {
			klass = Class.forName(classe);
		} catch (ClassNotFoundException e) {
			throw new ChecagemException("Classe nao encontrada >>> " + classe);
		}
		try {
			return (TipoFuncao) klass.newInstance();
		} catch (InstantiationException e) {
			throw new ChecagemException("Classe nao pode ser instanciada >>> " + classe);
		} catch (IllegalAccessException e) {
			throw new ChecagemException("Acesso ilegal ao instanciar classe >>> " + classe);
		}
	}

	public static void mapear(String arquivo) throws IOException, ClassNotFoundException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo)))) {
			String string = br.readLine();
			String chave = null;
			while (string != null) {
				if (!Util.estaVazio(string)) {
					if (Util.estaVazio(chave)) {
						chave = string.toLowerCase().trim();
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