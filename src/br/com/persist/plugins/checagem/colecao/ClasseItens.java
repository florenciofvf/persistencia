package br.com.persist.plugins.checagem.colecao;

import java.util.Collection;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoUnaria;

public class ClasseItens extends FuncaoUnaria {
	private static final String ERRO = "Erro ClasseItens";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		checkObrigatorioCollection(op0, ERRO + " >>> op0");
		Collection<?> colecao = (Collection<?>) op0;
		StringBuilder sb = new StringBuilder();
		int indice = 0;
		for (Object object : colecao) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			if (object == null) {
				sb.append(indice + ": null");
			} else {
				sb.append(indice + ": " + object.getClass().getName());
			}
			indice++;
		}
		return sb.toString();
	}
}