package br.com.persist.plugins.instrucao.inst;

import br.com.persist.assistencia.Lista;
import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.Biblioteca;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;
import br.com.persist.plugins.instrucao.pro.Instrucao;
import br.com.persist.plugins.instrucao.pro.Metodo;
import br.com.persist.plugins.instrucao.pro.PilhaMetodo;
import br.com.persist.plugins.instrucao.pro.PilhaOperando;

public class LoadHeadLista extends Instrucao {
	private boolean parametro;
	private String param;

	public LoadHeadLista(Metodo metodo) {
		super(metodo, InstrucaoConstantes.LOAD_HEAD_LISTA);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		LoadHeadLista resp = new LoadHeadLista(metodo);
		resp.parametro = parametro;
		resp.param = param;
		return resp;
	}

	@Override
	public void setParam(String string) throws InstrucaoException {
		if (string == null) {
			throw new InstrucaoException("LoadHeadLista param null.");
		}
		this.param = string;
		parametro = string.startsWith("$");
	}

	@Override
	public String getParam() {
		return param;
	}

	@Override
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca)
			throws InstrucaoException {
		Object valor = null;
		if (parametro) {
			valor = metodo.getValorParam(param);
		} else {
			String[] array = param.split("\\.");
			Biblioteca biblioteca;
			if (array.length == 2) {
				biblioteca = cacheBiblioteca.getBiblioteca(array[0]);
				valor = biblioteca.getValorVariavel(array[1]);
			} else {
				biblioteca = metodo.getBiblioteca();
				valor = biblioteca.getValorVariavel(array[0]);
			}
		}
		InstrucaoUtil.checarLista(valor);
		pilhaOperando.push(((Lista) valor).head());
	}
}