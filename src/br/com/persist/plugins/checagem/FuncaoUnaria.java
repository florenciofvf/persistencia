package br.com.persist.plugins.checagem;

public abstract class FuncaoUnaria extends TipoFuncao {
	public Sentenca param0() {
		return parametros.get(0);
	}

	@Override
	public void addParam(Sentenca sentenca) throws ChecagemException {
		if (!parametros.isEmpty()) {
			throw new ChecagemException("A funcao ja possui 1 parametro >>> " + getClass().getName());
		}
		addParamImpl(sentenca);
	}

	@Override
	public void preParametro() throws ChecagemException {
		throw new ChecagemException("Suporta apenas 1 parametro >>> " + getClass().getName());
	}

	@Override
	public void encerrar() throws ChecagemException {
		if (parametros.size() != 1) {
			throw new ChecagemException("A funcao exige 1 parametro >>> " + getClass().getName());
		}
		encerrado = true;
	}
}