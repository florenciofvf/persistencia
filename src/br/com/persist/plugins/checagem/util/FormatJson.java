package br.com.persist.plugins.checagem.util;

import br.com.persist.data.DataParser;
import br.com.persist.data.Tipo;
import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoUnaria;

public class FormatJson extends FuncaoUnaria {
	private static final String ERRO = "Erro FormatJson";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		checkObrigatorioString(op0, ERRO + " >>> op0");
		try {
			DataParser parser = new DataParser();
			Tipo tipo = parser.parse((String) op0);
			return tipo.toString();
		} catch (Exception ex) {
			throw new ChecagemException(getClass(), ex.getMessage());
		}
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "formatJson(Texto) : Texto";
	}
}