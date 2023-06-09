package br.com.persist.plugins.instrucao.inst;

import java.math.BigDecimal;

import br.com.persist.plugins.instrucao.CacheBiblioteca;
import br.com.persist.plugins.instrucao.Instrucao;
import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.Metodo;
import br.com.persist.plugins.instrucao.PilhaMetodo;
import br.com.persist.plugins.instrucao.PilhaOperando;

public class PushBD extends Instrucao {
	private BigDecimal bigDecimal;

	public PushBD(Metodo metodo) {
		super(metodo, InstrucaoConstantes.PUSH_BIG_DECIMAL);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		PushBD resp = new PushBD(metodo);
		resp.bigDecimal = bigDecimal;
		return resp;
	}

	@Override
	public void setParam(String string) {
		bigDecimal = new BigDecimal(string);
	}

	@Override
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca)
			throws InstrucaoException {
		pilhaOperando.push(bigDecimal);
	}
}