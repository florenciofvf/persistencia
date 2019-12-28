package br.com.persist.fmt;

public class Atomico {
	static final byte CHAVE_INI = 1;
	static final byte CHAVE_FIM = 2;
	static final byte COLCH_INI = 3;
	static final byte COLCH_FIM = 4;

	static final byte INVALIDO = 0;

	static final byte NUMERO = 5;
	static final byte LOGICO = 6;
	static final byte TEXTO = 7;

	static final byte DOIS_PONT = 8;
	static final byte VIRGULA = 9;

	Object valor;
	byte tipo;

	Atomico(byte tipo) {
		this.tipo = tipo;
	}

	Atomico(byte tipo, Object valor) {
		this.valor = valor;
		this.tipo = tipo;
	}
}