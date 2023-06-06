package br.com.persist.plugins.instrucao.inst;

import br.com.persist.plugins.instrucao.CacheBiblioteca;
import br.com.persist.plugins.instrucao.Instrucao;
import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.Metodo;
import br.com.persist.plugins.instrucao.PilhaMetodo;
import br.com.persist.plugins.instrucao.PilhaOperando;

public class Return extends Instrucao {
	public Return(Metodo metodo) {
		super(metodo, InstrucaoConstantes.RETURN);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		return new Return(metodo);
	}

	@Override
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca) {
		pilhaMetodo.remove();
	}
}