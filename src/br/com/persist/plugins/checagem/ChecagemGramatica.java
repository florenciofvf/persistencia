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
import br.com.persist.plugins.checagem.util.Expressao;

public class ChecagemGramatica {
	static final Map<String, String> prefixas = new HashMap<>();
	static final Map<String, Class<?>> infixas = new HashMap<>();

	private ChecagemGramatica() {
	}

	static {
		infixas.put("+", Somar.class);
		infixas.put("-", Subtrair.class);
		infixas.put("*", Multiplicar.class);
		infixas.put("/", Dividir.class);
		infixas.put("%", Resto.class);

		infixas.put("=", Igual.class);
		infixas.put("<", Menor.class);
		infixas.put("<=", MenorIgual.class);
		infixas.put(">", Maior.class);
		infixas.put(">=", MaiorIgual.class);

		infixas.put("^", Oux.class);
		infixas.put("&&", E.class);
		infixas.put("||", Ou.class);
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
		boolean prefixa = false;
		for (Token token : tokens) {
			if (token.isFuncaoPrefixa()) {
				TipoFuncao funcao = criarFuncaoPrefixa(token);
				funcaoSelecionada.addParam(funcao);
				funcaoSelecionada = funcao;
				prefixa = true;
			} else if (token.isFuncaoInfixa()) {
				FuncaoBinariaInfixa funcao = criarFuncaoInfixa(token);
				Sentenca sentenca = funcaoSelecionada.excluirUltimoParametro();
				funcao.addParamOp0(sentenca);
				funcaoSelecionada.addParam(funcao);
				funcaoSelecionada = funcao;
			} else if (token.isParenteseIni()) {
				if (prefixa) {
					prefixa = false;
				} else {
					TipoFuncao funcao = new Expressao();
					funcaoSelecionada.addParam(funcao);
					funcaoSelecionada = funcao;
				}
			} else if (token.isParenteseFim()) {
				funcaoSelecionada.encerrar();
				funcaoSelecionada = funcaoSelecionada.getPai();
				funcaoSelecionada = selecionada(funcaoSelecionada);
			} else if (token.isVirgula()) {
				funcaoSelecionada.preParametro();
			} else if (ehTipoAtomico(token)) {
				TipoAtomico atomico = Token.criarTipoAtomico(token);
				funcaoSelecionada.addParam(atomico);
				funcaoSelecionada = selecionada(funcaoSelecionada);
			} else {
				throw new ChecagemException(ChecagemGramatica.class, "Token invalido >>> " + token);
			}
		}
		checagemFinal(bloco, sentencaRaiz);
		bloco.setSentenca(sentencaRaiz.getSentenca());
	}

	private static TipoFuncao selecionada(TipoFuncao funcao) {
		if (funcao instanceof FuncaoBinariaInfixa) {
			return funcao.getPai();
		}
		return funcao;
	}

	private static void checagemFinal(Bloco bloco, SentencaRaiz sentencaRaiz) throws ChecagemException {
		if (sentencaRaiz.getSentenca() == null) {
			throw new ChecagemException(ChecagemGramatica.class, "Sentenca vazia >>> " + bloco);
		}
		if (sentencaRaiz.getSentenca() instanceof TipoFuncao) {
			((TipoFuncao) sentencaRaiz.getSentenca()).checarEncerrar();
		}
	}

	private static boolean ehTipoAtomico(Token token) {
		return token.isVariavel() || token.isBoolean() || token.isString() || token.isDouble() || token.isLong();
	}

	private static TipoFuncao criarFuncaoPrefixa(Token token) throws ChecagemException {
		if (!token.isFuncaoPrefixa()) {
			throw new ChecagemException(ChecagemGramatica.class, "Nao eh funcao prefixa >>> " + token);
		}
		String chave = token.getValor().toString();
		String classe = prefixas.get(chave.toLowerCase());
		if (classe == null) {
			throw new ChecagemException(ChecagemGramatica.class,
					token.getIndice() + " <<< Funcao nao declarada >>> " + chave);
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
			throw new ChecagemException(ChecagemGramatica.class, "Classe nao pode ser instanciada >>> " + klass);
		} catch (IllegalAccessException e) {
			throw new ChecagemException(ChecagemGramatica.class, "Acesso ilegal ao instanciar classe >>> " + klass);
		}
	}

	private static FuncaoBinariaInfixa criarFuncaoInfixa(Token token) throws ChecagemException {
		if (!token.isFuncaoInfixa()) {
			throw new ChecagemException(ChecagemGramatica.class, "Nao eh funcao infixa >>> " + token);
		}
		String chave = token.getValor().toString();
		Class<?> klass = infixas.get(chave.toLowerCase());
		if (klass == null) {
			throw new ChecagemException(ChecagemGramatica.class,
					token.getIndice() + " <<< Classe nao mapeada para >>> " + chave);
		}
		try {
			return (FuncaoBinariaInfixa) klass.newInstance();
		} catch (InstantiationException e) {
			throw new ChecagemException(ChecagemGramatica.class, "Classe nao pode ser instanciada >>> " + klass);
		} catch (IllegalAccessException e) {
			throw new ChecagemException(ChecagemGramatica.class, "Acesso ilegal ao instanciar classe >>> " + klass);
		}
	}
}