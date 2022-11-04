package br.com.persist.plugins.checagem.condicional;

import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoBinariaOuNParam;

public class MultEscolha extends FuncaoBinariaOuNParam {
	private static final String ERRO = "Erro MultEscolha";

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		for (int i = 0; i < parametros.size(); i += 2) {
			Object condicao = parametros.get(i).executar(checagem, bloco, ctx);
			checkObrigatorioBoolean(condicao, ERRO + " >>> op" + i);
			int indiceValor = i + 1;
			if (indiceValor >= parametros.size()) {
				throw new ChecagemException(getClass(), "Condicao sem valor >>> op" + i);
			}
			if ((Boolean) condicao) {
				return parametros.get(indiceValor).executar(checagem, bloco, ctx);
			}
			indiceValor += 2;
			if (indiceValor == parametros.size()) {
				return parametros.get(indiceValor - 1).executar(checagem, bloco, ctx);
			}
		}
		return null;
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "switch(Logico, Logico, Logico) : Objeto";
	}
}