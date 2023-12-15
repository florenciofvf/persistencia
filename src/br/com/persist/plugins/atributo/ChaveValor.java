package br.com.persist.plugins.atributo;

import java.util.Objects;

import br.com.persist.assistencia.Util;

public class ChaveValor {
	private final String chave;
	private final Object valor;

	public ChaveValor(String chave, Object valor) {
		this.chave = chave;
		this.valor = valor;
	}

	public String getChave() {
		return chave;
	}

	public Object getValor() {
		return valor;
	}

	@Override
	public int hashCode() {
		return Objects.hash(chave);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ChaveValor other = (ChaveValor) obj;
		return Objects.equals(chave, other.chave);
	}

	@Override
	public String toString() {
		return Util.citar2(chave);
	}
}