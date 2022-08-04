package br.com.persist.plugins.checagem.funcao;

import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Sentenca;

public abstract class FuncaoBinariaInfixa extends FuncaoBinaria {
	protected short comparacao1 = 200;
	protected short matematico3 = 140;
	protected short matematico2 = 120;
	protected short matematico1 = 100;
	protected short logico1 = 300;

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

	public abstract short getNivel();

	public boolean isPrioritario(Object obj) {
		if (obj instanceof FuncaoBinariaInfixa) {
			FuncaoBinariaInfixa funcao = (FuncaoBinariaInfixa) obj;
			return getNivel() < funcao.getNivel();
		}
		return false;
	}
}