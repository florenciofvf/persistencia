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
		List<Token> tokens = checagemToken.getTokens();
		TipoFuncao funcaoSelecionada = sentencaRaiz;
		for (int i = 0; i < tokens.size(); i++) {
			Token token = tokens.get(i);
			if (token.isParenteseIni()) {
				TipoAtomico atomico = (TipoAtomico) funcaoSelecionada.getUltimoParametro();
				TipoFuncao funcao = transformarEmFuncao(atomico);
				funcaoSelecionada.setUltimoParametro(funcao);
				funcaoSelecionada = funcao;
			} else if (token.isParenteseFim()) {
				funcaoSelecionada.encerrar();
				funcaoSelecionada = (TipoFuncao) funcaoSelecionada.getPai();
			} else if (token.isFuncaoInfixa()) {
			} else if (token.isAleatorios()) {
			} else if (token.isVirgula()) {
				funcaoSelecionada.preParametro();
			} else if (token.isBoolean() || token.isString() || token.isDouble() || token.isLong()) {
			} else {
				TipoAtomico atomico = Token.criarTipoAtomico(token);
				funcaoSelecionada.addParam(atomico);
			}
		}
		if (sentencaRaiz.getSentenca() == null) {
			throw new ChecagemException(ChecagemGramatica.class, "Sentenca invalida (vazia!) >>> " + bloco);
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
			throw new ChecagemException(ChecagemGramatica.class, "Classe nao encontrada >>> " + classe);
		}
		try {
			return (TipoFuncao) klass.newInstance();
		} catch (InstantiationException e) {
			throw new ChecagemException(ChecagemGramatica.class, "Classe nao pode ser instanciada >>> " + classe);
		} catch (IllegalAccessException e) {
			throw new ChecagemException(ChecagemGramatica.class, "Acesso ilegal ao instanciar classe >>> " + classe);
		}
	}
}