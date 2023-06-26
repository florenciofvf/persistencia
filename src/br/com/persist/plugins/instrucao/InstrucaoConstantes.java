package br.com.persist.plugins.instrucao;

import java.awt.GraphicsEnvironment;

public class InstrucaoConstantes {
	protected static final String[] FONTES = GraphicsEnvironment.getLocalGraphicsEnvironment()
			.getAvailableFontFamilyNames();
	public static final String LABEL_INSTRUCAO_MIN = "label.instrucao_min";
	public static final String PAINEL_INSTRUCAO = "PAINEL INSTRUCAO";
	public static final String LABEL_INSTRUCAO = "label.instrucao";
	public static final String FUNCAO_NATIVA = "funcao_nativa";
	public static final String DEC_VARIAVEL = "dec_variavel";
	public static final String PREFIXO_METODO_NATIVO = "@@@";
	public static final String PREFIXO_INSTRUCAO = "$$";
	public static final String INSTRUCAO = "instrucao";
	public static final String IGNORADOS = "ignorados";
	public static final String PREFIXO_METODO = "@@";
	public static final String PREFIXO_PARAM = "##";
	public static final String PREFIXO_VAR = "%%";
	public static final String FUNCAO = "funcao";
	public static final String ESPACO = " ";

	public static final String PUSH_BIG_INTEGER = "push_big_integer";
	public static final String PUSH_BIG_DECIMAL = "push_big_decimal";
	public static final String PUSH_STRING = "push_string";

	public static final String BIG_INTEGER = "big_integer";
	public static final String BIG_DECIMAL = "big_decimal";
	public static final String STRING = "string";

	public static final String DECLARE_VAR = "declare_var";
	public static final String MODIFIC_VAR = "modific_var";
	public static final String LOAD_PAR = "load_par";
	public static final String LOAD_VAR = "load_var";
	public static final String RETURN = "return";
	public static final String INVOKE = "invoke";
	public static final String IF_EQ = "ifeq";
	public static final String GOTO = "goto";
	public static final String NEG = "neg";
	public static final String VAR = "var";
	public static final String VAL = "val";
	public static final String CR = "\\r";
	public static final String LF = "\\n";
	public static final String IF = "if";

	public static final String ADD = "add";
	public static final String SUB = "sub";
	public static final String MUL = "mul";
	public static final String POW = "pow";
	public static final String DIV = "div";
	public static final String REM = "rem";

	public static final String MENOR_I = "le";
	public static final String MAIOR_I = "ge";
	public static final String MENOR = "lt";
	public static final String MAIOR = "gt";
	public static final String IGUAL = "eq";
	public static final String DIFF = "ne";

	public static final String AND = "and";
	public static final String XOR = "xor";
	public static final String OR = "or";

	private InstrucaoConstantes() {
	}
}