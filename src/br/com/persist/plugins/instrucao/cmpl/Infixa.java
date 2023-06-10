package br.com.persist.plugins.instrucao.cmpl;

import java.io.PrintWriter;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

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
	public void normalizarEstrutura(Metodo metodo) throws InstrucaoException {
		checarOperandos2();
		nos.get(0).normalizarEstrutura(metodo);
		nos.get(1).normalizarEstrutura(metodo);
	}

	@Override
	public void indexar(AtomicInteger atomic) throws InstrucaoException {
		checarOperandos2();
		nos.get(0).indexar(atomic);
		nos.get(1).indexar(atomic);
		indice = atomic.getAndIncrement();
	}

	@Override
	public void configurarDesvio() throws InstrucaoException {
		checarOperandos2();
		nos.get(0).configurarDesvio();
		nos.get(1).configurarDesvio();
	}

	@Override
	public final void print(PrintWriter pw) throws InstrucaoException {
		checarOperandos2();
		nos.get(0).print(pw);
		nos.get(1).print(pw);
		print(pw, nome);
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
}

class Somar extends Infixa {
	public Somar() {
		super(InstrucaoConstantes.ADD);
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
		super(InstrucaoConstantes.SUB);
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
		super(InstrucaoConstantes.MUL);
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
		super(InstrucaoConstantes.DIV);
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
		super(InstrucaoConstantes.REM);
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
		super(InstrucaoConstantes.IGUAL);
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
		super(InstrucaoConstantes.DIFF);
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
		super(InstrucaoConstantes.MENOR);
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
		super(InstrucaoConstantes.MENOR_I);
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
		super(InstrucaoConstantes.MAIOR);
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
		super(InstrucaoConstantes.MAIOR_I);
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

class And extends Infixa {
	public And() {
		super(InstrucaoConstantes.AND);
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

class Xor extends Infixa {
	public Xor() {
		super(InstrucaoConstantes.XOR);
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

class Or extends Infixa {
	public Or() {
		super(InstrucaoConstantes.OR);
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