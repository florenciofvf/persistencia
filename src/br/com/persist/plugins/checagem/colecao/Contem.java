package br.com.persist.plugins.checagem.colecao;

import java.util.Collection;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoBinaria;

public class Contem extends FuncaoBinaria {
	private static final String ERRO = "Erro Contem";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object op0 = param0().executar(checagem, bloco, ctx);
		Object op1 = param1().executar(checagem, bloco, ctx);
		checkObrigatorioCollection(op0, ERRO + " >>> op0");
		Collection<?> colecao = (Collection<?>) op0;
		return colecao.contains(op1);
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "contem(collection) : Logico";
	}
}