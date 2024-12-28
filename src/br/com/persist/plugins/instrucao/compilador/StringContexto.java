package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoException;

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
	public void indexar(Indexador indexador) {
		sequencia = indexador.get2();
	}

	@Override
	public void salvar(Compilador compilador, PrintWriter pw) throws InstrucaoException {
		super.salvar(compilador, pw);
		print(pw, PUSH_STRING, string);
	}

	@Override
	public String toString() {
		return string;
	}
}