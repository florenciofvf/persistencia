package br.com.persist.plugins.expressao.invocacao;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.ExpressaoMensagens;
import br.com.persist.plugins.expressao.ExpressaoUtil;
import br.com.persist.plugins.expressao.biblioteca.Biblioteca;
import br.com.persist.plugins.expressao.biblioteca.CacheBiblioteca;
import br.com.persist.plugins.expressao.biblioteca.LinkBibliotecaContexto;
import br.com.persist.plugins.expressao.compilador.Context;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Doc;
import br.com.persist.plugins.expressao.compilador.Indexador;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.Token.Tipo;
import br.com.persist.plugins.expressao.compilador.TokenManager;
import br.com.persist.plugins.expressao.constante.ConstanteContexto;
import br.com.persist.plugins.expressao.funcao.FuncaoContexto;
import br.com.persist.plugins.expressao.funcao.IFuncaoContexto;
import br.com.persist.plugins.expressao.instrucoes.ExpressaoContexto;
import br.com.persist.plugins.expressao.local.LocalContexto;
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
		FuncaoContexto funcaoContexto = ExpressaoUtil.getFuncaoContexto(this);

		LocalContexto localContexto = funcaoContexto.getLocalContexto(chamada);
		if (localContexto != null) {
			setPrefixo(LocalContexto.INVOKE_LOCAL);
			setBiblio(funcaoContexto.getNome());
			token.setStyle(Token.DEC_LOCAL);
			setMetodo(chamada);
			return;
		}

		List<String> lista = getHierarquiaParametro(chamada);
		if (!lista.isEmpty()) {
			setPrefixo(comRetorno ? INVOKE_PARAM_CRET : INVOKE_PARAM_VOID);
			setBiblio(montarString(lista));
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
			return;
		}

		setPrefixo(ConstanteContexto.INVOKE_CONST);
		token.setStyle(Token.CONSTANTE);
		setBiblio(THIS);
		setMetodo(array[0]);
	}

	@Override
	public void processarChave2(String chamada, String[] array, Map<String, AliasContexto> mapaAlias,
			CacheBiblioteca cache) throws ExpressaoException {
		if (array.length != 2) {
			return;
		}
		String alias = array[0];
		String metodo = array[1];
		FuncaoContexto funcaoContexto = ExpressaoUtil.getFuncaoContexto(this);

		LocalContexto localContexto = funcaoContexto.getLocalContexto(alias);
		if (localContexto != null && localContexto.isDeclaracaoMapa()) {
			setPrefixo(LocalContexto.INVOKE_LOCAL_MAPA);
			setBiblio(alias);
			token.setStyle(Token.DEC_LOCAL);
			setMetodo(metodo);
			return;
		}

		ConstanteContexto constanteContexto = getBibliotecaContexto().getConstanteContexto(alias);
		if (constanteContexto != null && constanteContexto.isDeclaracaoMapa()) {
			setPrefixo(ConstanteContexto.INVOKE_CONST_MAPA);
			setBiblio(alias);
			token.setStyle(Token.CONSTANTE);
			setMetodo(metodo);
			return;
		}

		AliasContexto aliasContexto = mapaAlias.get(alias);
		if (aliasContexto == null) {
			throw new ExpressaoException("erro.alias.nao_mapeado", alias);
		}
		String string = aliasContexto.getBiblioteca() + "." + metodo;
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

	public static InvocacaoContexto criarComEL(TokenManager tokenManager, Token origem, String string)
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
			ArgumentosContexto argumentos = ArgumentosContexto.criarComChave(getArg(tokenManager, origem, string, ':'));
			invocacao.adicionar(argumentos);
			return invocacao;
		} else if (string.contains(".")) {
			Token token = new Token("map.get", Tipo.VIRTUAL, -1);
			InvocacaoContexto invocacao = new InvocacaoContexto(token, true);
			ArgumentosContexto argumentos = ArgumentosContexto.criar();
			invocacao.adicionar(argumentos);
			ExpressaoContexto mapa = ExpressaoContexto.criarComChave(getArg(tokenManager, origem, string, '.'));
			argumentos.adicionar(mapa);
			ExpressaoContexto campo = ExpressaoContexto.criarComString(getArg2(tokenManager, origem, string, '.'));
			argumentos.adicionar(campo);
			return invocacao;
		} else {
			tokenManager.invalidar();
		}
		return null;
	}

	private static String getArg(TokenManager tokenManager, Token token, String string, char c) {
		int pos = string.indexOf(c);
		String prefixo = string.substring(0, pos).trim();
		if (c == ':' && prefixo.length() == 1) {
			tokenManager.addAlerta(ExpressaoMensagens.getString("alerta.lista.prefixo_minimo", token.getOriginal()));
		}
		return prefixo;
	}

	private static String getArg2(TokenManager tokenManager, Token token, String string, char c) {
		int pos = string.indexOf(c);
		String prefixo = string.substring(0, pos).trim();
		String sufixo = string.substring(pos + 1).trim();
		if (c == '.' && "head".equals(sufixo)) {
			tokenManager.addAlerta(ExpressaoMensagens.getString("alerta.mapa.sufixo_head", token.getOriginal()));
		}
		if (c == '.' && "tail".equals(sufixo)) {
			tokenManager.addAlerta(ExpressaoMensagens.getString("alerta.mapa.sufixo_tail", token.getOriginal()));
		}
		if (c == '.' && prefixo.length() == 1) {
			tokenManager.addAlerta(ExpressaoMensagens.getString("alerta.mapa.prefixo_minimo", token.getOriginal()));
		}
		return sufixo;
	}

	public static InvocacaoContexto criarComEL(TokenManager tokenManager, Token origem, String string,
			InvocacaoContexto argumento) throws ExpressaoException {
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
			ExpressaoContexto campo = ExpressaoContexto.criarComString(getArg2(tokenManager, origem, string, '.'));
			argumentos.adicionar(campo);
			return invocacao;
		} else {
			tokenManager.invalidar();
		}
		return null;
	}
}