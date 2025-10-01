package br.com.persist.plugins.objeto;

import java.awt.Font;

public class ObjetoConstantes {
	protected static final String[] NIVEIS_TRANSPARENCIA_FORM = { "0.0", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6",
			"0.7", "0.8", "0.9", "1.0" };
	public static final String ALTURMA_MINIMA_FORMULARIO_MAXIMO_DE_REGISTROS = "ALTURMA_MINIMA_FORMULARIO_MAXIMO_DE_REGISTROS";
	public static final String ALTURMA_MINIMA_FORMULARIO_SCROLL_HOR_VISIVEL = "ALTURMA_MINIMA_FORMULARIO_SCROLL_HOR_VISIVEL";
	public static final String ALTURMA_MINIMA_FORMULARIO_SEM_REGISTROS = "ALTURMA_MINIMA_FORMULARIO_SEM_REGISTROS";
	public static final String DELTA_AJUSTE_FORM_DISTANCIA_VERTICAL = "DELTA_AJUSTE_FORM_DISTANCIA_VERTICAL";
	public static final String DESTACAR_PROPRIO_LARGURA_INTERNAL = "DESTACAR_PROPRIO_LARGURA_INTERNAL";
	public static final String DESTACAR_PROPRIO_ALTURA_INTERNAL = "DESTACAR_PROPRIO_ALTURA_INTERNAL";
	public static final String DELTA_X_AJUSTE_FORM_OBJETO = "DELTA_X_AJUSTE_FORM_OBJETO";
	public static final String DELTA_Y_AJUSTE_FORM_OBJETO = "DELTA_Y_AJUSTE_FORM_OBJETO";
	public static final String CHAVE_MENSAGEM_VI = "msg.arquivo_vinculo_inexistente";
	public static final Font FONT_HORAS = new Font(Font.MONOSPACED, Font.BOLD, 90);
	public static final String LABEL_ATUALIZAR_AUTO = "label.atualizar_auto";
	public static final String LABEL_VINCULO = "label.aplicar_arq_vinculo";
	public static final String PESQUISA_INVERTIDO = "pesquisa_invertido";
	public static final String LABEL_DESKTOP_MIN = "label.desktop_min";
	public static final int TIPO_DESTAC_FORM_VISIBILIDADE = 0;
	public static final int TIPO_DESTAC_FORM_COR_FUNDO = 1;
	public static final int TIPO_DESTAC_FORM_TITULO = 2;
	public static final int TIPO_CONTAINER_FORMULARIO = 0;
	public static final int TIPO_CONTAINER_FICHARIO = 1;
	public static final int TIPO_CONTAINER_DESKTOP = 2;
	public static final int TIPO_CONTAINER_PROPRIO = 3;
	public static final String INVISIVEL = "invisivel";
	public static final String PESQUISA = "pesquisa";
	public static final String ERROR = "error";
	public static final byte ARRASTO = 1;
	public static final byte RELACAO = 3;
	public static final byte SELECAO = 5;
	public static final byte ROTULOS = 7;

	private ObjetoConstantes() {
	}
}