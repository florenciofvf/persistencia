package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Token.Tipo;

public class NumeroContexto extends Container {
	public static final String PUSH_BIG_INTEGER = "push_big_integer";
	public static final String PUSH_BIG_DECIMAL = "push_big_decimal";

	private final String numero;

	public NumeroContexto(Token token) {
		this.numero = token.getString();
		this.token = token;
	}

	public String getNumero() {
		return numero;
	}

	@Override
	public void indexar(Indexador indexador) {
		sequencia = indexador.get2();
		indexarNegativo(indexador);
	}

	@Override
	public void salvar(Compilador compilador, PrintWriter pw) throws InstrucaoException {
		if (token.tipo == Tipo.INTEIRO) {
			print(pw, PUSH_BIG_INTEGER, numero);
		} else {
			print(pw, PUSH_BIG_DECIMAL, numero);
		}
		salvarNegativo(compilador, pw);
	}

	@Override
	public String toString() {
		return numero;
	}
}