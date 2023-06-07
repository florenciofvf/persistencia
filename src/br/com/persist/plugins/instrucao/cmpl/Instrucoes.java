package br.com.persist.plugins.instrucao.cmpl;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;

class Return extends No {
	public Return() {
		super(InstrucaoConstantes.RETURN);
	}

	@Override
	public int totalInstrucoes() {
		return 1;
	}

	@Override
	public void print(PrintWriter pw) {
		pw.println(InstrucaoConstantes.PREFIXO_INSTRUCAO + nome);
	}
}

class Push extends No {
	final Atom atom;

	public Push(Atom atom) {
		super("push");
		this.atom = atom;
	}

	@Override
	public int totalInstrucoes() {
		return 1;
	}

	@Override
	public void print(PrintWriter pw) {
		if (atom.isString()) {
			pw.print(InstrucaoConstantes.PREFIXO_INSTRUCAO + InstrucaoConstantes.PUSH_STRING);
		} else if (atom.isBigInteger()) {
			pw.print(InstrucaoConstantes.PREFIXO_INSTRUCAO + InstrucaoConstantes.PUSH_BIG_INTEGER);
		} else if (atom.isBigDecimal()) {
			pw.print(InstrucaoConstantes.PREFIXO_INSTRUCAO + InstrucaoConstantes.PUSH_BIG_DECIMAL);
		} else {
			throw new IllegalStateException();
		}
		pw.println(InstrucaoConstantes.ESPACO + atom.getValor());
	}
}

class Load extends No {
	final Atom atom;

	public Load(Atom atom) {
		super("load");
		this.atom = atom;
	}

	@Override
	public int totalInstrucoes() {
		return atom.isNegarVariavel() ? 2 : 1;
	}

	@Override
	public void print(PrintWriter pw) {
		pw.print(InstrucaoConstantes.PREFIXO_INSTRUCAO + InstrucaoConstantes.LOAD);
		pw.println(InstrucaoConstantes.ESPACO + atom.getValor());
		if (atom.isNegarVariavel()) {
			pw.println(InstrucaoConstantes.PREFIXO_INSTRUCAO + InstrucaoConstantes.NEG);
		}
	}
}

class Invoke extends No {
	public Invoke(String nome) {
		super(nome);
	}

	@Override
	public int totalInstrucoes() throws InstrucaoException {
		int total = 0;
		for (No no : nos) {
			total += no.totalInstrucoes();
		}
		return total + 1;
	}

	@Override
	public void print(PrintWriter pw) throws InstrucaoException {
		for (No no : nos) {
			no.print(pw);
		}
		pw.print(InstrucaoConstantes.PREFIXO_INSTRUCAO + InstrucaoConstantes.INVOKE);
		pw.println(InstrucaoConstantes.ESPACO + nome);
	}
}