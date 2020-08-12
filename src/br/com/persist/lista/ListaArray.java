package br.com.persist.lista;

import java.util.ArrayList;

public class ListaArray<E> extends ArrayList<E> {
	private static final long serialVersionUID = 1L;
	private transient ListaArrayListener listener;

	@Override
	public boolean add(E e) {
		boolean b = super.add(e);

		if (listener != null) {
			listener.adicionado(size() - 1);
		}

		return b;
	}

	@Override
	public E remove(int index) {
		E e = super.remove(index);

		if (listener != null) {
			listener.excluido(index);
		}

		return e;
	}

	@Override
	public void clear() {
		while (!isEmpty()) {
			remove(0);
		}
	}

	public ListaArrayListener getListener() {
		return listener;
	}

	public void setListener(ListaArrayListener listener) {
		this.listener = listener;
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}