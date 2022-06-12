package br.com.persist.plugins.checagem.atom;

import java.util.ArrayList;
import java.util.List;

public abstract class TipoFuncao extends Sentenca {
	protected final List<Sentenca> parametros;

	public TipoFuncao() {
		parametros = new ArrayList<>();
	}

	public void addParam(Sentenca sentenca) {
		addParamImpl(sentenca);
	}

	protected void addParamImpl(Sentenca sentenca) {
		check(sentenca);
		sentenca.pai = this;
		parametros.add(sentenca);
	}

	public void setUltimoParametro(Sentenca sentenca) {
		check(sentenca);
		sentenca.pai = this;
		parametros.set(parametros.size() - 1, sentenca);
	}

	private void check(Sentenca sentenca) {
		if (sentenca == this) {
			throw new IllegalStateException("Sentenca tentando adicionar a si proprio");
		}
		if (sentenca.pai != null) {
			throw new IllegalStateException("A sentenca ja possui um pai");
		}
	}

	public Sentenca getUltimoParametro() {
		return parametros.get(parametros.size() - 1);
	}
}