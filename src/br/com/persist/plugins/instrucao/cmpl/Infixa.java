package br.com.persist.plugins.instrucao.cmpl;

import java.io.PrintWriter;
import java.util.Objects;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;

public abstract class Infixa extends No {
	protected short matematico1 = 100;
	protected short matematico2 = 120;
	protected short matematico3 = 140;
	protected short comparacao1 = 200;
	protected short logico1 = 300;

	public Infixa(String nome) {
		super(Objects.requireNonNull(nome));
	}

	@Override
	public int totalInstrucoes() throws InstrucaoException {
		checarOperandos();
		int totalE = nos.get(0).totalInstrucoes();
		int totalD = nos.get(1).totalInstrucoes();
		return totalE + totalD + 1;
	}

	@Override
	public final void print(PrintWriter pw) throws InstrucaoException {
		checarOperandos();
		nos.get(0).print(pw);
		nos.get(1).print(pw);
		pw.println(InstrucaoConstantes.PREFIXO_INSTRUCAO + nome);
	}

	public abstract short getPrioridade();

	public abstract Infixa clonar();

	public boolean possuoPrioridadeSobre(No no) {
		if (no instanceof Infixa) {
			Infixa infixa = (Infixa) no;
			return getPrioridade() < infixa.getPrioridade();
		}
		return false;
	}

	public boolean valido() {
		return nos.size() == 2;
	}

	private void checarOperandos() throws InstrucaoException {
		if (!valido()) {
			throw new InstrucaoException(nome + " <<< Faltando operandos", false);
		}
	}
}

class Somar extends Infixa {
	public Somar() {
		super("add");
	}

	@Override
	public short getPrioridade() {
		return matematico3;
	}

	@Override
	public Infixa clonar() {
		return new Somar();
	}
}

class Subtrair extends Infixa {
	public Subtrair() {
		super("sub");
	}

	@Override
	public short getPrioridade() {
		return matematico3;
	}

	@Override
	public Infixa clonar() {
		return new Subtrair();
	}
}

class Multiplicar extends Infixa {
	public Multiplicar() {
		super("mul");
	}

	@Override
	public short getPrioridade() {
		return matematico2;
	}

	@Override
	public Infixa clonar() {
		return new Multiplicar();
	}
}

class Dividir extends Infixa {
	public Dividir() {
		super("div");
	}

	@Override
	public short getPrioridade() {
		return matematico2;
	}

	@Override
	public Infixa clonar() {
		return new Dividir();
	}
}

class Resto extends Infixa {
	public Resto() {
		super("rem");
	}

	@Override
	public short getPrioridade() {
		return matematico1;
	}

	@Override
	public Infixa clonar() {
		return new Resto();
	}
}

class Igual extends Infixa {
	public Igual() {
		super("eq");
	}

	@Override
	public short getPrioridade() {
		return comparacao1;
	}

	@Override
	public Infixa clonar() {
		return new Igual();
	}
}

class Diferente extends Infixa {
	public Diferente() {
		super("ne");
	}

	@Override
	public short getPrioridade() {
		return comparacao1;
	}

	@Override
	public Infixa clonar() {
		return new Diferente();
	}
}

class Menor extends Infixa {
	public Menor() {
		super("lt");
	}

	@Override
	public short getPrioridade() {
		return comparacao1;
	}

	@Override
	public Infixa clonar() {
		return new Menor();
	}
}

class MenorIgual extends Infixa {
	public MenorIgual() {
		super("le");
	}

	@Override
	public short getPrioridade() {
		return comparacao1;
	}

	@Override
	public Infixa clonar() {
		return new MenorIgual();
	}
}

class Maior extends Infixa {
	public Maior() {
		super("gt");
	}

	@Override
	public short getPrioridade() {
		return comparacao1;
	}

	@Override
	public Infixa clonar() {
		return new Maior();
	}
}

class MaiorIgual extends Infixa {
	public MaiorIgual() {
		super("ge");
	}

	@Override
	public short getPrioridade() {
		return comparacao1;
	}

	@Override
	public Infixa clonar() {
		return new MaiorIgual();
	}
}

class Xor extends Infixa {
	public Xor() {
		super("xor");
	}

	@Override
	public short getPrioridade() {
		return logico1;
	}

	@Override
	public Infixa clonar() {
		return new Xor();
	}
}

class And extends Infixa {
	public And() {
		super("and");
	}

	@Override
	public short getPrioridade() {
		return logico1;
	}

	@Override
	public Infixa clonar() {
		return new And();
	}
}

class Or extends Infixa {
	public Or() {
		super("or");
	}

	@Override
	public short getPrioridade() {
		return logico1;
	}

	@Override
	public Infixa clonar() {
		return new Or();
	}
}