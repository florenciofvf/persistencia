package br.com.persist.plugins.instrucao.cmpl;

import java.util.ArrayList;
import java.util.List;

public class PilhaNo {
	private final List<No> nos;

	public PilhaNo() {
		nos = new ArrayList<>();
	}

	public No peek() {
		return nos.get(nos.size() - 1);
	}

	public void push(No no) {
		if (no != null) {
			nos.add(no);
		}
	}

	public No pop() {
		return nos.remove(nos.size() - 1);
	}

	public int size() {
		return nos.size();
	}
}