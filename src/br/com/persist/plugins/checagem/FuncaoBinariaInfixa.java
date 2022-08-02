package br.com.persist.plugins.checagem;

public abstract class FuncaoBinariaInfixa extends FuncaoBinaria {
	public void addParamOp0(Sentenca sentenca) throws ChecagemException {
		super.addParam(sentenca);
		modoInsercao = true;
	}

	@Override
	public void preParametro() throws ChecagemException {
		throw new ChecagemException(getClass(), "A funcao eh infixa");
	}

	@Override
	public void checarEncerrar() throws ChecagemException {
		if (parametros.size() == 2) {
			encerrado = true;
		}
		super.checarEncerrar();
	}
}