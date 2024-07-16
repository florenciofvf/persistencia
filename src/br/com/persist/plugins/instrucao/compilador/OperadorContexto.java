package br.com.persist.plugins.instrucao.compilador;

import java.util.concurrent.atomic.AtomicInteger;

public class OperadorContexto extends Container {
	private final String id;

	public OperadorContexto(Token token) {
		this.id = token.getString();
		this.token = token;
	}

	public String getId() {
		return id;
	}

	public short getPrioridade() {
		if (igual("%")) {
			return 100;
		}
		if (igual("*", "/")) {
			return 120;
		}
		if (igual("+", "-")) {
			return 140;
		}
		if (igual("=", "!=", "<", ">", "<=", ">=")) {
			return 200;
		}
		if (igual("&", "|", "^")) {
			return 300;
		}
		throw new IllegalStateException(id);
	}

	private boolean igual(String... strings) {
		for (String string : strings) {
			if (string.equals(id)) {
				return true;
			}
		}
		return false;
	}

	public boolean possuoPrioridadeSobre(OperadorContexto operador) {
		return getPrioridade() < operador.getPrioridade();
	}

	@Override
	public void indexar(AtomicInteger atomic) {
		super.indexar(atomic);
		indice = atomic.getAndIncrement();
	}

	@Override
	public String toString() {
		return id;
	}
}