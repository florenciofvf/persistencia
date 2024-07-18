package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

public class StringContexto extends Container {
	public static final String PUSH_STRING = "push_string";
	private final String string;

	public StringContexto(Token token) {
		this.string = token.getString();
		this.token = token;
	}

	public String getString() {
		return string;
	}

	@Override
	public void indexar(AtomicInteger atomic) {
		super.indexar(atomic);
		indice = atomic.getAndIncrement();
	}

	@Override
	public void salvar(PrintWriter pw) {
		super.salvar(pw);
		print(pw, PUSH_STRING, string);
	}

	@Override
	public String toString() {
		return string;
	}
}