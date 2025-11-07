package br.com.persist.plugins.metadado;

import br.com.persist.assistencia.Evento;

public class MetadadoEvento implements Evento {
	public static final String ABRIR_EXPORTACAO_METADADO_FORM = "abrirExportacaoMetadadoForm";
	public static final String ABRIR_EXPORTACAO_METADADO_FICH = "abrirExportacaoMetadadoFich";
	public static final String ABRIR_IMPORTACAO_METADADO_FORM = "abrirImportacaoMetadadoForm";
	public static final String ABRIR_IMPORTACAO_METADADO_FICH = "abrirImportacaoMetadadoFich";
	public static final String EXPORTAR_METADADO_RAIZ_DIALOG = "exportarMetadadoRaizDialog";
	public static final String EXPORTAR_METADADO_RAIZ_FORM = "exportarMetadadoRaizForm";
	public static final String EXPORTAR_METADADO_RAIZ_FICH = "exportarMetadadoRaizFich";
	public static final String GET_METADADO_OBJETO = "GET_METADADO_OBJETO";
	public static final String ABRIR_METADADO = "ABRIR_METADADO";
	public static final String CIRCULAR = "CIRCULAR";
	public static final String CONEXAO = "CONEXAO";
	public static final String METODO = "METODO";

	private MetadadoEvento() {
	}
}