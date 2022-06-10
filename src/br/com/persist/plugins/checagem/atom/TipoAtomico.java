package br.com.persist.plugins.checagem.atom;

import br.com.persist.plugins.checagem.Sentenca;

public abstract class TipoAtomico extends Sentenca {
	@Override
	public void add(Sentenca sentenca) {
		throw new UnsupportedOperationException("Not supported.");
	}
}