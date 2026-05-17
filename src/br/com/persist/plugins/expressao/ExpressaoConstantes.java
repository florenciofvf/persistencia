package br.com.persist.plugins.expressao;

public class ExpressaoConstantes {
	public static final boolean LOGGER_PILHA_OPERANDO = get("logger_pilha_operando");
	public static final boolean LOGGER_PARENT_FUNCAO = get("logger_parent_funcao");
	public static final boolean LOGGER_PILHA_FUNCAO = get("logger_pilha_funcao");
	public static final boolean LOGGER_PROCESSADOR = get("logger_processador");
	public static final boolean LOGGER_INSTRUCAO = get("logger_instrucao");
	public static final boolean LOGGER_ENDERECO = get("logger_endereco");

	public static final String LABEL_EXPRESSAO_MIN = "label.expressao_min";
	public static final String PAINEL_EXPRESSAO = "PAINEL EXPRESSAO";
	public static final String LABEL_EXPRESSAO = "label.expressao";
	public static final String EXPRESSOES = "expressoes";
	public static final String EXPRESSAO = "expressao";
	public static final String IGNORADOS = "ignorados";
	public static final String ESPACO = " ";

	private ExpressaoConstantes() {
	}

	private static boolean get(String propriedade) {
		return "true".equals(System.getProperty("expressao." + propriedade));
	}
}