package br.com.persist.plugins.checagem;

public abstract class FuncaoVazia extends TipoFuncao {
	@Override
	public void addParam(Sentenca sentenca) throws ChecagemException {
		throw new ChecagemException("A funcao nao suporta parametros >>> " + getClass().getName());
	}

	@Override
	public void preParametro() throws ChecagemException {
		throw new ChecagemException("Nao suporta qualquer parametro >>> " + getClass().getName());
	}

	@Override
	public void encerrar() throws ChecagemException {
		if (!parametros.isEmpty()) {
			throw new ChecagemException("A funcao nao exige parametros >>> " + getClass().getName());
		}
		encerrado = true;
	}
}