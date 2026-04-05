package br.com.persist.plugins.expressao.invocacao;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Context;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Doc;
import br.com.persist.plugins.expressao.compilador.Indexador;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.TokenManager;

public class InvocacaoContexto extends Contexto {
	public static final String INVOKE_RETR = "invoke_cret";
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
		init();
	}

	private void init() {
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
	protected void configurarAliasInvocacaoPre(Map<String, String> mapa) throws ExpressaoException {
		if (alias != null) {
			biblio = mapa.get(alias);
			if (biblio == null) {
				throw new ExpressaoException("erro.alias.nao_mapeado", alias);
			}
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
			print(pw, INVOKE_RETR, biblio, metodo);
		} else {
			print(pw, INVOKE_VOID, biblio, metodo);
		}
	}
}