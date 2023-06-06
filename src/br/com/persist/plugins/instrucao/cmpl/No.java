package br.com.persist.plugins.instrucao.cmpl;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public abstract class No {
	protected final List<No> nos;
	protected String nome;
	protected No parent;

	public No(String nome) {
		nos = new ArrayList<>();
		this.nome = nome;
	}

	public No() {
		this(null);
	}

	public No add(No no) {
		if (no != null) {
			if (no.parent != null) {
				no.parent.remove(no);
			}
			no.parent = this;
			nos.add(no);
		}
		return this;
	}

	public No get(int indice) {
		return nos.get(indice);
	}

	public No getUltimoNo() {
		return nos.get(nos.size() - 1);
	}

	public No excluirUltimoNo() {
		No no = nos.remove(nos.size() - 1);
		no.parent = null;
		return no;
	}

	public No getParent() {
		return parent;
	}

	public No remove(No no) {
		no.parent = null;
		nos.remove(no);
		return this;
	}

	public List<No> getNos() {
		return nos;
	}

	@Override
	public String toString() {
		return nome;
	}

	public abstract int totalInstrucoes();

	public abstract void print(PrintWriter pw);
}

class NoRaiz extends No {
	public NoRaiz() {
		super(null);
	}

	@Override
	public int totalInstrucoes() {
		return 0;
	}

	@Override
	public void print(PrintWriter pw) {
		throw new IllegalStateException();
	}
}