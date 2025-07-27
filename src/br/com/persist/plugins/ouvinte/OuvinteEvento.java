package br.com.persist.plugins.ouvinte;

import br.com.persist.assistencia.Evento;

public class OuvinteEvento implements Evento {
	public static final String GET_STRING = "OuvinteEvento.GET_STRING";
	public static final String GET_RESULT = "OuvinteEvento.GET_RESULT";

	private OuvinteEvento() {
	}
}