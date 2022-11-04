package br.com.persist.plugins.checagem.util;

import br.com.persist.assistencia.Constantes;
import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.Sentenca;
import br.com.persist.plugins.checagem.funcao.FuncaoUnariaOuNParam;

public class Doc extends FuncaoUnariaOuNParam {
	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		StringBuilder resposta = new StringBuilder();
		for (Sentenca s : parametros) {
			if (resposta.length() > 0) {
				resposta.append(Constantes.QL2);
			}
			resposta.append(s.getDoc());
		}
		return resposta;
	}

	@Override
	public String getDoc() {
		return "doc(funcao,funcao)";
	}
}