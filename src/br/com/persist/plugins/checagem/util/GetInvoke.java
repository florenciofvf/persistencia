package br.com.persist.plugins.checagem.util;

import java.lang.reflect.Method;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoBinaria;

public class GetInvoke extends FuncaoBinaria {
	private static final String ERRO = "Erro GetInvoke";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		Object op1 = param1().executar(checagem, bloco, ctx);
		checkObrigatorioString(op1, ERRO + " >>> op1");
		try {
			Class<?> klass = op0.getClass();
			Method method = klass.getMethod((String) op1);
			return method.invoke(op0);
		} catch (Exception ex) {
			throw new ChecagemException(getClass(), ex.getMessage());
		}
	}
}