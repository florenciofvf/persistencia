package br.com.persist.plugins.checagem;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.assistencia.Util;

public class ChecagemGramatica {
	static Map<String, String> map = new HashMap<>();

	private ChecagemGramatica() {
	}

	static void mapear(String arquivo) throws IOException, ClassNotFoundException {
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

	public static void criarHierarquiaSentencas(List<Bloco> blocos) throws ChecagemException {
		for (Bloco bloco : blocos) {
			criarSentenca(bloco);
		}
	}

	private static void criarSentenca(Bloco bloco) throws ChecagemException {
		ChecagemToken checagemToken = new ChecagemToken(bloco.getString());
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
			throw new ChecagemException("Sentenca invalida (vazia!)>>> " + bloco);
		}
		if (sentencaRaiz.getSentenca() instanceof TipoFuncao) {
			((TipoFuncao) sentencaRaiz.getSentenca()).checarEncerrar();
		}
		bloco.setSentenca(sentencaRaiz.getSentenca());
	}

	private static TipoFuncao transformarEmFuncao(TipoAtomico atomico) throws ChecagemException {
		String chave = atomico.getValorString().toLowerCase().trim();
		String classe = map.get(chave);
		if (classe == null) {
			return new FuncaoPadrao();
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
}