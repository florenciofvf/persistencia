package br.com.persist.plugins.arquivo;

import br.com.persist.assistencia.Evento;

public class ArquivoEvento implements Evento {
	public static final String ABRIR_ARQUIVO = "abrir_arquivo";
	public static final String FICHARIO = "fichario";

	private ArquivoEvento() {
	}
}