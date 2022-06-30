package br.com.persist.plugins.checagem;

public class SentencaRaiz extends FuncaoUnaria {
	public Sentenca getSentenca() {
		return param0();
	}

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		throw new ChecagemException("Nao pode ser executado >>> " + getClass().getName());
	}

	@Override
	public void encerrar() throws ChecagemException {
		throw new ChecagemException("Nao pode ser encerrado >>> " + getClass().getName());
	}
}