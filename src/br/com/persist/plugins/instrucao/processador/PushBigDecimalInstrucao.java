package br.com.persist.plugins.instrucao.processador;

import java.math.BigDecimal;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.NumeroContexto;

public class PushBigDecimalInstrucao extends Instrucao {
	private BigDecimal bigDecimal;

	public PushBigDecimalInstrucao() {
		super(NumeroContexto.PUSH_BIG_DECIMAL);
	}

	@Override
	public Instrucao clonar() {
		return this;
	}

	@Override
	public void setParametros(String string) {
		bigDecimal = new BigDecimal(string);
	}

	@Override
	public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
			PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws InstrucaoException {
		pilhaOperando.push(bigDecimal);
	}
}