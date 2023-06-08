package br.com.persist.plugins.instrucao.cmpl;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;

class NoRaiz extends No {
	public NoRaiz() {
		super("raiz");
	}

	@Override
	public int totalInstrucoes() throws InstrucaoException {
		throw new InstrucaoException(nome + " <<< totalInstrucoes()", false);
	}

	@Override
	public void print(PrintWriter pw) throws InstrucaoException {
		throw new InstrucaoException(nome + " <<< print(PrintWriter pw)", false);
	}
}

class Param extends No {
	public Param(String nome) {
		super(nome);
	}

	@Override
	public int totalInstrucoes() {
		return 1;
	}

	@Override
	public void print(PrintWriter pw) {
		pw.println(InstrucaoConstantes.PREFIXO_PARAM + nome);
	}
}

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
	public void print(PrintWriter pw) throws InstrucaoException {
		if (atom.isString()) {
			pw.print(InstrucaoConstantes.PREFIXO_INSTRUCAO + InstrucaoConstantes.PUSH_STRING);
		} else if (atom.isBigInteger()) {
			pw.print(InstrucaoConstantes.PREFIXO_INSTRUCAO + InstrucaoConstantes.PUSH_BIG_INTEGER);
		} else if (atom.isBigDecimal()) {
			pw.print(InstrucaoConstantes.PREFIXO_INSTRUCAO + InstrucaoConstantes.PUSH_BIG_DECIMAL);
		} else {
			throw new InstrucaoException(atom.getValor() + " <<< Atomico error", false);
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

class Expression extends No {
	final Atom atom;

	public Expression(Atom atom) {
		super("expression");
		this.atom = atom;
	}

	@Override
	public int totalInstrucoes() throws InstrucaoException {
		checarOperandos();
		int total = nos.get(0).totalInstrucoes();
		if (atom.isNegarExpressao()) {
			total++;
		}
		return total;
	}

	@Override
	public void print(PrintWriter pw) throws InstrucaoException {
		checarOperandos();
		nos.get(0).print(pw);
		if (atom.isNegarExpressao()) {
			pw.println(InstrucaoConstantes.PREFIXO_INSTRUCAO + InstrucaoConstantes.NEG);
		}
	}

	private void checarOperandos() throws InstrucaoException {
		if (nos.size() != 1) {
			throw new InstrucaoException(nome + " <<< Total de operandos incorreto", false);
		}
	}
}

class If extends No {
	final Goto gotoBody = new Goto();
	final Goto gotoElse = new Goto();
	final Ifeq ifeq = new Ifeq();

	public If() {
		super(InstrucaoConstantes.IF);
	}

	@Override
	public int totalInstrucoes() throws InstrucaoException {
		checarOperandos();
		int total = nos.get(0).totalInstrucoes();
		total += ifeq.totalInstrucoes();
		total += nos.get(1).totalInstrucoes();
		total += gotoBody.totalInstrucoes();
		total += nos.get(2).totalInstrucoes();
		total += gotoElse.totalInstrucoes();
		return total;
	}

	@Override
	public void print(PrintWriter pw) throws InstrucaoException {
		checarOperandos();
		nos.get(0).print(pw);
		ifeq.print(pw);
		nos.get(1).print(pw);
		gotoBody.print(pw);
		nos.get(2).print(pw);
		gotoElse.print(pw);
	}

	public boolean valido() {
		return nos.size() == 3;
	}

	private void checarOperandos() throws InstrucaoException {
		if (!valido()) {
			throw new InstrucaoException(nome + " <<< Faltando operandos", false);
		}
	}
}

class Ifeq extends No {
	public Ifeq() {
		super(InstrucaoConstantes.IF_EQ);
	}

	@Override
	public int totalInstrucoes() {
		return 1;
	}

	@Override
	public void print(PrintWriter pw) {
		pw.print(InstrucaoConstantes.PREFIXO_INSTRUCAO + nome);
		pw.println(InstrucaoConstantes.ESPACO + indice);
	}
}

class Goto extends No {
	public Goto() {
		super(InstrucaoConstantes.GOTO);
	}

	@Override
	public int totalInstrucoes() {
		return 1;
	}

	@Override
	public void print(PrintWriter pw) {
		pw.print(InstrucaoConstantes.PREFIXO_INSTRUCAO + nome);
		pw.println(InstrucaoConstantes.ESPACO + indice);
	}
}