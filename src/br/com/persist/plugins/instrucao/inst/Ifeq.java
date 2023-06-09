package br.com.persist.plugins.instrucao.inst;

import br.com.persist.plugins.instrucao.CacheBiblioteca;
import br.com.persist.plugins.instrucao.Instrucao;
import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.InstrucaoUtil;
import br.com.persist.plugins.instrucao.Metodo;
import br.com.persist.plugins.instrucao.PilhaMetodo;
import br.com.persist.plugins.instrucao.PilhaOperando;

public class Ifeq extends Instrucao {
	private int indice;

	public Ifeq(Metodo metodo) {
		super(metodo, InstrucaoConstantes.IF_EQ);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		Ifeq resp = new Ifeq(metodo);
		resp.indice = indice;
		return resp;
	}

	@Override
	public void setParam(String string) {
		indice = Integer.parseInt(string);
	}

	@Override
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca)
			throws InstrucaoException {
		Object operando = pilhaOperando.pop();
		InstrucaoUtil.checarNumber(operando);
		int valor = ((Number) operando).intValue();
		if (valor == 0) {
			metodo.setIndice(indice);
		}
	}
}