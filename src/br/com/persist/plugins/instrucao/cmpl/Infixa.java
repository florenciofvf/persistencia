package br.com.persist.plugins.instrucao.cmpl;

import java.io.PrintWriter;

public abstract class Infixa extends No {
	protected short matematico1 = 100;
	protected short matematico2 = 120;
	protected short matematico3 = 140;
	protected short comparacao1 = 200;
	protected short logico1 = 300;

	public Infixa(String nome) {
		super(nome);
	}

	@Override
	public int totalInstrucoes() {
		return 0;
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

	@Override
	public void print(PrintWriter pw) {
		// TODO Auto-generated method stub
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

	@Override
	public void print(PrintWriter pw) {
		// TODO Auto-generated method stub
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

	@Override
	public void print(PrintWriter pw) {
		// TODO Auto-generated method stub
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

	@Override
	public void print(PrintWriter pw) {
		// TODO Auto-generated method stub
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

	@Override
	public void print(PrintWriter pw) {
		// TODO Auto-generated method stub
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

	@Override
	public void print(PrintWriter pw) {
		// TODO Auto-generated method stub
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

	@Override
	public void print(PrintWriter pw) {
		// TODO Auto-generated method stub
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

	@Override
	public void print(PrintWriter pw) {
		// TODO Auto-generated method stub
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

	@Override
	public void print(PrintWriter pw) {
		// TODO Auto-generated method stub
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

	@Override
	public void print(PrintWriter pw) {
		// TODO Auto-generated method stub
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

	@Override
	public void print(PrintWriter pw) {
		// TODO Auto-generated method stub
	}
}

class Oux extends Infixa {
	public Oux() {
		super("xor");
	}

	@Override
	public short getPrioridade() {
		return logico1;
	}

	@Override
	public Infixa clonar() {
		return new Oux();
	}

	@Override
	public void print(PrintWriter pw) {
		// TODO Auto-generated method stub
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

	@Override
	public void print(PrintWriter pw) {
		// TODO Auto-generated method stub
	}
}

class Ou extends Infixa {
	public Ou() {
		super("or");
	}

	@Override
	public short getPrioridade() {
		return logico1;
	}

	@Override
	public Infixa clonar() {
		return new Ou();
	}

	@Override
	public void print(PrintWriter pw) {
		// TODO Auto-generated method stub
	}
}