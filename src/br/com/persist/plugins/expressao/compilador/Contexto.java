package br.com.persist.plugins.expressao.compilador;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;

public abstract class Contexto {
	protected final List<Contexto> componentes;
	protected final List<Contexto> pilhaLocal;
	protected int indiceEstado;
	protected Contexto parent;
	protected Token token;

	protected Contexto(Token token) {
		componentes = new ArrayList<>();
		pilhaLocal = new ArrayList<>();
		this.token = token;
	}

	protected Contexto() {
		this(null);
	}

	public boolean isEmpty() {
		return componentes.isEmpty();
	}

	public Contexto getParent() {
		return parent;
	}

	public Contexto getUltimo() {
		return get(componentes.size() - 1);
	}

	public Contexto getPrimeiro() {
		return get(0);
	}

	public Contexto get(int indice) {
		if (indice >= 0 && indice < componentes.size()) {
			return componentes.get(indice);
		}
		return null;
	}

	public Contexto excluirUltimo() {
		Contexto ultimo = getUltimo();
		remove(ultimo);
		return ultimo;
	}

	public void add(Contexto c) {
		if (c != null) {
			if (c.parent != null) {
				c.parent.remove(c);
			}
			componentes.add(c);
			c.parent = this;
		}
	}

	public void remove(Contexto c) {
		if (c != null && c.parent == this) {
			componentes.remove(c);
			c.parent = null;
		}
	}

	public void processar(Compilador compilador, Token token) {

	}

	public void listar(List<Contexto> lista) {
		listarPre(lista);
		for (Contexto item : componentes) {
			item.listar(lista);
		}
		listarPos(lista);
	}

	protected void listarPre(List<Contexto> lista) {
	}

	protected void listarPos(List<Contexto> lista) {
	}

	public void configurarSaltos() throws ExpressaoException {
		configurarSaltosPre();
		for (Contexto item : componentes) {
			item.configurarSaltos();
		}
		configurarSaltosPos();
	}

	protected void configurarSaltosPre() throws ExpressaoException {
	}

	protected void configurarSaltosPos() throws ExpressaoException {
	}

	public void empilharLocal(List<Contexto> lista) {
		empilharLocalPre(lista);
		for (Contexto item : componentes) {
			item.empilharLocal(lista);
		}
		empilharLocalPos(lista);
	}

	public void empilharLocalIni() {
		pilhaLocal.clear();
		empilharLocal(pilhaLocal);
	}

	public List<Contexto> getPilhaLocal() {
		return pilhaLocal;
	}

	protected void empilharLocalPre(List<Contexto> lista) {
	}

	protected void empilharLocalPos(List<Contexto> lista) {
	}

	class AbreParentese implements TokenExec {
		public void processar(Compilador compilador, Token token) {
			if (token.isAbreParentese()) {
				ExpressaoContexto expressao = new ExpressaoContexto();
				compilador.setSelecionado(expressao);
				add(expressao);
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}

	class AbreChave implements TokenExec {
		final byte estrutura;

		AbreChave(byte estrutura) {
			this.estrutura = estrutura;
		}

		public void processar(Compilador compilador, Token token) {
			if (token.isAbreChave()) {
				InstrucoesContexto instrucoes = new InstrucoesContexto(estrutura);
				compilador.setSelecionado(instrucoes);
				add(instrucoes);
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}

	protected class PontoEVirgula implements TokenExec {
		public void processar(Compilador compilador, Token token) {
			if (token.isPontoEVirgula()) {
				compilador.setSelecionado(parent);
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}
}