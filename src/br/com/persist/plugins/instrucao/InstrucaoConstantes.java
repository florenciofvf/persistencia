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

	public static final String DECLARE_VAR = "declare_var";
	public static final String CR = "\\r";
	public static final String LF = "\\n";

	private InstrucaoConstantes() {
	}
}