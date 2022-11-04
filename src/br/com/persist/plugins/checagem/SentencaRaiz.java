package br.com.persist.plugins.checagem;

import br.com.persist.plugins.checagem.funcao.FuncaoUnaria;

public class SentencaRaiz extends FuncaoUnaria {
	public Sentenca getSentenca() {
		return param0();
	}

	@Override
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		throw new ChecagemException(getClass(), "Nao pode ser executado");
	}

	@Override
	public void encerrar() throws ChecagemException {
		throw new ChecagemException(getClass(), "Nao pode ser encerrado");
	}

	@Override
	public String getDoc() throws ChecagemException {
		throw new ChecagemException(getClass(), "Nenhum doc");
	}
}