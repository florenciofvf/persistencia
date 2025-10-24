package br.com.persist.plugins.atributo;

import java.util.Objects;

public class Metodo {
	private final String nome;
	private String retorno;

	public Metodo(String nome) {
		this.nome = Objects.requireNonNull(nome);
	}

	public String getNome() {
		return nome;
	}

	public boolean isConstrutor(String classe) {
		return nome.equals(classe);
	}

	public boolean isGet() {
		return nome.startsWith("get");
	}

	public boolean isSet() {
		return nome.startsWith("set");
	}

	public boolean isIs() {
		return nome.startsWith("is");
	}

	public String getRetorno() {
		if (retorno == null) {
			return "";
		}
		return retorno + " resp = ";
	}

	public void setRetorno(String retorno) {
		if (retorno != null && !retorno.contains(" void ")) {
			this.retorno = retorno;
		}
	}

	@Override
	public String toString() {
		return nome;
	}
}