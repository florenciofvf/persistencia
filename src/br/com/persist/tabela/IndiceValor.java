package br.com.persist.tabela;

import java.util.List;
import java.util.Objects;

public class IndiceValor {
	public final Object valor;
	public final int indice;

	public IndiceValor(Object valor, int indice) {
		Objects.requireNonNull(valor);
		this.indice = indice;
		this.valor = valor;
	}

	public boolean igual(List<Object> registro) {
		Object objeto = registro.get(indice);
		return valor.equals(objeto);
	}
}