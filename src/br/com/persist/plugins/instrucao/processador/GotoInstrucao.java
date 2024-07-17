package br.com.persist.plugins.instrucao.inst;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;
import br.com.persist.plugins.instrucao.pro.Instrucao;
import br.com.persist.plugins.instrucao.pro.Metodo;
import br.com.persist.plugins.instrucao.pro.PilhaMetodo;
import br.com.persist.plugins.instrucao.pro.PilhaOperando;

public class Goto extends Instrucao {
	private int indice;

	public Goto(Metodo metodo) {
		super(metodo, InstrucaoConstantes.GOTO);
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
	public String getParam() {
		return String.valueOf(indice);
	}

	@Override
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca)
			throws InstrucaoException {
		metodo.setIndice(indice);
	}
}