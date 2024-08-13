package br.com.persist.plugins.instrucao.processador;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.RetornoContexto;

public class RetornoInstrucao extends Instrucao {
	public RetornoInstrucao() {
		super(RetornoContexto.RETURN);
	}

	@Override
	public Instrucao clonar() {
		return new RetornoInstrucao();
	}

	@Override
	public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
			PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
		pilhaFuncao.pop();
	}
}