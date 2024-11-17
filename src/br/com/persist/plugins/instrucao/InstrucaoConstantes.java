package br.com.persist.plugins.instrucao;

import java.awt.Color;
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

	public static final String PREFIXO_FUNCAO_NATIVA = "funcao_nativa ";
	public static final Color COLOR_SEL = new Color(155, 100, 255);
	public static final Color COLOR_TAB = new Color(185, 185, 185);
	public static final Color COLOR_RET = new Color(175, 175, 175);
	public static final String PREFIXO_CONSTANTE = "constante ";
	public static final String PREFIXO_TIPO_VOID = "tipo_void";
	public static final String PREFIXO_PARAMETRO = "param ";
	public static final String PREFIXO_FUNCAO = "funcao ";

	private InstrucaoConstantes() {
	}
}