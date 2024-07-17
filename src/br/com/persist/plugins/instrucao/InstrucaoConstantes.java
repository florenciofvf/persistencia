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


	public static final String BIG_INTEGER = "big_integer";
	public static final String BIG_DECIMAL = "big_decimal";
	public static final String STRING = "string";

	public static final String LOAD_LISTA_VAZIA = "load_lista_vazia";
	public static final String LOAD_HEAD_LISTA = "load_head_lista";
	public static final String LOAD_TAIL_LISTA = "load_tail_lista";
	public static final String INVOKE_DIN = "invoke_dinamic";
	public static final String DECLARE_VAR = "declare_var";
	public static final String MODIFIC_VAR = "modific_var";
	public static final String ADD_LISTA = "add_lista";
	public static final String TAIL_CALL = "tailcall";
	public static final String LOAD_PAR = "load_par";
	public static final String LOAD_VAR = "load_var";
	public static final String LAMBDA = "lambda";
	public static final String INVOKE = "invoke";
	public static final String NEG = "neg";
	public static final String VAR = "var";
	public static final String VAL = "val";
	public static final String CR = "\\r";
	public static final String LF = "\\n";
	public static final String IF = "if";

	private InstrucaoConstantes() {
	}
}