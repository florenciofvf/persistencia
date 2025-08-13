package br.com.persist.plugins.instrucao.biblionativo;

import br.com.persist.plugins.instrucao.processador.Biblioteca;

public class IBiblio {
	private IBiblio() {
	}

	@Biblio(1)
	public static String name(Object biblio) {
		if (biblio instanceof Biblioteca) {
			String nome = ((Biblioteca) biblio).getNome();
			int pos = nome.lastIndexOf(".");
			return pos != -1 ? nome.substring(0, pos) : nome;
		}
		return "";
	}
}