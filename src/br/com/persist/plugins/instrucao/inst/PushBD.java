package br.com.persist.plugins.instrucao.inst;

import java.math.BigDecimal;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;
import br.com.persist.plugins.instrucao.pro.Instrucao;
import br.com.persist.plugins.instrucao.pro.Metodo;
import br.com.persist.plugins.instrucao.pro.PilhaMetodo;
import br.com.persist.plugins.instrucao.pro.PilhaOperando;

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
	public String getParam() {
		return bigDecimal.toString();
	}

	@Override
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca)
			throws InstrucaoException {
		pilhaOperando.push(bigDecimal);
	}
}