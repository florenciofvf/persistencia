package br.com.persist.plugins.instrucao.cmpl;

import java.io.PrintWriter;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;

class NoRaiz extends No {
	public NoRaiz() {
		super("raiz");
	}

	@Override
	public void normalizarEstrutura(Metodo metodo) throws InstrucaoException {
		throw new InstrucaoException(nome + " <<< normalizarEstrutura(Metodo metodo)", false);
	}

	@Override
	public void indexar(AtomicInteger atomic) throws InstrucaoException {
		throw new InstrucaoException(nome + " <<< indexar(AtomicInteger atomic)", false);
	}

	@Override
	public void print(PrintWriter pw) throws InstrucaoException {
		throw new InstrucaoException(nome + " <<< print(PrintWriter pw)", false);
	}
}

abstract class Comum extends No {
	public Comum(String nome) {
		super(Objects.requireNonNull(nome));
	}

	public void normalizarEstrutura(Metodo metodo) throws InstrucaoException {
	}

	public void indexar(AtomicInteger atomic) throws InstrucaoException {
		indice = atomic.getAndIncrement();
	}

	public void print(PrintWriter pw) throws InstrucaoException {
		print(pw, nome);
	}
}

abstract class Desvio extends Comum {
	protected int salto;

	public Desvio(String nome) {
		super(nome);
	}

	@Override
	public void print(PrintWriter pw) throws InstrucaoException {
		print(pw, nome, "" + salto);
	}
}

class Param extends Comum {
	public Param(String nome) {
		super(nome);
	}

	@Override
	public void print(PrintWriter pw) {
		pw.println(InstrucaoConstantes.PREFIXO_PARAM + nome);
	}
}

class Return extends Comum {
	public Return() {
		super(InstrucaoConstantes.RETURN);
	}
}

class Neg extends Comum {
	public Neg() {
		super(InstrucaoConstantes.NEG);
	}
}

class Push extends Comum {
	final Atom atom;

	public Push(Atom atom) {
		super("push");
		this.atom = atom;
	}

	@Override
	public void print(PrintWriter pw) throws InstrucaoException {
		if (atom.isString()) {
			print(pw, InstrucaoConstantes.PUSH_STRING, atom.getValor());
		} else if (atom.isBigInteger()) {
			print(pw, InstrucaoConstantes.PUSH_BIG_INTEGER, atom.getValor());
		} else if (atom.isBigDecimal()) {
			print(pw, InstrucaoConstantes.PUSH_BIG_DECIMAL, atom.getValor());
		} else {
			throw new InstrucaoException(atom.getValor() + " <<< Atomico error", false);
		}
	}
}

class Load extends No {
	final Atom atom;
	Neg neg;

	public Load(Atom atom) {
		super("load");
		this.atom = atom;
	}

	@Override
	public void normalizarEstrutura(Metodo metodo) throws InstrucaoException {
		if (atom.isNegarVariavel()) {
			neg = new Neg();
		}
	}

	@Override
	public void indexar(AtomicInteger atomic) throws InstrucaoException {
		indice = atomic.getAndIncrement();
		if (neg != null) {
			neg.indexar(atomic);
		}
	}

	@Override
	public void print(PrintWriter pw) throws InstrucaoException {
		print(pw, InstrucaoConstantes.LOAD, atom.getValor());
		if (neg != null) {
			neg.print(pw);
		}
	}
}

class Invoke extends No {
	public Invoke(String nome) {
		super(nome);
	}

	@Override
	public void normalizarEstrutura(Metodo metodo) throws InstrucaoException {
		for (No no : nos) {
			no.normalizarEstrutura(metodo);
		}
	}

	@Override
	public void indexar(AtomicInteger atomic) throws InstrucaoException {
		for (No no : nos) {
			no.indexar(atomic);
		}
		indice = atomic.getAndIncrement();
	}

	@Override
	public void print(PrintWriter pw) throws InstrucaoException {
		for (No no : nos) {
			no.print(pw);
		}
		print(pw, InstrucaoConstantes.INVOKE, nome);
	}
}

class Expression extends No {
	final Atom atom;
	Neg neg;

	public Expression(Atom atom) {
		super("expression");
		this.atom = atom;
	}

	@Override
	public void normalizarEstrutura(Metodo metodo) throws InstrucaoException {
		checarOperandos();
		nos.get(0).normalizarEstrutura(metodo);
		if (atom.isNegarVariavel()) {
			neg = new Neg();
		}
	}

	@Override
	public void indexar(AtomicInteger atomic) throws InstrucaoException {
		checarOperandos();
		nos.get(0).indexar(atomic);
		if (neg != null) {
			neg.indexar(atomic);
		}
	}

	@Override
	public void print(PrintWriter pw) throws InstrucaoException {
		checarOperandos();
		nos.get(0).print(pw);
		if (neg != null) {
			neg.print(pw);
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

class Ifeq extends Desvio {
	public Ifeq() {
		super(InstrucaoConstantes.IF_EQ);
	}
}

class Goto extends Desvio {
	public Goto() {
		super(InstrucaoConstantes.GOTO);
	}
}