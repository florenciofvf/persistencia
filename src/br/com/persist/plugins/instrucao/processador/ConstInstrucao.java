package br.com.persist.plugins.instrucao.processador;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.ConstanteContexto;

public class ConstInstrucao extends Instrucao {
	public ConstInstrucao() {
		super(ConstanteContexto.CONST);
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