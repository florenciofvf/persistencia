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
	private final boolean negarExpressao;

	public Expression(boolean negarExpressao) {
		super("expression");
		this.negarExpressao = negarExpressao;
	}

	@Override
	public int totalInstrucoes() throws InstrucaoException {
		checarOperandos();
		int total = nos.get(0).totalInstrucoes();
		if (negarExpressao) {
			total++;
		}
		return total;
	}

	@Override
	public void print(PrintWriter pw) throws InstrucaoException {
		checarOperandos();
		nos.get(0).print(pw);
		if (negarExpressao) {
			pw.println(InstrucaoConstantes.PREFIXO_INSTRUCAO + InstrucaoConstantes.NEG);
		}
	}

	private void checarOperandos() throws InstrucaoException {
		if (nos.size() != 1) {
			throw new InstrucaoException(nome + " <<< Total de operandos incorreto", false);
		}
	}
}