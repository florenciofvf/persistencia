package br.com.persist.formulario;

import br.com.persist.assistencia.Evento;

public class FormularioEvento implements Evento {
	public static final String LISTA_BIBLIO_RESPONSE = "formulario.lista_biblio.response";
	public static final String LISTA_BIBLIO_REQUEST = "formulario.lista_biblio.request";
	public static final String FECHAR_FORMULARIO = "fechar_formulario";
	public static final String FECHAR_CONEXOES = "fechar_conexoes";

	private FormularioEvento() {
	}
}