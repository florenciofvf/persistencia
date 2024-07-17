package br.com.persist.plugins.instrucao.inst;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;
import br.com.persist.plugins.instrucao.pro.Instrucao;
import br.com.persist.plugins.instrucao.pro.Metodo;
import br.com.persist.plugins.instrucao.pro.PilhaMetodo;
import br.com.persist.plugins.instrucao.pro.PilhaOperando;

public class TailCall extends Instrucao {
	public TailCall(Metodo metodo) {
		super(metodo, InstrucaoConstantes.TAIL_CALL);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		return new TailCall(metodo);
	}

	@Override
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca)
			throws InstrucaoException {
		if (metodo == null) {
			throw new InstrucaoException("erro.metodo_inexistente", "null", "null");
		}
		try {
			Invoke.setParametros(metodo, pilhaOperando);
			metodo.setIndice(0);
		} catch (Exception ex) {
			throw new InstrucaoException(Invoke.stringPilhaMetodo(metodo, pilhaMetodo), ex);
		}
	}
}