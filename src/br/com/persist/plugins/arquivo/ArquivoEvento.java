package br.com.persist.plugins.arquivo;

import br.com.persist.assistencia.Evento;

public class ArquivoEvento implements Evento {
	public static final String TIPO_CONTAINER = "tipo_container";
	public static final String ABRIR_ARQUIVO = "abrir_arquivo";

	private ArquivoEvento() {
	}
}