package br.com.persist.plugins.checagem;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.checagem.comparacao.Igual;
import br.com.persist.plugins.checagem.comparacao.Maior;
import br.com.persist.plugins.checagem.comparacao.MaiorIgual;
import br.com.persist.plugins.checagem.comparacao.Menor;
import br.com.persist.plugins.checagem.comparacao.MenorIgual;
import br.com.persist.plugins.checagem.logico.E;
import br.com.persist.plugins.checagem.logico.Ou;
import br.com.persist.plugins.checagem.logico.Oux;
import br.com.persist.plugins.checagem.matematico.Dividir;
import br.com.persist.plugins.checagem.matematico.Multiplicar;
import br.com.persist.plugins.checagem.matematico.Resto;
import br.com.persist.plugins.checagem.matematico.Somar;
import br.com.persist.plugins.checagem.matematico.Subtrair;

public class ChecagemGramatica {
	static final Map<String, String> prefixas = new HashMap<>();
	static final Map<String, String> infixas = new HashMap<>();

	private ChecagemGramatica() {
	}

	static {
		infixas.put("+", Somar.class.getName());
		infixas.put("-", Subtrair.class.getName());
		infixas.put("*", Multiplicar.class.getName());
		infixas.put("/", Dividir.class.getName());
		infixas.put("%", Resto.class.getName());

		infixas.put("=", Igual.class.getName());
		infixas.put("<", Menor.class.getName());
		infixas.put("<=", MenorIgual.class.getName());
		infixas.put(">", Maior.class.getName());
		infixas.put(">=", MaiorIgual.class.getName());

		infixas.put("^", Oux.class.getName());
		infixas.put("&&", E.class.getName());
		infixas.put("||", Ou.class.getName());
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
						prefixas.put(chave, string.trim());
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
		for (Token token : tokens) {
			if (token.isFuncaoPrefixa()) {
				//
			} else if (token.isFuncaoInfixa()) {
				//
			} else if (token.isParenteseIni()) {
				TipoAtomico atomico = (TipoAtomico) funcaoSelecionada.getUltimoParametro();
				TipoFuncao funcao = transformarEmFuncao(atomico);
				funcaoSelecionada.setUltimoParametro(funcao);
				funcaoSelecionada = funcao;
			} else if (token.isParenteseFim()) {
				funcaoSelecionada.encerrar();
				funcaoSelecionada = (TipoFuncao) funcaoSelecionada.getPai();
			} else if (token.isVariavel()) {
				//
			} else if (token.isVirgula()) {
				funcaoSelecionada.preParametro();
			} else if (token.isBoolean() || token.isString() || token.isDouble() || token.isLong()) {
				//
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

	private static TipoFuncao criarFuncao(Token token) throws ChecagemException {
		if(!token.isFuncaoPrefixa()) {
			throw new ChecagemException(ChecagemGramatica.class, "Nao eh funcao prefixa >>> " + token);
		}
		String chave = token.getValor().toString().toLowerCase();
		String classe = prefixas.get(chave);
		if (classe == null) {
			throw new ChecagemException(ChecagemGramatica.class, token.getIndice() + " <<< Funcao nao declarada >>> " + chave);
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