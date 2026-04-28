package br.com.persist.plugins.expressao.invocacao;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblioteca.Biblioteca;
import br.com.persist.plugins.expressao.biblioteca.CacheBiblioteca;
import br.com.persist.plugins.expressao.biblioteca.LinkBibliotecaContexto;
import br.com.persist.plugins.expressao.compilador.Context;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Doc;
import br.com.persist.plugins.expressao.compilador.Indexador;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.Token.Tipo;
import br.com.persist.plugins.expressao.funcao.IFuncaoContexto;
import br.com.persist.plugins.expressao.compilador.TokenManager;
import br.com.persist.plugins.expressao.constante.ConstanteContexto;
import br.com.persist.plugins.expressao.instrucoes.ExpressaoContexto;
import br.com.persist.plugins.expressao.organiza.AliasContexto;
import br.com.persist.plugins.expressao.processador.Funcao;

public class InvocacaoContexto extends Contexto implements LinkBibliotecaContexto {
	public static final String INVOKE_PARAM_CRET = "invoke_param_cret";
	public static final String INVOKE_PARAM_VOID = "invoke_param_void";
	public static final String INVOKE_CRET = "invoke_cret";
	public static final String INVOKE_VOID = "invoke_void";
	private ArgumentosContexto argumentos;
	private boolean comRetorno;
	private boolean processado;

	public InvocacaoContexto(Token token, boolean comRetorno) {
		super(token);
		this.comRetorno = comRetorno;
	}

	@Override
	public void ajusteChavesEInvocacoesPre(Map<String, AliasContexto> mapaAlias, CacheBiblioteca cache)
			throws ExpressaoException {
		if (processado) {
			return;
		}
		String chamada = token.getString();
		String[] array = chamada.split("\\.");
		processarChave(chamada, array);
		processarChave2(chamada, array, mapaAlias, cache);
		processarChaveN(chamada, array, cache);
		processado = true;
	}

	@Override
	public void processarChave(String chamada, String[] array) {
		if (array.length != 1) {
			return;
		}
		AtomicBoolean sucesso = new AtomicBoolean();
		List<String> lista = checarSeEhParametroDeFuncao(chamada, sucesso);
		if (sucesso.get()) {
			setPrefixo(comRetorno ? INVOKE_PARAM_CRET : INVOKE_PARAM_VOID);
			setBiblio(montarString(lista, false));
			token.setStyle(Token.PARAMETRO);
			setMetodo(chamada);
			return;
		}
		IFuncaoContexto funcao = getBibliotecaContexto().getFuncao(chamada);
		if (funcao != null) {
			setPrefixo(comRetorno ? INVOKE_CRET : INVOKE_VOID);
			setBiblio(THIS);
			setMetodo(array[0]);
			return;
		}
		funcao = getFuncaoNomeOriginal(chamada);
		if (funcao != null) {
			setPrefixo(comRetorno ? INVOKE_CRET : INVOKE_VOID);
			setBiblio(THIS);
			setMetodo(funcao.getNome());
		} else {
			setPrefixo(ConstanteContexto.INVOKE_CONST);
			token.setStyle(Token.CONSTANTE);
			setBiblio(THIS);
			setMetodo(array[0]);
		}
	}

	@Override
	public void processarChave2(String chamada, String[] array, Map<String, AliasContexto> mapaAlias,
			CacheBiblioteca cache) throws ExpressaoException {
		if (array.length != 2) {
			return;
		}
		String alias = array[0];
		AliasContexto aliasContexto = mapaAlias.get(alias);
		if (aliasContexto == null) {
			throw new ExpressaoException("erro.alias.nao_mapeado", alias);
		}
		String string = aliasContexto.getBiblioteca() + "." + array[1];
		processarChaveN(string, string.split("\\."), cache);
	}

	@Override
	public void processarChaveN(String chamada, String[] array, CacheBiblioteca cache) throws ExpressaoException {
		if (array.length < 3) {
			return;
		}
		int pos = chamada.lastIndexOf(".");
		String nomeAbsoluto = chamada.substring(0, pos);
		String chave = chamada.substring(pos + 1);
		Biblioteca biblioteca = cache.getBiblioteca(nomeAbsoluto);
		Funcao funcao = biblioteca.getFuncao(chave);
		boolean comRetornoFuncao = !funcao.isTipoVoid();
		if (comRetornoFuncao != comRetorno) {
			throw new ExpressaoException("erro.invocacao.retorno", chamada, (funcao.isTipoVoid() ? "VOID" : "VALOR"));
		}
		setPrefixo(comRetorno ? INVOKE_CRET : INVOKE_VOID);
		setBiblio(nomeAbsoluto);
		setMetodo(chave);
	}

	public ArgumentosContexto getArgumentos() {
		return argumentos;
	}

	@Override
	protected void selecionadoVia(TokenManager tokenManager, Contexto contexto) throws ExpressaoException {
		if (contexto instanceof ArgumentosContexto && comRetorno) {
			tokenManager.selecionarParentDe(this);
		}
	}

	@Override
	protected void processarPre(TokenManager tokenManager, Token token) throws ExpressaoException {
		if (token.isAbreParentese()) {
			if (argumentos != null) {
				tokenManager.invalidar(token);
			} else {
				argumentos = new ArgumentosContexto();
				tokenManager.selecionar(argumentos);
				token.setConsumido(true);
				adicionar(argumentos);
			}
		} else if (token.isPontoEVirgula()) {
			if (argumentos == null) {
				tokenManager.invalidar(token);
			}
			if (comRetorno) {
				tokenManager.invalidar(token);
			}
			token.setConsumido(true);
			tokenManager.selecionarParentDe(this);
		} else {
			tokenManager.invalidar(token);
		}
	}

	@Context("invocar_funcao")
	@Doc({ "metodo();", "alias.mensagem(arg);", "br.com.teste.Biblioteca.mensagem(arg);" })
	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		throw new ExpressaoException("erro.processar.invocacao.estado");
	}

	@Override
	protected void empilharLocalPos(List<Contexto> lista) {
		lista.add(this);
		empilharLocalNegativo(lista);
	}

	@Override
	protected void listarPos(List<Contexto> lista) {
		lista.add(this);
		listarNegativo(lista);
	}

	@Override
	public void indexar(Indexador indexador) {
		indice = indexador.get2();
	}

	@Override
	public void salvar(PrintWriter pw) throws ExpressaoException {
		print(pw, getPrefixo(), getBiblio(), getMetodo());
	}

	public static InvocacaoContexto criarComEL(TokenManager tokenManager, String string) throws ExpressaoException {
		if (string.contains(":")) {
			Token token = null;
			if (string.endsWith("head")) {
				token = new Token("list.head", Tipo.VIRTUAL, -1);
			} else if (string.endsWith("tail")) {
				token = new Token("list.tail", Tipo.VIRTUAL, -1);
			} else {
				tokenManager.invalidar();
			}
			InvocacaoContexto invocacao = new InvocacaoContexto(token, true);
			ArgumentosContexto argumentos = ArgumentosContexto.criarComChave(getArg(string, ':'));
			invocacao.adicionar(argumentos);
			return invocacao;
		} else if (string.contains(".")) {
			Token token = new Token("map.get", Tipo.VIRTUAL, -1);
			InvocacaoContexto invocacao = new InvocacaoContexto(token, true);
			ArgumentosContexto argumentos = ArgumentosContexto.criar();
			invocacao.adicionar(argumentos);
			ExpressaoContexto mapa = ExpressaoContexto.criarComChave(getArg(string, '.'));
			argumentos.adicionar(mapa);
			ExpressaoContexto campo = ExpressaoContexto.criarComString(getArg2(string, '.'));
			argumentos.adicionar(campo);
			return invocacao;
		} else {
			tokenManager.invalidar();
		}
		return null;
	}

	private static String getArg(String string, char c) {
		int pos = string.indexOf(c);
		return string.substring(0, pos).trim();
	}

	private static String getArg2(String string, char c) {
		int pos = string.indexOf(c);
		return string.substring(pos + 1).trim();
	}

	public static InvocacaoContexto criarComEL(TokenManager tokenManager, String string, InvocacaoContexto argumento)
			throws ExpressaoException {
		if (string.contains(":")) {
			Token token = null;
			if (string.endsWith("head")) {
				token = new Token("list.head", Tipo.VIRTUAL, -1);
			} else if (string.endsWith("tail")) {
				token = new Token("list.tail", Tipo.VIRTUAL, -1);
			} else {
				tokenManager.invalidar();
			}
			InvocacaoContexto invocacao = new InvocacaoContexto(token, true);
			ArgumentosContexto argumentos = ArgumentosContexto.criar(argumento);
			invocacao.adicionar(argumentos);
			return invocacao;
		} else if (string.contains(".")) {
			Token token = new Token("map.get", Tipo.VIRTUAL, -1);
			InvocacaoContexto invocacao = new InvocacaoContexto(token, true);
			ArgumentosContexto argumentos = ArgumentosContexto.criar();
			invocacao.adicionar(argumentos);
			ExpressaoContexto mapa = ExpressaoContexto.criar(argumento);
			argumentos.adicionar(mapa);
			ExpressaoContexto campo = ExpressaoContexto.criarComString(getArg2(string, '.'));
			argumentos.adicionar(campo);
			return invocacao;
		} else {
			tokenManager.invalidar();
		}
		return null;
	}
}