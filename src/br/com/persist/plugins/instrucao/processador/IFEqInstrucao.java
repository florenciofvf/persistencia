package br.com.persist.plugins.instrucao.processador;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.IFEqContexto;

public class IFEqInstrucao extends Instrucao {
	private int indice;

	public IFEqInstrucao() {
		super(IFEqContexto.IF_EQ);
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
			PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
		Object operando = pilhaOperando.pop();
		InstrucaoUtil.checarNumber(operando);
		int valor = ((Number) operando).intValue();
		if (valor == 0) {
			funcao.setIndice(indice);
		}
	}
}