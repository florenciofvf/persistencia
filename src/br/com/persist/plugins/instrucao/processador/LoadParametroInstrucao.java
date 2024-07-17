package br.com.persist.plugins.instrucao.processador;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.ParametroContexto;

public class LoadParametroInstrucao extends Instrucao {
	public LoadParametroInstrucao() {
		super(ParametroContexto.LOAD_PAR);
	}

	@Override
	public Instrucao clonar() {
		return this;
	}

	@Override
	public void processar(CacheBiblioteca cacheBiblioteca, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws InstrucaoException {
		Object valor = funcao.getValorParam(parametros);
		pilhaOperando.push(valor);
	}
}