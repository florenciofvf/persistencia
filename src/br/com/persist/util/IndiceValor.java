package br.com.persist.util;

import java.util.List;
import java.util.Objects;

public class IndiceValor {
	public final Object valor;
	public final int indice;

	public IndiceValor(int indice, Object valor) {
		Objects.requireNonNull(valor);
		this.indice = indice;
		this.valor = valor;
	}

	public boolean igual(List<Object> registro) {
		Object objeto = registro.get(indice);
		return valor.equals(objeto);
	}
}