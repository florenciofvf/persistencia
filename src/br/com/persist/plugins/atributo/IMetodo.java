package br.com.persist.plugins.atributo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IMetodo {
	private final List<String> invocacoes;
	private final String nome;
	private Method method;

	public IMetodo(String nome) {
		this.nome = Objects.requireNonNull(nome);
		invocacoes = new ArrayList<>();
	}

	public void addInvocacoes(List<String> invocacoes) {
		if (invocacoes != null) {
			this.invocacoes.addAll(invocacoes);
		}
	}

	public List<String> getInvocacoes() {
		return invocacoes;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public String getNome() {
		return nome;
	}

	@Override
	public String toString() {
		return nome;
	}
}