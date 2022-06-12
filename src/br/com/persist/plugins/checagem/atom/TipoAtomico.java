package br.com.persist.plugins.checagem.atom;

public abstract class TipoAtomico extends Sentenca {
	public abstract String getValorString();

	@Override
	public void preParametro() {
		throw new UnsupportedOperationException("Nao suporta parametros >>> " + getClass().getName());
	}

	@Override
	public void encerrar() {
		throw new UnsupportedOperationException("Nao eh uma funcao >>> " + getClass().getName());
	}
}