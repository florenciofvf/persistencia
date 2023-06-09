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
	public void configurarDesvio() throws InstrucaoException {
		throw new InstrucaoException(nome + " <<< configurarDesvio()", false);
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

	@Override
	public void normalizarEstrutura(Metodo metodo) throws InstrucaoException {
		checarOperandos0();
	}

	@Override
	public void configurarDesvio() throws InstrucaoException {
		checarOperandos0();
	}

	@Override
	public void indexar(AtomicInteger atomic) throws InstrucaoException {
		checarOperandos0();
		indice = atomic.getAndIncrement();
	}

	@Override
	public void print(PrintWriter pw) throws InstrucaoException {
		checarOperandos0();
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
		checarOperandos0();
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
	public void configurarDesvio() throws InstrucaoException {
		if (neg != null) {
			neg.configurarDesvio();
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
	public void configurarDesvio() throws InstrucaoException {
		for (No no : nos) {
			no.configurarDesvio();
		}
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
		checarOperandos1();
		nos.get(0).normalizarEstrutura(metodo);
		if (atom.isNegarVariavel()) {
			neg = new Neg();
		}
	}

	@Override
	public void indexar(AtomicInteger atomic) throws InstrucaoException {
		checarOperandos1();
		nos.get(0).indexar(atomic);
		if (neg != null) {
			neg.indexar(atomic);
		}
	}

	@Override
	public void configurarDesvio() throws InstrucaoException {
		checarOperandos1();
		nos.get(0).configurarDesvio();
		if (neg != null) {
			neg.configurarDesvio();
		}
	}

	@Override
	public void print(PrintWriter pw) throws InstrucaoException {
		checarOperandos1();
		nos.get(0).print(pw);
		if (neg != null) {
			neg.print(pw);
		}
	}
}

class If extends No {
	Goto gotoFinalBody = new Goto();
	Goto gotoFinalElse = new Goto();
	Ifeq ifeq = new Ifeq();
	No saltoFinalBody;
	No saltoFinalElse;

	public If() {
		super(InstrucaoConstantes.IF);
	}

	@Override
	public void normalizarEstrutura(Metodo metodo) throws InstrucaoException {
		checarOperandos3();
		No condicao = nos.get(0);
		No bodyIf = nos.get(1);
		No bodyElse = nos.get(2);

		saltoFinalBody = proximo();
		if (saltoFinalBody == null) {
			saltoFinalBody = metodo.getReturn();
		}

		if (parent instanceof If) {
			gotoFinalElse = null;
		} else {
			saltoFinalElse = proximo();
		}

		condicao.normalizarEstrutura(metodo);
		bodyIf.normalizarEstrutura(metodo);
		bodyElse.normalizarEstrutura(metodo);
	}

	private No proximo() {
		No no = this;
		while (no != null) {
			if (!(no instanceof If)) {
				break;
			}
			no = no.parent;
		}
		return no;
	}

	@Override
	public void indexar(AtomicInteger atomic) throws InstrucaoException {
		checarOperandos3();
		No condicao = nos.get(0);
		No bodyIf = nos.get(1);
		No bodyElse = nos.get(2);

		condicao.indexar(atomic);
		ifeq.indexar(atomic);
		bodyIf.indexar(atomic);
		gotoFinalBody.indexar(atomic);
		bodyElse.indexar(atomic);
		if (saltoFinalElse != null) {
			gotoFinalElse.indexar(atomic);
		}
	}

	@Override
	public void configurarDesvio() throws InstrucaoException {
		checarOperandos3();
		No condicao = nos.get(0);
		No bodyIf = nos.get(1);
		No bodyElse = nos.get(2);

		condicao.configurarDesvio();
		bodyElse.configDesvio(ifeq);
		bodyIf.configurarDesvio();
		gotoFinalBody.salto = saltoFinalBody.indice;
		bodyElse.configurarDesvio();
		if (saltoFinalElse != null) {
			gotoFinalElse.salto = saltoFinalElse.indice;
		}
	}

	@Override
	public void print(PrintWriter pw) throws InstrucaoException {
		checarOperandos3();
		No condicao = nos.get(0);
		No bodyIf = nos.get(1);
		No bodyElse = nos.get(2);

		condicao.print(pw);
		ifeq.print(pw);
		bodyIf.print(pw);
		gotoFinalBody.print(pw);
		bodyElse.print(pw);
		if (saltoFinalElse != null) {
			gotoFinalElse.print(pw);
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