package br.com.persist.plugins.checagem;

public abstract class FuncaoBinariaOuMaior extends FuncaoUnaria {
	private final int totalParametros;

	public FuncaoBinariaOuMaior(int totalParametros) {
		if (totalParametros < 2) {
			throw new IllegalArgumentException("Total de parametos menor que 2 >>> " + getClass().getName());
		}
		this.totalParametros = totalParametros;
	}

	public Sentenca param1() {
		return parametros.get(1);
	}

	@Override
	public void addParam(Sentenca sentenca) throws ChecagemException {
		if (!modoInsercao) {
			throw new ChecagemException(getClass(), "O parametro nao pode ser adicionado");
		}
		if (parametros.size() == totalParametros) {
			throw new ChecagemException(getClass(), erro("A funcao ja possui"));
		}
		addParamImpl(sentenca);
		modoInsercao = false;
	}

	@Override
	public void preParametro() throws ChecagemException {
		if (modoInsercao) {
			throw new ChecagemException(getClass(), "Parametro anterior nao adicionado");
		}
		if (parametros.size() == totalParametros) {
			throw new ChecagemException(getClass(), erro("A funcao suporta apenas"));
		}
		modoInsercao = true;
	}

	@Override
	public void encerrar() throws ChecagemException {
		if (parametros.size() != totalParametros) {
			throw new ChecagemException(getClass(), erro("A funcao exige"));
		}
		encerrado = true;
	}

	private String erro(String prefixo) {
		return prefixo + " " + totalParametros + " parametros";
	}
}