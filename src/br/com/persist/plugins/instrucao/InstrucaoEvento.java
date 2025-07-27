package br.com.persist.plugins.instrucao;

import br.com.persist.assistencia.Evento;

public class InstrucaoEvento implements Evento {
	public static final String BIBLIO_PARA_OBJETO_RESPONSE = "instrucao.biblio.paraObjeto.response";
	public static final String BIBLIO_PARA_OBJETO_REQUEST = "instrucao.biblio.paraObjeto.request";

	private InstrucaoEvento() {
	}
}