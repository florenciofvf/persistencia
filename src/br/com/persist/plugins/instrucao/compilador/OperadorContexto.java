package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class OperadorContexto extends Container {
	public static final String ADD_LISTA = "add_lista";
	public static final String ADD = "add";
	public static final String SUB = "sub";
	public static final String MUL = "mul";
	public static final String DIV = "div";
	public static final String REM = "rem";

	public static final String MENOR_IGUAL = "le";
	public static final String MAIOR_IGUAL = "ge";
	public static final String MENOR = "lt";
	public static final String MAIOR = "gt";
	public static final String IGUAL = "eq";
	public static final String DIFF = "ne";

	public static final String AND = "and";
	public static final String XOR = "xor";
	public static final String OR = "or";

	private final String id;

	public OperadorContexto(Token token) {
		this.id = token.getString();
		this.token = token;
	}

	public String getId() {
		return id;
	}

	public short getPrioridade() throws InstrucaoException {
		if (igual(":")) {
			return 50;
		}
		if (igual("%")) {
			return 100;
		}
		if (igual("*", "/")) {
			return 120;
		}
		if (igual("+", "-")) {
			return 140;
		}
		if (igual("=", "!=", "<", ">", "<=", ">=")) {
			return 200;
		}
		if (igual("&", "|", "^")) {
			return 300;
		}
		throw new InstrucaoException("Operador >>> " + id, false);
	}

	public String getCodigo() throws InstrucaoException {
		if (igual(":")) {
			return ADD_LISTA;
		}
		if (igual("%")) {
			return REM;
		}
		if (igual("*")) {
			return MUL;
		}
		if (igual("/")) {
			return DIV;
		}
		if (igual("+")) {
			return ADD;
		}
		if (igual("-")) {
			return SUB;
		}
		if (igual("=")) {
			return IGUAL;
		}
		if (igual("!=")) {
			return DIFF;
		}
		if (igual("<")) {
			return MENOR;
		}
		if (igual(">")) {
			return MAIOR;
		}
		if (igual("<=")) {
			return MENOR_IGUAL;
		}
		if (igual(">=")) {
			return MAIOR_IGUAL;
		}
		if (igual("&")) {
			return AND;
		}
		if (igual("|")) {
			return OR;
		}
		if (igual("^")) {
			return XOR;
		}
		throw new InstrucaoException("Operador >>> " + id, false);
	}

	private boolean igual(String... strings) {
		for (String string : strings) {
			if (string.equals(id)) {
				return true;
			}
		}
		return false;
	}

	public boolean possuoPrioridadeSobre(OperadorContexto operador) throws InstrucaoException {
		return getPrioridade() < operador.getPrioridade();
	}

	@Override
	public void indexar(Indexador indexador) {
		super.indexar(indexador);
		sequencia = indexador.get();
	}

	@Override
	public void salvar(PrintWriter pw) throws InstrucaoException {
		super.salvar(pw);
		print(pw, getCodigo());
	}

	@Override
	public String toString() {
		return id;
	}
}