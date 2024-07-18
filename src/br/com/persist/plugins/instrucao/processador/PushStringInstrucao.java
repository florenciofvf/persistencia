package br.com.persist.plugins.instrucao.processador;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.StringContexto;

public class PushStringInstrucao extends Instrucao {
	public PushStringInstrucao() {
		super(StringContexto.PUSH_STRING);
	}

	@Override
	public Instrucao clonar() {
		return this;
	}

	@Override
	public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
			PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
		pilhaOperando.push(getParametros());
	}
}