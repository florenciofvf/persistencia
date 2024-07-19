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

	public static final String PREFIXO_FUNCAO_NATIVA = "funcao_nativa";
	public static final String PREFIXO_INSTRUCAO = "instrucao";
	public static final String PREFIXO_PARAMETRO = "parametro";
	public static final String PREFIXO_FUNCAO = "funcao";

	private InstrucaoConstantes() {
	}
}