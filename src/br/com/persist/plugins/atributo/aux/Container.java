package br.com.persist.plugins.atributo.aux;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.StringPool;

public abstract class Container {
	protected final List<Container> lista;

	protected Container() {
		lista = new ArrayList<>();
	}

	public Container ql() {
		add(new Linha());
		return this;
	}

	public Container append(String string) {
		add(new Sequencia(string));
		return this;
	}

	public Container add(Container c) {
		if (c != null) {
			lista.add(c);
		}
		return this;
	}

	public Container get(int indice) {
		if (indice >= 0 && indice < lista.size()) {
			return lista.get(indice);
		}
		return null;
	}

	public Container addInstrucao(String string) {
		add(new Instrucao(string));
		return this;
	}

	public Container addComentario(String string) {
		add(new Comentario(string));
		return this;
	}

	public Container addFragmento(String string) {
		add(new Fragmento(string));
		return this;
	}

	public Container addImport(String string) {
		add(new Import(string));
		return this;
	}

	public Container addVar(String string) {
		add(new Var(string));
		return this;
	}

	public Container addReturn(String string) {
		addReturn("", string);
		return this;
	}

	public Container addReturn(String prefixo, String string) {
		add(new Return(prefixo, string));
		return this;
	}

	public void gerar(int tab, StringPool pool) {
		for (Container c : lista) {
			c.gerar(tab, pool);
		}
	}
}