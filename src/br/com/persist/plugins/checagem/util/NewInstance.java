package br.com.persist.plugins.checagem.util;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoUnaria;

public class NewInstance extends FuncaoUnaria {
	private static final String ERRO = "Erro NewInstance";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		try {
			if (op0 instanceof Class<?>) {
				return ((Class<?>) op0).newInstance();
			} else if (op0 instanceof String) {
				return Class.forName((String) op0).newInstance();
			}
			throw new ChecagemException(getClass(), ERRO + " >>> " + op0);
		} catch (Exception ex) {
			throw new ChecagemException(getClass(), ERRO + " >>> " + ex.getMessage());
		}
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "newInstance(Texto) : Objeto";
	}
}