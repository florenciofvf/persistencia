package br.com.persist.plugins.checagem.util;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoUnariaOuNParam;

public class Importar extends FuncaoUnariaOuNParam {
	private static final String ERRO = "Erro Importar";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		checkObrigatorioString(op0, ERRO + " >>> op0");
		checagem.add(op0.toString());
		for (int i = 1; i < parametros.size(); i++) {
			Object valor = parametros.get(i).executar(checagem, bloco, ctx);
			checkObrigatorioString(valor, ERRO + " >>> op" + i);
			checagem.add(op0.toString());
		}
		return op0;
	}
}