package br.com.persist.plugins.biblio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.assistencia.ArquivoUtil;
import br.com.persist.assistencia.Constantes;

public class BiblioProvedor {
	private static final List<Biblio> lista = new ArrayList<>();
	private static final Logger LOG = Logger.getGlobal();
	private static final File file;
	private static int indice;

	private BiblioProvedor() {
	}

	static {
		file = new File("libs" + Constantes.SEPARADOR + "outras_libs");
	}

	public static synchronized int nextIndice() {
		return ++indice;
	}

	public static void inicializar() {
		lista.clear();
		try {
			if (file.exists() && file.canRead()) {
				List<String> list = ArquivoUtil.lerArquivo(file);
				for (String string : list) {
					lista.add(new Biblio(string));
				}
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	public static void salvar() throws IOException {
		List<String> list = new ArrayList<>();
		for (Biblio biblio : lista) {
			list.add(biblio.getNome());
		}
		ArquivoUtil.salvar(list, file);
	}

	public static boolean contem(Biblio conexao) {
		return contem(conexao.getNome());
	}

	public static boolean contem(String nome) {
		return getBiblio(nome) != null;
	}

	public static void adicionar(Biblio biblio) {
		if (contem(biblio)) {
			return;
		}
		lista.add(biblio);
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