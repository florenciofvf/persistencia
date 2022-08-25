package br.com.persist.plugins.checagem.util;

import java.io.IOException;

import br.com.persist.assistencia.RequestUtil;
import br.com.persist.parser.Objeto;
import br.com.persist.parser.Parser;
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
		Parser parser = new Parser();
		Objeto parametros = (Objeto) parser.parse(string);
		try {
			return RequestUtil.processar(parametros);
		} catch (IOException ex) {
			throw new ChecagemException(getClass(), ex.getMessage());
		}
	}
}