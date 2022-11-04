package br.com.persist.plugins.checagem.colecao;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoVazia;

public class MapParam extends FuncaoVazia {
	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		return ctx.getMap();
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "mapParam() : {}";
	}
}