package br.com.persist.plugins.expressao.parametros;

import java.io.PrintWriter;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Context;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Doc;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.TokenManager;

public class ParametroContexto extends Contexto {
	public static final String PREFIXO_PARAMETRO = "param ";
	public static final String LOAD_PARAM = "load_param";

	public ParametroContexto(Token chave) {
		super(chave);
	}

	@Context("parametro")
	@Doc("chave")
	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		tokenManager.invalidar(token);
	}

	@Override
	public void salvar(PrintWriter pw) throws ExpressaoException {
		pw.println(PREFIXO_PARAMETRO + token.getString());
	}
}