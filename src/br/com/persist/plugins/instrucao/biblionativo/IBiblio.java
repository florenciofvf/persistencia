package br.com.persist.plugins.instrucao.biblionativo;

import br.com.persist.plugins.instrucao.processador.Biblioteca;

public class IBiblio {
	private IBiblio() {
	}

	@Biblio(1)
	public static String name(Object biblio) {
		if (biblio instanceof Biblioteca) {
			String nome = ((Biblioteca) biblio).getNome();
			return getNome(nome);
		}
		return "";
	}

	@Biblio(2)
	public static String simpleName(Object biblio) {
		if (biblio instanceof Biblioteca) {
			String nome = ((Biblioteca) biblio).getNomeSimples();
			return getNome(nome);
		}
		return "";
	}

	@Biblio(3)
	public static String nameAPartir(String string, Object biblio) {
		String nome = simpleName(biblio);
		int pos = nome.indexOf(string);
		if (pos != -1) {
			return nome.substring(pos + string.length());
		}
		return "";
	}

	private static String getNome(String string) {
		int pos = string.lastIndexOf(".");
		return pos != -1 ? string.substring(0, pos) : string;
	}
}