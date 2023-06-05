package br.com.persist.plugins.instrucao.cmpl;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public abstract class No {
	protected List<No> nos;
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
			no.parent = this;
			nos.add(no);
		}
		return this;
	}

	public No getParent() {
		return parent;
	}

	public No remove(No no) {
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

	public abstract int comprimento();

	public abstract void print(PrintWriter pw);
}