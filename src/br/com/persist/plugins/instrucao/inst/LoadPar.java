package br.com.persist.plugins.instrucao.inst;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;
import br.com.persist.plugins.instrucao.pro.Instrucao;
import br.com.persist.plugins.instrucao.pro.Metodo;
import br.com.persist.plugins.instrucao.pro.PilhaMetodo;
import br.com.persist.plugins.instrucao.pro.PilhaOperando;

public class LoadPar extends Instrucao {
	private String param;

	public LoadPar(Metodo metodo) {
		super(metodo, InstrucaoConstantes.LOAD_PAR);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		LoadPar resp = new LoadPar(metodo);
		resp.param = param;
		return resp;
	}

	@Override
	public void setParam(String string) throws InstrucaoException {
		if (string == null) {
			throw new InstrucaoException("Load param null.");
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
		Object valor = metodo.getValorParam(param);
		pilhaOperando.push(valor);
	}
}