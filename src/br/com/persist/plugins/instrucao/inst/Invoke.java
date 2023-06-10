package br.com.persist.plugins.instrucao.inst;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.Biblioteca;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;
import br.com.persist.plugins.instrucao.pro.Instrucao;
import br.com.persist.plugins.instrucao.pro.Metodo;
import br.com.persist.plugins.instrucao.pro.PilhaMetodo;
import br.com.persist.plugins.instrucao.pro.PilhaOperando;

public class Invoke extends Instrucao {
	private String nomeMetodo;

	public Invoke(Metodo metodo) {
		super(metodo, InstrucaoConstantes.INVOKE);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		Invoke resp = new Invoke(metodo);
		resp.nomeMetodo = nomeMetodo;
		return resp;
	}

	@Override
	public void setParam(String string) throws InstrucaoException {
		if (string == null) {
			throw new InstrucaoException("Invoke metodo null.");
		}
		this.nomeMetodo = string;
	}

	@Override
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca)
			throws InstrucaoException {
		String[] array = nomeMetodo.split(".");
		Biblioteca biblioteca;
		Metodo invocar;
		if (array.length == 2) {
			biblioteca = cacheBiblioteca.getBiblioteca(array[0]);
			invocar = biblioteca.getMetodo(array[1]);
		} else {
			biblioteca = metodo.getBiblioteca();
			invocar = biblioteca.getMetodo(array[0]);
		}
		// TODO
		pilhaMetodo.push(invocar.clonar());
	}
}