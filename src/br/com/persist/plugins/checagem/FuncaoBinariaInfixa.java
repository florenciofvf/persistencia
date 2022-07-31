package br.com.persist.plugins.checagem;

public abstract class FuncaoBinariaInfixa extends FuncaoBinariaOuMaior {

	public FuncaoBinariaInfixa() {
		super(2);
	}

	@Override
	public void preParametro() throws ChecagemException {
		throw new ChecagemException(getClass(), "A funcao eh infixa");
	}
}