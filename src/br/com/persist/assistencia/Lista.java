package br.com.persist.assistencia;

import java.util.Collection;

public class Lista {
	private int comprimento;
	private No cabeca;
	private No cauda;

	public void adicionar(Object e) {
		No no = new No(e);
		if (cabeca == null) {
			cabeca = no;
		}
		if (cauda != null) {
			cauda.proximo = no;
		}
		cauda = no;
		comprimento++;
	}

	public int getComprimento() {
		return comprimento;
	}

	public boolean isEmpty() {
		return comprimento == 0;
	}

	public boolean contem(Object obj) {
		if (comprimento == 0) {
			return false;
		}
		No no = cabeca;
		if (obj == null) {
			while (no != null) {
				if (no.valor == null) {
					return true;
				}
				no = no.proximo;
			}
		} else {
			while (no != null) {
				if (obj.equals(no.valor)) {
					return true;
				}
				no = no.proximo;
			}
		}
		return false;
	}

	public boolean contemTodos(Collection<?> colecao) {
		if (comprimento == 0 || colecao == null || colecao.isEmpty()) {
			return false;
		}
		for (Object object : colecao) {
			if (!contem(object)) {
				return false;
			}
		}
		return true;
	}
}

class No {
	protected final Object valor;
	protected No proximo;

	public No(Object valor, No proximo) {
		this.proximo = proximo;
		this.valor = valor;
	}

	public No(Object valor) {
		this.valor = valor;
	}
}