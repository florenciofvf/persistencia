package br.com.persist.plugins.atributo.aux;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.StringPool;

public abstract class Container {
	protected final List<Container> lista;

	protected Container() {
		lista = new ArrayList<>();
	}

	public void ql() {
		add(new Linha());
	}

	public void append(String string) {
		add(new Sequencia(string));
	}

	public void add(Container c) {
		if (c != null) {
			lista.add(c);
		}
	}

	public void addInstrucao(String string) {
		add(new Instrucao(string));
	}

	public void addComentario(String string) {
		add(new Comentario(string));
	}

	public void addFragmento(String string) {
		add(new Fragmento(string));
	}

	public void addImport(String string) {
		add(new Import(string));
	}

	public void addVar(String string) {
		add(new Var(string));
	}

	public void addReturn(String string) {
		addReturn("", string);
	}

	public void addReturn(String prefixo, String string) {
		add(new Return(prefixo, string));
	}

	public void gerar(int tab, StringPool pool) {
		for (Container c : lista) {
			c.gerar(tab, pool);
		}
	}
}