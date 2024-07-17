package br.com.persist.plugins.instrucao.inst;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;
import br.com.persist.plugins.instrucao.pro.Instrucao;
import br.com.persist.plugins.instrucao.pro.Metodo;
import br.com.persist.plugins.instrucao.pro.PilhaMetodo;
import br.com.persist.plugins.instrucao.pro.PilhaOperando;

public class PushSTR extends Instrucao {
	private String string;

	public PushSTR(Metodo metodo) {
		super(metodo, InstrucaoConstantes.PUSH_STRING);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		PushSTR resp = new PushSTR(metodo);
		resp.string = string;
		return resp;
	}

	@Override
	public void setParam(String string) {
		if (string == null) {
			string = "";
		}
		this.string = Util.replaceAll(string, InstrucaoConstantes.CR, "\r");
		this.string = Util.replaceAll(this.string, InstrucaoConstantes.LF, "\n");
	}

	@Override
	public String getParam() {
		return string;
	}

	@Override
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca)
			throws InstrucaoException {
		pilhaOperando.push(string);
	}
}