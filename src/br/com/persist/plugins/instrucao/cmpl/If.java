package br.com.persist.plugins.instrucao.cmpl;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;

public class If extends No {
	public If() {
		super(InstrucaoConstantes.IF);
	}

	@Override
	public int totalInstrucoes() {
		return 0;
	}

	@Override
	public void print(PrintWriter pw) {
	}
}

class Body extends No {
	public Body() {
		super(InstrucaoConstantes.BODY);
	}

	@Override
	public int totalInstrucoes() {
		return 0;
	}

	@Override
	public void print(PrintWriter pw) {
	}
}

class Else extends No {
	public Else() {
		super(InstrucaoConstantes.ELSE);
	}

	@Override
	public int totalInstrucoes() {
		return 0;
	}

	@Override
	public void print(PrintWriter pw) {
	}
}