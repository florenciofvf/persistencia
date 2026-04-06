package br.com.persist.plugins.expressao.nativo;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class StringPushInstrucao extends Instrucao {
	public StringPushInstrucao() {
		super(StringContexto.PUSH_STRING);
	}

	@Override
	public Instrucao novo() {
		return new StringPushInstrucao();
	}

	@Override
	public void setParametros(String parametros) {
		if (parametros == null) {
			parametros = "";
		}
		this.parametros = Util.replaceAll(parametros, "\\R", "\r");
		this.parametros = Util.replaceAll(this.parametros, "\\N", "\n");
		this.parametros = Util.replaceAll(this.parametros, "\\T", "\t");
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		pilhaOperando.push(getParametros());
	}
}