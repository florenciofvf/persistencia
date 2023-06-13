package br.com.persist.plugins.instrucao.inst;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.Biblioteca;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;
import br.com.persist.plugins.instrucao.pro.Instrucao;
import br.com.persist.plugins.instrucao.pro.Metodo;
import br.com.persist.plugins.instrucao.pro.PilhaMetodo;
import br.com.persist.plugins.instrucao.pro.PilhaOperando;

public class LoadVar extends Instrucao {
	private String param;

	public LoadVar(Metodo metodo) {
		super(metodo, InstrucaoConstantes.LOAD_VAR);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		LoadVar resp = new LoadVar(metodo);
		resp.param = param;
		return resp;
	}

	@Override
	public void setParam(String string) throws InstrucaoException {
		if (string == null) {
			throw new InstrucaoException("LoadVar var null.");
		}
		this.param = string;
	}

	@Override
	public String getParam() {
		return param;
	}

	@Override
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca)
			throws InstrucaoException {
		String[] array = param.split("\\.");
		Biblioteca biblioteca;
		Object valor;
		if (array.length == 2) {
			biblioteca = cacheBiblioteca.getBiblioteca(array[0]);
			valor = biblioteca.getValorVariavel(array[1]);
		} else {
			biblioteca = metodo.getBiblioteca();
			valor = biblioteca.getValorVariavel(array[0]);
		}
		pilhaOperando.push(valor);
	}
}