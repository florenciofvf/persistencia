package br.com.persist.util;

import java.util.ArrayList;

import br.com.persist.listener.ListaListener;

public class ListaArray<E> extends ArrayList<E> {
	private static final long serialVersionUID = 1L;
	private transient ListaListener listener;

	@Override
	public void clear() {
		super.clear();

		if (listener != null) {
			listener.limpo();
		}
	}

	@Override
	public boolean add(E e) {
		boolean b = super.add(e);

		if (listener != null) {
			listener.adicionado();
		}

		return b;
	}

	public ListaListener getListener() {
		return listener;
	}

	public void setListener(ListaListener listener) {
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