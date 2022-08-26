package br.com.persist.plugins.checagem.util;

import br.com.persist.parser.Parser;
import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoUnaria;

public class ParseJson extends FuncaoUnaria {
	private static final String ERRO = "Erro ParseJson";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		checkObrigatorioString(op0, ERRO + " >>> op0");
		try {
			Parser parser = new Parser();
			return parser.parse((String) op0);
		} catch (Exception ex) {
			throw new ChecagemException(getClass(), ex.getMessage());
		}
	}
}