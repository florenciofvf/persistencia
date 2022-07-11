package br.com.persist.plugins.checagem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import br.com.persist.assistencia.Lista;

public abstract class TipoFuncao extends Sentenca {
	protected final List<Sentenca> parametros;
	protected boolean modoInsercao = true;
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
			throw new ChecagemException(getClass(), "Sentenca nula");
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
			throw new ChecagemException(getClass(), "Sentenca tentando adicionar a si proprio");
		}
		if (sentenca.pai != null) {
			throw new ChecagemException(getClass(), "A sentenca ja possui um pai");
		}
	}

	private void checkParametros() throws ChecagemException {
		if (parametros.isEmpty()) {
			throw new ChecagemException(getClass(), "Nenhum parametro definido");
		}
	}

	public Sentenca getUltimoParametro() throws ChecagemException {
		checkParametros();
		return parametros.get(parametros.size() - 1);
	}

	protected void checkObrigatorioBoolean(Object object, String msg) throws ChecagemException {
		if (!(object instanceof Boolean)) {
			throw new ChecagemException(getClass(), msg + " nao eh Boolean");
		}
	}

	protected void checkObrigatorioDouble(Object object, String msg) throws ChecagemException {
		if (!(object instanceof Double)) {
			throw new ChecagemException(getClass(), msg + " nao eh Double");
		}
	}

	protected void checkObrigatorioLong(Object object, String msg) throws ChecagemException {
		if (!(object instanceof Long)) {
			throw new ChecagemException(getClass(), msg + " nao eh Long");
		}
	}

	protected void checkObrigatorioString(Object object, String msg) throws ChecagemException {
		if (!(object instanceof String)) {
			throw new ChecagemException(getClass(), msg + " nao eh String");
		}
	}

	protected void checkObrigatorioCollection(Object object, String msg) throws ChecagemException {
		if (!(object instanceof Collection<?>)) {
			throw new ChecagemException(getClass(), msg + " nao eh Collection");
		}
	}

	protected void checkObrigatorioLista(Object object, String msg) throws ChecagemException {
		if (!(object instanceof Lista)) {
			throw new ChecagemException(getClass(), msg + " nao eh Lista");
		}
	}

	protected void checkObrigatorioMap(Object object, String msg) throws ChecagemException {
		if (!(object instanceof Map<?, ?>)) {
			throw new ChecagemException(getClass(), msg + " nao eh Map");
		}
	}

	public void checarEncerrar() throws ChecagemException {
		if (!encerrado) {
			throw new ChecagemException(getClass(), "Funcao nao encerrada");
		}
	}
}