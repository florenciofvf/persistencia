package br.com.persist.assistencia;

import java.util.Collection;
import java.util.Iterator;

public class Lista<E> implements Collection<E> {
	private int comprimento;
	private No<E> cabeca;
	private No<E> cauda;

	public E getCabeca() throws ListaException {
		if (comprimento == 0) {
			throw new ListaException("Lista Vazia.");
		}
		return cabeca.valor;
	}

	public Lista<E> getCauda() throws ListaException {
		if (comprimento == 0) {
			throw new ListaException("Lista Vazia.");
		}
		Lista<E> resposta = new Lista<>();
		if (comprimento == 1) {
			return resposta;
		}
		resposta.comprimento = comprimento - 1;
		resposta.add(cabeca.proximo.valor);
		resposta.cauda = cauda;
		return resposta;
	}

	@Override
	public int size() {
		return comprimento;
	}

	@Override
	public boolean isEmpty() {
		return comprimento == 0;
	}

	@Override
	public boolean contains(Object obj) {
		if (comprimento == 0) {
			return false;
		}
		No<E> no = cabeca;
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

	@Override
	public Iterator<E> iterator() {
		return null;
	}

	@Override
	public Object[] toArray() {
		return new Object[0];
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException("Nao suportado.");
	}

	@Override
	public boolean add(E e) {
		No<E> no = new No<>(e);
		if (cabeca == null) {
			cabeca = no;
		}
		if (cauda != null) {
			cauda.proximo = no;
		}
		cauda = no;
		comprimento++;
		return true;
	}

	@Override
	public boolean remove(Object o) {
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> colecao) {
		if (comprimento == 0 || colecao == null || colecao.isEmpty()) {
			return false;
		}
		for (Object object : colecao) {
			if (!contains(object)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> colecao) {
		boolean resposta = false;
		for (E e : colecao) {
			if (add(e)) {
				resposta = true;
			}
		}
		return resposta;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}

	@Override
	public void clear() {
		comprimento = 0;
		cabeca = null;
		cauda = null;
	}
}

class No<E> {
	protected final E valor;
	protected No<E> proximo;

	public No(E valor, No<E> proximo) {
		this.proximo = proximo;
		this.valor = valor;
	}

	public No(E valor) {
		this.valor = valor;
	}
}