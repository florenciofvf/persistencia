package br.com.persist.plugins.checagem.util;

import br.com.persist.assistencia.RequestUtil;
import br.com.persist.data.DataParser;
import br.com.persist.data.Objeto;
import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoUnaria;
import br.com.persist.plugins.variaveis.VariavelProvedor;

public class Request extends FuncaoUnaria {
	private static final String ERRO = "Erro Request";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		checkObrigatorioString(op0, ERRO + " >>> op0");
		String string = (String) op0;
		string = VariavelProvedor.substituir(string);
		DataParser parser = new DataParser();
		try {
			Objeto parametros = (Objeto) parser.parse(string);
			return RequestUtil.processar(parametros);
		} catch (Exception ex) {
			throw new ChecagemException(getClass(), ERRO + " >>> " + ex.getMessage());
		}
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "request(Texto) : Objeto";
	}
}