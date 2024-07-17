package br.com.persist.plugins.instrucao;

import java.awt.GraphicsEnvironment;

public class InstrucaoConstantes {
	protected static final String[] FONTES = GraphicsEnvironment.getLocalGraphicsEnvironment()
			.getAvailableFontFamilyNames();
	public static final String LABEL_INSTRUCAO_MIN = "label.instrucao_min";
	public static final String PAINEL_INSTRUCAO = "PAINEL INSTRUCAO";
	public static final String LABEL_INSTRUCAO = "label.instrucao";
	public static final String FUNCAO_NATIVA = "function_native";
	public static final String INSTRUCAO = "instrucao";
	public static final String IGNORADOS = "ignorados";
	public static final String ESPACO = " ";

	public static final String PREFIXO_FUNCAO_NATIVA = "fun_nat";
	public static final String PREFIXO_INSTRUCAO = "ins";
	public static final String PREFIXO_PARAMETRO = "par";
	public static final String PREFIXO_FUNCAO = "fun";

	public static final String BIG_INTEGER = "big_integer";
	public static final String BIG_DECIMAL = "big_decimal";
	public static final String STRING = "string";

	private InstrucaoConstantes() {
	}
}