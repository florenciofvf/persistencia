package br.com.persist.plugins.instrucao.inst;

import br.com.persist.plugins.instrucao.CacheBiblioteca;
import br.com.persist.plugins.instrucao.Instrucao;
import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.Metodo;
import br.com.persist.plugins.instrucao.PilhaMetodo;
import br.com.persist.plugins.instrucao.PilhaOperando;

public class PushString extends Instrucao {
	private String string;

	public PushString(Metodo metodo) {
		super(metodo, InstrucaoConstantes.PUSH_STRING);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		PushString resp = new PushString(metodo);
		resp.string = string;
		return resp;
	}

	@Override
	public void setParam(String string) {
		if (string == null) {
			string = "";
		}
		this.string = string;
	}

	@Override
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca)
			throws InstrucaoException {
		pilhaOperando.push(string);
	}
}