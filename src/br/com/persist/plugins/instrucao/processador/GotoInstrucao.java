package br.com.persist.plugins.instrucao.processador;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.GotoContexto;

public class GotoInstrucao extends Instrucao {
	private int indice;

	public GotoInstrucao() {
		super(GotoContexto.GOTO);
	}

	@Override
	public Instrucao clonar() {
		return this;
	}

	@Override
	public void setParametros(String string) {
		indice = Integer.parseInt(string);
	}

	@Override
	public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
			PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws InstrucaoException {
		funcao.setIndice(indice);
	}
}