package br.com.persist.plugins.instrucao.biblionativo;

import br.com.persist.plugins.instrucao.processador.Biblioteca;

public class IBiblio {
	private IBiblio() {
	}

	@Biblio(1)
	public static String name(Object biblio) {
		if (biblio instanceof Biblioteca) {
			return ((Biblioteca) biblio).getNome();
		}
		return "";
	}
}