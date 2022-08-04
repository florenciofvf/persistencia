package br.com.persist.plugins.checagem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import br.com.persist.assistencia.ListaEncadeada;

public abstract class TipoFuncao implements Sentenca {
	protected final List<Sentenca> parametros;
	protected boolean modoInsercao = true;
	protected boolean encerrado;
	protected TipoFuncao pai;

	public TipoFuncao() {
		parametros = new ArrayList<>();
	}

	public TipoFuncao getPai() {
		return pai;
	}

	public abstract void encerrar() throws ChecagemException;

	public abstract void preParametro() throws ChecagemException;

	public boolean excluir(Sentenca s) throws ChecagemException {
		if (!parametros.contains(s)) {
			throw new ChecagemException(getClass(), "Nao contem >>> " + s);
		}
		return parametros.remove(s);
	}

	public void addParam(Sentenca sentenca) throws ChecagemException {
		addParamImpl(sentenca);
	}

	protected void addParamImpl(Sentenca sentenca) throws ChecagemException {
		if (sentenca == null) {
			throw new ChecagemException(getClass(), "Sentenca nula");
		}
		if (sentenca == this) {
			throw new ChecagemException(getClass(), "Sentenca tentando adicionar a si proprio");
		}
		if (sentenca instanceof TipoFuncao) {
			TipoFuncao funcao = (TipoFuncao) sentenca;
			if (funcao.pai != null) {
				funcao.pai.excluir(sentenca);
			}
			funcao.pai = this;
		}
		parametros.add(sentenca);
	}

	public Sentenca excluirUltimoParametro() throws ChecagemException {
		if (parametros.isEmpty()) {
			throw new ChecagemException(getClass(), "Nenhum parametro definido");
		}
		modoInsercao = true;
		Sentenca sentenca = parametros.remove(parametros.size() - 1);
		if (sentenca instanceof TipoFuncao) {
			TipoFuncao funcao = (TipoFuncao) sentenca;
			funcao.pai = null;
		}
		return sentenca;
	}

	public Sentenca getUltimoParametro() throws ChecagemException {
		if (parametros.isEmpty()) {
			throw new ChecagemException(getClass(), "Nenhum parametro definido");
		}
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
		if (!(object instanceof ListaEncadeada)) {
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