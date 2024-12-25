package br.com.persist.plugins.instrucao.processador;

import java.util.Objects;

public class Parametro {
	final String nome;
	final String head;
	final String tail;
	Object valor;
	int indice;

	public Parametro(String nome) {
		this.nome = nome;
		int pos = nome.indexOf(':');
		if (pos != -1) {
			head = nome.substring(1, pos);
			tail = nome.substring(pos + 1, nome.length() - 1);
		} else {
			head = null;
			tail = null;
		}
	}

	public boolean contem(String string) {
		if (nome.equals(string)) {
			return true;
		}
		if (head != null && tail != null) {
			return head.equals(string) || tail.equals(string);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(nome);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Parametro other = (Parametro) obj;
		return Objects.equals(nome, other.nome);
	}

	@Override
	public String toString() {
		return indice + ": " + nome + "=" + valor;
	}
}