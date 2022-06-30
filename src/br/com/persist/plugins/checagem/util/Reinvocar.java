package br.com.persist.plugins.checagem.util;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.ChecagemUtil;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.FuncaoUnariaOuNParam;
import br.com.persist.plugins.checagem.Modulo;

public class Reinvocar extends FuncaoUnariaOuNParam {
	private static final String ERRO = "Erro Reinvocar >>> ";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		if (parametros.size() == 1) {
			return executarProprioBloco(checagem, bloco, ctx);
		} else if (parametros.size() == 2) {
			return executarOutroBloco(checagem, bloco, ctx);
		} else if (parametros.size() == 3) {
			return executarModuloBloco(checagem, bloco, ctx);
		}
		throw new ChecagemException(ERRO + "O maximo de parametros eh 3");
	}

	private Object executarProprioBloco(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		checkObrigatorioMap(op0, ERRO + "op0");
		return bloco.executar(checagem, Contexto.criar(op0));
	}

	private Object executarOutroBloco(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		checkObrigatorioString(op0, ERRO + "op0");
		Object op1 = parametros.get(1).executar(checagem, bloco, ctx);
		checkObrigatorioMap(op1, ERRO + "op1");
		Modulo modulo = bloco.getModulo();
		Bloco outro = modulo.getBloco((String) op0);
		return outro.executar(checagem, Contexto.criar(op1));
	}

	private Object executarModuloBloco(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		checkObrigatorioString(op0, ERRO + "op0");
		Object op1 = parametros.get(1).executar(checagem, bloco, ctx);
		checkObrigatorioString(op1, ERRO + "op1");
		Object op2 = parametros.get(2).executar(checagem, bloco, ctx);
		checkObrigatorioMap(op2, ERRO + "op2");
		String idModulo = (String) op0;
		ChecagemUtil.checarModulo(idModulo);
		Modulo modulo = checagem.getModulo(idModulo);
		Bloco outro = modulo.getBloco((String) op1);
		return outro.executar(checagem, Contexto.criar(op2));
	}
}