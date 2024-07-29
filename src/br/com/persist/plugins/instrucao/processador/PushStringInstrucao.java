package br.com.persist.plugins.instrucao.processador;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.StringContexto;

public class PushStringInstrucao extends Instrucao {
	public PushStringInstrucao() {
		super(StringContexto.PUSH_STRING);
	}

	@Override
	public Instrucao clonar() {
		return new PushStringInstrucao();
	}

	@Override
	public void setParametros(String parametros) {
		if (parametros == null) {
			parametros = "";
		}
		this.parametros = Util.replaceAll(parametros, "\\R", "\r");
		this.parametros = Util.replaceAll(this.parametros, "\\N", "\n");
	}

	@Override
	public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
			PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
		pilhaOperando.push(getParametros());
	}
}