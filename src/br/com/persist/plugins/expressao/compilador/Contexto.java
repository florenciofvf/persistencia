package br.com.persist.plugins.expressao.compilador;

import java.util.ArrayList;
import java.util.List;

public abstract class Contexto {
	protected final List<Contexto> componentes;
	protected int indiceEstado;
	protected Contexto parent;

	protected Contexto() {
		componentes = new ArrayList<>();
	}

	public Contexto getParent() {
		return parent;
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