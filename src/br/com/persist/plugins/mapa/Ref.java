package br.com.persist.plugins.mapa;

import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.Util;

public class Ref {
	private final String nome;

	public Ref(String nome) throws ArgumentoException {
		if (Util.isEmpty(nome)) {
			throw new ArgumentoException("Nome da referencia vazia.");
		}
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public String toString() {
		return nome;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ref other = (Ref) obj;
		if (nome == null) {
			if (other.nome != null) {
				return false;
			}
		} else if (!nome.equals(other.nome)) {
			return false;
		}
		return true;
	}
}