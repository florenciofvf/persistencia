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
			print(pw, InstrucaoConstantes.PUSH_BIG_DECIMAL, atom.getValorBigDecimal());
		} else {
			throw new InstrucaoException(atom.getValor() + " <<< Atomico error", false);
		}
	}
}

class LoadPar extends No {
	final Atom atom;
	Neg neg;

	public LoadPar(Atom atom) {
		super(InstrucaoConstantes.LOAD_PAR);
		this.atom = atom;
	}

	@Override
	public void normalizarEstrutura(Metodo metodo) throws InstrucaoException {
		if (atom.isNegar()) {
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
		print(pw, nome, atom.getValor());
		if (neg != null) {
			neg.print(pw);
		}
	}
}

class LoadVar extends No {
	final Atom atom;
	Neg neg;

	public LoadVar(Atom atom) {
		super(InstrucaoConstantes.LOAD_VAR);
		this.atom = atom;
	}

	@Override
	public void normalizarEstrutura(Metodo metodo) throws InstrucaoException {
		if (atom.isNegar()) {
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
		print(pw, nome, atom.getValor());
		if (neg != null) {
			neg.print(pw);
		}
	}
}

class Invoke extends No {
	final Atom atom;
	Neg neg;

	public Invoke(Atom atom) {
		super(atom.getValor());
		this.atom = atom;
	}

	@Override
	public void normalizarEstrutura(Metodo metodo) throws InstrucaoException {
		for (No no : nos) {
			no.normalizarEstrutura(metodo);
		}
		if (atom.isNegar()) {
			neg = new Neg();
		}
	}

	@Override
	public void indexar(AtomicInteger atomic) throws InstrucaoException {
		for (No no : nos) {
			no.indexar(atomic);
		}
		indice = atomic.getAndIncrement();
		if (neg != null) {
			neg.indexar(atomic);
		}
	}

	@Override
	public void configurarDesvio() throws InstrucaoException {
		for (No no : nos) {
			no.configurarDesvio();
		}
		if (neg != null) {
			neg.configurarDesvio();
		}
	}

	@Override
	public void print(PrintWriter pw) throws InstrucaoException {
		for (No no : nos) {
			no.print(pw);
		}
		print(pw, InstrucaoConstantes.INVOKE, nome);
		if (neg != null) {
			neg.print(pw);
		}
	}
}

class TailCall extends No {
	public TailCall() {
		super(InstrucaoConstantes.TAIL_CALL);
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
		print(pw, nome);
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
		if (atom.isNegar()) {
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

class DeclareVar extends No {
	final LoadVar loadVar;
	final Atom atom;

	public DeclareVar(Atom atom) {
		super(InstrucaoConstantes.DECLARE_VAR);
		loadVar = new LoadVar(atom);
		this.atom = atom;
	}

	@Override
	public void normalizarEstrutura(Metodo metodo) throws InstrucaoException {
		checarOperandos1();
		nos.get(0).normalizarEstrutura(metodo);
		loadVar.normalizarEstrutura(metodo);
	}

	@Override
	public void indexar(AtomicInteger atomic) throws InstrucaoException {
		checarOperandos1();
		nos.get(0).indexar(atomic);
		indice = atomic.getAndIncrement();
		loadVar.indexar(atomic);
	}

	@Override
	public void configurarDesvio() throws InstrucaoException {
		checarOperandos1();
		nos.get(0).configurarDesvio();
		loadVar.configurarDesvio();
	}

	@Override
	public void print(PrintWriter pw) throws InstrucaoException {
		checarOperandos1();
		nos.get(0).print(pw);
		print(pw, nome, atom.getValor());
		loadVar.print(pw);
	}
}

class ModificVar extends No {
	final LoadVar loadVar;
	final Atom atom;

	public ModificVar(Atom atom) {
		super(InstrucaoConstantes.MODIFIC_VAR);
		loadVar = new LoadVar(atom);
		this.atom = atom;
	}

	@Override
	public void normalizarEstrutura(Metodo metodo) throws InstrucaoException {
		checarOperandos1();
		nos.get(0).normalizarEstrutura(metodo);
		loadVar.normalizarEstrutura(metodo);
	}

	@Override
	public void indexar(AtomicInteger atomic) throws InstrucaoException {
		checarOperandos1();
		nos.get(0).indexar(atomic);
		indice = atomic.getAndIncrement();
		loadVar.indexar(atomic);
	}

	@Override
	public void configurarDesvio() throws InstrucaoException {
		checarOperandos1();
		nos.get(0).configurarDesvio();
		loadVar.configurarDesvio();
	}

	@Override
	public void print(PrintWriter pw) throws InstrucaoException {
		checarOperandos1();
		nos.get(0).print(pw);
		print(pw, nome, atom.getValor());
		loadVar.print(pw);
	}
}

class If extends No {
	SaltoFinal saltoFinalBody;
	Ifeq ifeq = new Ifeq();

	public If() {
		super(InstrucaoConstantes.IF);
	}

	@Override
	public void normalizarEstrutura(Metodo metodo) throws InstrucaoException {
		checarOperandos3();
		No condicao = nos.get(0);
		No bodyIf = nos.get(1);
		No bodyElse = nos.get(2);

		No container = containerSuperior();
		Goto gotoFinalBody = new Goto();
		if (container == null) {
			saltoFinalBody = new SaltoFinal(true, gotoFinalBody, metodo.getReturn());
		} else {
			No proximoApos = container.proximoApos(filhoDe(container, this));
			if (proximoApos == container) {
				saltoFinalBody = new SaltoFinal(true, gotoFinalBody, container);
			} else {
				saltoFinalBody = new SaltoFinal(false, gotoFinalBody, proximoApos);
			}
		}

		condicao.normalizarEstrutura(metodo);
		bodyIf.normalizarEstrutura(metodo);
		bodyElse.normalizarEstrutura(metodo);
	}

	class SaltoFinal {
		final boolean porIndice;
		final Desvio desvio;
		final No no;

		public SaltoFinal(boolean porIndice, Desvio desvio, No no) {
			this.porIndice = porIndice;
			this.desvio = desvio;
			this.no = no;
		}

		void indexar(AtomicInteger atomic) throws InstrucaoException {
			desvio.indexar(atomic);
		}

		void configurarDesvio() {
			if (porIndice) {
				desvio.salto = no.indice;
			} else {
				no.configDesvio(desvio);
			}
		}

		void print(PrintWriter pw) throws InstrucaoException {
			desvio.print(pw);
		}
	}

	private No containerSuperior() {
		No no = this;
		while (no != null) {
			if (!(no instanceof If)) {
				break;
			}
			no = no.parent;
		}
		return no;
	}

	private No filhoDe(No container, No no) {
		while (no != null) {
			if (no.parent == container) {
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
		saltoFinalBody.indexar(atomic);
		bodyElse.indexar(atomic);
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
		saltoFinalBody.configurarDesvio();
		bodyElse.configurarDesvio();
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
		saltoFinalBody.print(pw);
		bodyElse.print(pw);
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