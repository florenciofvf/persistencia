package br.com.persist.plugins.checagem.logico;

import br.com.persist.plugins.checagem.Auto;
import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.Sentenca;
import br.com.persist.plugins.checagem.funcao.FuncaoUnaria;

public class Nao extends FuncaoUnaria implements Auto {
	private static final String ERRO = "Erro Nao";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		checkObrigatorioBoolean(op0, ERRO + " >>> op0");
		Boolean pri = (Boolean) op0;
		return !pri;
	}

	@Override
	public void addParam(Sentenca sentenca) throws ChecagemException {
		super.addParam(sentenca);
		encerrar();
	}
}