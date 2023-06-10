package br.com.persist.plugins.instrucao.inst;

import java.util.ArrayList;
import java.util.List;

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
		Metodo clone = invocar.clonar();
		if (!clone.isNativo()) {
			setParametros(clone, pilhaOperando);
			pilhaMetodo.push(clone);
		} else {
			Object resp = invocarNativo(clone, pilhaOperando);
			pilhaOperando.push(resp);
		}
	}

	private void setParametros(Metodo metodo, PilhaOperando pilhaOperando) throws InstrucaoException {
		List<Integer> params = listaParam(metodo);
		for (int i = params.size() - 1; i >= 0; i--) {
			Object valor = pilhaOperando.pop();
			metodo.setValorParam(params.get(i), valor);
		}
	}

	private List<Integer> listaParam(Metodo metodo) {
		List<Integer> resp = new ArrayList<>();
		for (int i = 0; i < metodo.getTotalParam(); i++) {
			resp.add(i);
		}
		return resp;
	}

	private Object invocarNativo(Metodo metodo, PilhaOperando pilhaOperando) {
		// TODO Auto-generated method stub
		return null;
	}
}