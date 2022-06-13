package br.com.persist.plugins.checagem.atom;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.checagem.ChecagemException;

public abstract class TipoFuncao extends Sentenca {
	protected final List<Sentenca> parametros;
	protected boolean encerrado;

	public TipoFuncao() {
		parametros = new ArrayList<>();
	}

	public abstract void encerrar() throws ChecagemException;

	public abstract void preParametro() throws ChecagemException;

	public void addParam(Sentenca sentenca) throws ChecagemException {
		addParamImpl(sentenca);
	}

	protected void addParamImpl(Sentenca sentenca) throws ChecagemException {
		if (sentenca == null) {
			throw new ChecagemException("Sentenca nula");
		}
		checkSentenca(sentenca);
		sentenca.pai = this;
		parametros.add(sentenca);
	}

	public void setUltimoParametro(Sentenca sentenca) throws ChecagemException {
		checkSentenca(sentenca);
		checkParametros();
		sentenca.pai = this;
		parametros.set(parametros.size() - 1, sentenca);
	}

	private void checkSentenca(Sentenca sentenca) throws ChecagemException {
		if (sentenca == this) {
			throw new ChecagemException("Sentenca tentando adicionar a si proprio");
		}
		if (sentenca.pai != null) {
			throw new ChecagemException("A sentenca ja possui um pai");
		}
	}

	private void checkParametros() throws ChecagemException {
		if (parametros.isEmpty()) {
			throw new ChecagemException("Nenhum parametro definido");
		}
	}

	public Sentenca getUltimoParametro() throws ChecagemException {
		checkParametros();
		return parametros.get(parametros.size() - 1);
	}

	protected void checkObrigatorioBoolean(Object object, String msg) throws ChecagemException {
		if (!(object instanceof Boolean)) {
			throw new ChecagemException(msg + " nao eh Boolean");
		}
	}

	protected void checkObrigatorioDouble(Object object, String msg) throws ChecagemException {
		if (!(object instanceof Double)) {
			throw new ChecagemException(msg + " nao eh Double");
		}
	}

	protected void checkObrigatorioLong(Object object, String msg) throws ChecagemException {
		if (!(object instanceof Long)) {
			throw new ChecagemException(msg + " nao eh Long");
		}
	}

	protected void checkObrigatorioString(Object object, String msg) throws ChecagemException {
		if (!(object instanceof String)) {
			throw new ChecagemException(msg + " nao eh String");
		}
	}

	public void checarEncerrar() throws ChecagemException {
		if (!encerrado) {
			throw new ChecagemException("Funcao nao encerrada");
		}
	}
}