package br.com.persist.plugins.instrucao.processador;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;

public class ConstInstrucao extends Instrucao {
	public ConstInstrucao() {
		super(InstrucaoConstantes.CONST);
	}

	@Override
	public Instrucao clonar() {
		return new ConstInstrucao();
	}

	@Override
	public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
			PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
		Object valor = pilhaOperando.pop();
		Constante constante = null;
		if (funcao == null) {
			constante = biblioteca.getConstante(parametros);
		} else {
			constante = new Constante(parametros);
			biblioteca.addConstante(constante);
		}
		constante.setValor(valor);
	}
}