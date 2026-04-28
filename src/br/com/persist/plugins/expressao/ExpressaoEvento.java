package br.com.persist.plugins.expressao;

import br.com.persist.assistencia.Evento;

public class ExpressaoEvento implements Evento {
	public static final String BIBLIO_PARA_OBJETO_RESPONSE = "expressao.biblio.paraObjeto.response";
	public static final String BIBLIO_PARA_OBJETO_REQUEST = "expressao.biblio.paraObjeto.request";

	private ExpressaoEvento() {
	}
}