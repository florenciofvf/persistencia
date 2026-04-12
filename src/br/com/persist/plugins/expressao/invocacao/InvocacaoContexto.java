package br.com.persist.plugins.expressao.invocacao;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblioteca.LinkBibliotecaContexto;
import br.com.persist.plugins.expressao.compilador.Context;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Doc;
import br.com.persist.plugins.expressao.compilador.Indexador;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.Token.Tipo;
import br.com.persist.plugins.expressao.compilador.TokenManager;
import br.com.persist.plugins.expressao.instrucoes.ExpressaoContexto;
import br.com.persist.plugins.expressao.organiza.AliasContexto;

public class InvocacaoContexto extends Contexto implements LinkBibliotecaContexto {
	public static final String INVOKE_CRET = "invoke_cret";
	public static final String INVOKE_VOID = "invoke_void";
	public static final String THIS = "this";
	private ArgumentosContexto argumentos;
	private boolean comRetorno;
	private String biblio;
	private String metodo;
	private String alias;

	public InvocacaoContexto(Token token, boolean comRetorno) {
		super(token);
		this.comRetorno = comRetorno;
		initLink();
	}

	public void initLink() {
		String chamada = token.getString();
		String[] array = chamada.split("\\.");
		if (array.length == 1) {
			biblio = THIS;
			metodo = array[0];
		} else if (array.length == 2) {
			alias = array[0];
			metodo = array[1];
		} else {
			int pos = chamada.lastIndexOf(".");
			biblio = chamada.substring(0, pos);
			metodo = chamada.substring(pos + 1);
		}
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
	public void configurarLinkBibliotecaPre(Map<String, AliasContexto> mapaAlias) throws ExpressaoException {
		if (alias != null) {
			AliasContexto aliasContexto = mapaAlias.get(alias);
			if (aliasContexto == null) {
				throw new ExpressaoException("erro.alias.nao_mapeado", alias);
			}
			biblio = aliasContexto.getBiblioteca();
		}
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
		if (comRetorno) {
			print(pw, INVOKE_CRET, biblio, metodo);
		} else {
			print(pw, INVOKE_VOID, biblio, metodo);
		}
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