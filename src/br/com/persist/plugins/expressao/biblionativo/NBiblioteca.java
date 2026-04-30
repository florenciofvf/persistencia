package br.com.persist.plugins.expressao.biblionativo;

import br.com.persist.plugins.expressao.biblioteca.Biblioteca;

public class NBiblioteca {
	private NBiblioteca() {
	}

	@Biblio(1)
	public static String nameAbsolute(Object biblio) {
		if (biblio instanceof Biblioteca) {
			return ((Biblioteca) biblio).getNomeAbsoluto();
		}
		return "";
	}

	@Biblio(2)
	public static String nameSimple(Object biblio) {
		if (biblio instanceof Biblioteca) {
			return ((Biblioteca) biblio).getNomeSimples();
		}
		return "";
	}

	@Biblio(3)
	public static String nameAPartir(Object apartir, Object biblio) {
		if (apartir == null) {
			return "";
		}
		String nome = nameSimple(biblio);
		String strAPartir = apartir.toString();
		int pos = nome.indexOf(strAPartir);
		if (pos != -1) {
			return nome.substring(pos);
		}
		return "";
	}

	@Biblio(4)
	public static String nameApos(Object apos, Object biblio) {
		if (apos == null) {
			return "";
		}
		String nome = nameSimple(biblio);
		String strApos = apos.toString();
		int pos = nome.indexOf(strApos);
		if (pos != -1) {
			return nome.substring(pos + strApos.length());
		}
		return "";
	}
}