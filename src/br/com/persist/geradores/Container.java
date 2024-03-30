package br.com.persist.geradores;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.StringPool;

public abstract class Container extends Objeto {
	protected final List<Objeto> objetos;

	protected Container(String id) {
		super(id);
		objetos = new ArrayList<>();
	}

	public Container add(Objeto o) {
		if (o.parent != null) {
			o.parent.remover(o);
		}
		if (addInvalido(o)) {
			return this;
		}
		o.parent = this;
		objetos.add(o);
		return this;
	}

	private boolean addInvalido(Objeto o) {
		Objeto obj = this;
		while (obj != null) {
			if (obj == o) {
				return true;
			}
			obj = obj.parent;
		}
		return false;
	}

	public Container remover(Objeto o) {
		if (o.parent == this) {
			objetos.remove(o);
			o.parent = null;
		}
		return this;
	}

	public Objeto get(int indice) {
		if (indice >= 0 && indice < objetos.size()) {
			return objetos.get(indice);
		}
		return null;
	}

	public List<Objeto> getObjetos() {
		return objetos;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		for (Objeto o : objetos) {
			o.gerar(tab, pool);
		}
	}

	public Container ql() {
		add(new NewLine());
		return this;
	}

	public Container append(String string) {
		add(new Sequence(string));
		return this;
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
		add(new Importar(string));
		return this;
	}

	public Container addVarJS(String string) {
		add(new VarJS(string));
		return this;
	}

	public Container addReturn(String string) {
		add(new Retornar(string));
		return this;
	}
}