package br.com.persist.plugins.instrucao.inst;

import br.com.persist.plugins.instrucao.Instrucao;
import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;
import br.com.persist.plugins.instrucao.pro.Metodo;
import br.com.persist.plugins.instrucao.pro.PilhaMetodo;
import br.com.persist.plugins.instrucao.pro.PilhaOperando;

public class Load extends Instrucao {
	private String param;

	public Load(Metodo metodo) {
		super(metodo, InstrucaoConstantes.PUSH_STRING);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		Load resp = new Load(metodo);
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
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca)
			throws InstrucaoException {
		Object valor = metodo.getValorParam(param);
		pilhaOperando.push(valor);
	}
}