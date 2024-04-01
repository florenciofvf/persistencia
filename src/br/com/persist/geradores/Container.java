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

	protected Container add(Objeto o) {
		if (o == null) {
			return this;
		}
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

	public boolean isEmpty() {
		return objetos.isEmpty();
	}

	public int size() {
		return objetos.size();
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		for (Objeto o : objetos) {
			o.gerar(tab + 1, pool);
		}
	}

	public JSInvocaProm criarJSInvocaProm(String string) {
		JSInvocaProm obj = new JSInvocaProm(string);
		add(obj);
		return obj;
	}

	public If criarIf(String condicao, Else elsee) {
		If se = new If(condicao, elsee);
		add(se);
		return se;
	}

	public ElseIf criarElseIf(String condicao) {
		ElseIf elseIf = new ElseIf(condicao);
		add(elseIf);
		return elseIf;
	}

	public For criarFor(String condicao) {
		For loop = new For(condicao);
		add(loop);
		return loop;
	}

	public Try criarTry(Catch catche) {
		Try tre = new Try(catche);
		add(tre);
		return tre;
	}

	public Container newLine() {
		add(new NewLine());
		return this;
	}

	public Container addString(String string) {
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

	public Container addReturn(String string) {
		add(new Retornar(string));
		return this;
	}

	public Container addReturn() {
		return addInstrucao("return");
	}

	public Container addEspaco() {
		add(new Espaco());
		return this;
	}
}