package br.com.persist.plugins.checagem;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.checagem.colecao.Lista;
import br.com.persist.plugins.checagem.colecao.Mapa;
import br.com.persist.plugins.checagem.comparacao.Igual;
import br.com.persist.plugins.checagem.comparacao.Maior;
import br.com.persist.plugins.checagem.comparacao.MaiorIgual;
import br.com.persist.plugins.checagem.comparacao.Menor;
import br.com.persist.plugins.checagem.comparacao.MenorIgual;
import br.com.persist.plugins.checagem.funcao.FuncaoBinariaInfixa;
import br.com.persist.plugins.checagem.logico.E;
import br.com.persist.plugins.checagem.logico.Nao;
import br.com.persist.plugins.checagem.logico.Ou;
import br.com.persist.plugins.checagem.logico.Oux;
import br.com.persist.plugins.checagem.matematico.Dividir;
import br.com.persist.plugins.checagem.matematico.Multiplicar;
import br.com.persist.plugins.checagem.matematico.Resto;
import br.com.persist.plugins.checagem.matematico.Somar;
import br.com.persist.plugins.checagem.matematico.Subtrair;
import br.com.persist.plugins.checagem.util.Expressao;

public class ChecagemGramatica {
	static final Map<String, Class<?>> infixas = new HashMap<>();
	static final Map<String, String> prefixas = new HashMap<>();
	static final Map<String, Class<?>> autos = new HashMap<>();

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

		autos.put("!", Nao.class);
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
		List<Token> tokens = checagemToken.getTokens(false);
		SentencaRaiz sentencaRaiz = new SentencaRaiz();
		AtomicBoolean prefixaSet = new AtomicBoolean();
		TipoFuncao funcaoSelecionada = sentencaRaiz;
		for (Token token : tokens) {
			funcaoSelecionada = processar(prefixaSet, funcaoSelecionada, token);
		}
		checagemFinal(bloco, sentencaRaiz);
		bloco.setSentenca(sentencaRaiz.getSentenca());
	}

	private static TipoFuncao processar(AtomicBoolean prefixaSet, TipoFuncao funcaoSelecionada, Token token)
			throws ChecagemException {
		if (token.isFuncaoPrefixa()) {
			funcaoSelecionada = vincular(funcaoSelecionada, criarFuncaoPrefixa(token));
			prefixaSet.set(true);
		} else if (token.isFuncaoInfixa()) {
			funcaoSelecionada = processarInfixa(funcaoSelecionada, criarFuncaoInfixa(token));
		} else if (token.isAuto()) {
			funcaoSelecionada = vincular(funcaoSelecionada, criarFuncaoAuto(token));
		} else if (token.isParenteseIni()) {
			if (prefixaSet.get()) {
				prefixaSet.set(false);
			} else {
				funcaoSelecionada = vincular(funcaoSelecionada, new Expressao(token.isNegarExpressao()));
			}
		} else if (token.isParenteseFim()) {
			funcaoSelecionada.encerrar();
			funcaoSelecionada = funcaoSelecionada.getPai();
			funcaoSelecionada = selecionada(funcaoSelecionada);
		} else if (token.isColcheteIni()) {
			funcaoSelecionada = vincular(funcaoSelecionada, new Lista());
		} else if (token.isChaveIni()) {
			funcaoSelecionada = vincular(funcaoSelecionada, new Mapa());
		} else if (token.isVirgula()) {
			funcaoSelecionada.preParametro();
		} else if (ehTipoAtomico(token)) {
			processarAtomico(funcaoSelecionada, token);
			funcaoSelecionada = selecionada(funcaoSelecionada);
		} else {
			throw new ChecagemException(ChecagemGramatica.class, "Token invalido >>> " + token);
		}
		return funcaoSelecionada;
	}

	private static void processarAtomico(TipoFuncao funcaoSelecionada, Token token) throws ChecagemException {
		TipoAtomico atomico = Token.criarTipoAtomico(token);
		funcaoSelecionada.addParam(atomico);
	}

	private static TipoFuncao vincular(TipoFuncao funcaoSelecionada, TipoFuncao funcao) throws ChecagemException {
		funcaoSelecionada.addParam(funcao);
		return funcao;
	}

	private static TipoFuncao processarInfixa(TipoFuncao funcaoSelecionada, FuncaoBinariaInfixa infixa)
			throws ChecagemException {
		Sentenca sentenca = funcaoSelecionada.getUltimoParametro();
		if (infixa.isPrioritario(sentenca)) {
			FuncaoBinariaInfixa anterior = (FuncaoBinariaInfixa) sentenca;
			sentenca = anterior.excluirUltimoParametro();
			infixa.addParamOp0(sentenca);
			anterior.addParam(infixa);
		} else {
			sentenca = funcaoSelecionada.excluirUltimoParametro();
			infixa.addParamOp0(sentenca);
			funcaoSelecionada.addParam(infixa);
		}
		return infixa;
	}

	private static TipoFuncao selecionada(TipoFuncao funcao) {
		while (funcao instanceof Auto || funcao instanceof FuncaoBinariaInfixa) {
			funcao = funcao.getPai();
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
			throw new ChecagemException(ChecagemGramatica.class,
					"Classe nao pode ser instanciada (prefixa) >>> " + klass);
		} catch (IllegalAccessException e) {
			throw new ChecagemException(ChecagemGramatica.class,
					"Acesso ilegal ao instanciar classe (prefixa) >>> " + klass);
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
			throw new ChecagemException(ChecagemGramatica.class,
					"Classe nao pode ser instanciada (infixa) >>> " + klass);
		} catch (IllegalAccessException e) {
			throw new ChecagemException(ChecagemGramatica.class,
					"Acesso ilegal ao instanciar classe (infixa) >>> " + klass);
		}
	}

	private static TipoFuncao criarFuncaoAuto(Token token) throws ChecagemException {
		if (!token.isAuto()) {
			throw new ChecagemException(ChecagemGramatica.class, "Nao eh funcao auto >>> " + token);
		}
		String chave = token.getValor().toString();
		Class<?> klass = autos.get(chave.toLowerCase());
		if (klass == null) {
			throw new ChecagemException(ChecagemGramatica.class,
					token.getIndice() + " <<< Classe nao mapeada para >>> " + chave);
		}
		try {
			return (TipoFuncao) klass.newInstance();
		} catch (InstantiationException e) {
			throw new ChecagemException(ChecagemGramatica.class, "Classe nao pode ser instanciada (auto) >>> " + klass);
		} catch (IllegalAccessException e) {
			throw new ChecagemException(ChecagemGramatica.class,
					"Acesso ilegal ao instanciar classe (auto) >>> " + klass);
		}
	}
}