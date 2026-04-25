package br.com.persist.plugins.expressao.nativo;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class StringPushInstrucao extends Instrucao {
	private String string;

	public StringPushInstrucao(int indice, String parametros) throws ExpressaoException {
		super(indice, StringContexto.PUSH_STRING);
		if (parametros == null) {
			parametros = "";
		}
		this.string = Util.replaceAll(parametros, "\\R", "\r");
		this.string = Util.replaceAll(this.string, "\\N", "\n");
		this.string = Util.replaceAll(this.string, "\\T", "\t");
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		pilhaOperando.push(string);
	}
}