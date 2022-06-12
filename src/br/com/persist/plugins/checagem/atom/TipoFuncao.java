package br.com.persist.plugins.checagem.atom;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.checagem.ChecagemException;

public abstract class TipoFuncao extends Sentenca {
	protected final List<Sentenca> parametros;

	public TipoFuncao() {
		parametros = new ArrayList<>();
	}

	public void addParam(Sentenca sentenca) throws ChecagemException {
		addParamImpl(sentenca);
	}

	protected void addParamImpl(Sentenca sentenca) throws ChecagemException {
		check(sentenca);
		sentenca.pai = this;
		parametros.add(sentenca);
	}

	public void setUltimoParametro(Sentenca sentenca) throws ChecagemException {
		check(sentenca);
		sentenca.pai = this;
		parametros.set(parametros.size() - 1, sentenca);
	}

	private void check(Sentenca sentenca) throws ChecagemException {
		if (sentenca == this) {
			throw new ChecagemException("Sentenca tentando adicionar a si proprio");
		}
		if (sentenca.pai != null) {
			throw new ChecagemException("A sentenca ja possui um pai");
		}
	}

	public Sentenca getUltimoParametro() {
		return parametros.get(parametros.size() - 1);
	}
}