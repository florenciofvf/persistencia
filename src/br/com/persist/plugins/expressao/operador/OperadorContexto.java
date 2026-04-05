package br.com.persist.plugins.expressao.operador;

import java.io.PrintWriter;
import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Context;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.TokenManager;

public class OperadorContexto extends Contexto {
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

	public OperadorContexto(Token operador) {
		super(operador);
	}

	public short getPrioridade() throws ExpressaoException {
		if (igual("%")) {
			return 100;
		}
		if (igual("*", "/")) {
			return 120;
		}
		if (igual("+", "-")) {
			return 140;
		}
		if (igual("==", "!=", "<", ">", "<=", ">=")) {
			return 200;
		}
		if (igual("&&", "||", "^")) {
			return 300;
		}
		if (igual(":")) {
			return 400;
		}
		throw new ExpressaoException("Operador >>> " + token.getString(), false);
	}

	public String getInstrucao() throws ExpressaoException {
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
		if (igual("==")) {
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
		if (igual("&&")) {
			return AND;
		}
		if (igual("||")) {
			return OR;
		}
		if (igual("^")) {
			return XOR;
		}
		if (igual(":")) {
			return ADD_LISTA;
		}
		throw new ExpressaoException("Operador >>> " + token.getString(), false);
	}

	private boolean igual(String... strings) {
		String operador = token.getString();
		for (String item : strings) {
			if (item.equals(operador)) {
				return true;
			}
		}
		return false;
	}

	public boolean possuoPrioridadeSobre(OperadorContexto operador) throws ExpressaoException {
		return getPrioridade() < operador.getPrioridade();
	}

	@Context("operador")
	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		tokenManager.invalidar(token);
	}

	@Override
	protected void empilharLocalPos(List<Contexto> lista) {
		lista.add(this);
	}

	@Override
	protected void listarPos(List<Contexto> lista) {
		lista.add(this);
	}

	@Override
	public void salvar(PrintWriter pw) throws ExpressaoException {
		print(pw, getInstrucao());
	}
}