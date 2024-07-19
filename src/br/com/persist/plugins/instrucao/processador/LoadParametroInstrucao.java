package br.com.persist.plugins.instrucao.processador;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.ParametroContexto;

public class LoadParametroInstrucao extends Instrucao {
	public LoadParametroInstrucao() {
		super(ParametroContexto.LOAD_PARAM);
	}

	@Override
	public Instrucao clonar() {
		return this;
	}

	@Override
	public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
			PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
		Object valor = funcao.getValorParametro(parametros);
		pilhaOperando.push(valor);
	}
}