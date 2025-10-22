package br.com.persist.plugins.biblio;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.assistencia.Constantes;

public class BiblioJarProvedor {
	private static final List<Biblio> lista = new ArrayList<>();
	private static final Logger LOG = Logger.getGlobal();
	private static final File file;

	private BiblioJarProvedor() {
	}

	static {
		file = new File("libs");
	}

	public static void inicializar() {
		lista.clear();
		try {
			if (file.exists()) {
				File[] files = file.listFiles();
				if (files != null) {
					for (File item : files) {
						String nome = item.getName().toLowerCase();
						if (nome.endsWith(".jar")) {
							lista.add(new Biblio(nome));
						}
					}
				}
				Collections.sort(lista, (o1, o2) -> o1.getNome().compareTo(o2.getNome()));
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	public static boolean contem(Biblio biblio) {
		return contem(biblio.getNome());
	}

	public static boolean contem(String nome) {
		return getBiblio(nome) != null;
	}

	public static void adicionar(Biblio biblio, int indice) {
		if (contem(biblio)) {
			return;
		}
		lista.add(indice, biblio);
	}

	public static void adicionar(Biblio biblio) {
		if (contem(biblio)) {
			return;
		}
		lista.add(biblio);
	}

	public static Biblio getBiblio(int indice) {
		if (indice >= 0 && indice < getSize()) {
			return lista.get(indice);
		}
		return null;
	}

	public static Biblio getBiblio(String nome) {
		for (Biblio item : lista) {
			if (item.getNome().equals(nome)) {
				return item;
			}
		}
		return null;
	}

	public static void excluir(int indice) {
		if (indice >= 0 && indice < getSize()) {
			lista.remove(indice);
		}
	}

	public static int getSize() {
		return lista.size();
	}

	public static int anterior(int indice) {
		if (indice > 0 && indice < getSize()) {
			Biblio item = lista.remove(indice);
			indice--;
			lista.add(indice, item);
			return indice;
		}
		return -1;
	}

	public static int proximo(int indice) {
		if (indice + 1 < getSize()) {
			Biblio item = lista.remove(indice);
			indice++;
			lista.add(indice, item);
			return indice;
		}
		return -1;
	}
}