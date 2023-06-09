package br.com.persist.plugins.instrucao.inst;

import br.com.persist.plugins.instrucao.CacheBiblioteca;
import br.com.persist.plugins.instrucao.Instrucao;
import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.Metodo;
import br.com.persist.plugins.instrucao.PilhaMetodo;
import br.com.persist.plugins.instrucao.PilhaOperando;

public class Goto extends Instrucao {
	private int indice;

	public Goto(Metodo metodo) {
		super(metodo, InstrucaoConstantes.RETURN);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		Goto resp = new Goto(metodo);
		resp.indice = indice;
		return resp;
	}

	@Override
	public void setParam(String string) {
		indice = Integer.parseInt(string);
	}

	@Override
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca) {
		metodo.setIndice(indice);
	}
}