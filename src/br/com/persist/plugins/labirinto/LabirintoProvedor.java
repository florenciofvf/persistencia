package br.com.persist.plugins.labirinto;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.SetValor.Valor;
import br.com.persist.marca.XML;
import br.com.persist.marca.XMLException;
import br.com.persist.marca.XMLUtil;

public class LabirintoProvedor {
	private static final List<Labirinto> lista = new ArrayList<>();
	private static final Logger LOG = Logger.getGlobal();
	private static final File file;

	private LabirintoProvedor() {
	}

	static {
		file = new File(LabirintoConstantes.LABIRINTO + Constantes.SEPARADOR + "labirinto.xml");
	}

	public static Labirinto getLabirinto(String nome) {
		for (Labirinto item : lista) {
			if (item.getNome().equals(nome)) {
				return item;
			}
		}
		return null;
	}

	public static void excluir(int[] indices) {
		List<Labirinto> lista = new ArrayList<>();
		for (int i : indices) {
			Labirinto item = getLabirinto(i);
			if (item != null) {
				lista.add(item);
			}
		}
		for (Labirinto item : lista) {
			int indice = getIndice(item.getNome());
			if (indice != -1) {
				excluir(indice);
			}
		}
	}

	public static void excluir(int indice) {
		if (indice >= 0 && indice < getSize()) {
			lista.remove(indice);
		}
	}

	public static Labirinto getLabirinto(int indice) {
		if (indice >= 0 && indice < getSize()) {
			return lista.get(indice);
		}
		return null;
	}

	public static int getIndice(String nome) {
		for (int i = 0; i < lista.size(); i++) {
			Labirinto item = lista.get(i);
			if (item.getNome().equals(nome)) {
				return i;
			}
		}
		return -1;
	}

	public static int getSize() {
		return lista.size();
	}

	public static boolean contem(Labirinto labirinto) {
		return contem(labirinto.getNome());
	}

	public static boolean contem(String nome) {
		return getLabirinto(nome) != null;
	}

	public static void adicionar(Labirinto labirinto) {
		if (!contem(labirinto)) {
			lista.add(labirinto);
		}
	}

	public static void inicializar() {
		lista.clear();
		try {
			if (file.exists() && file.canRead()) {
				XML.processar(file, new LabirintoXMLHandler());
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	public static void salvar() throws XMLException {
		XMLUtil util = new XMLUtil(file);
		util.prologo();
		util.abrirTag2("LABIRINTOS");
		salvarLabirinto(util);
		util.finalizarTag("LABIRINTOS");
		util.close();
	}

	private static void salvarLabirinto(XMLUtil util) {
		for (Labirinto item : lista) {
			if (item.isValido()) {
				item.salvar(util);
			}
		}
	}

	public static Valor getValor(int i) {
		Labirinto item = getLabirinto(i);
		return new LabirintoValor(item);
	}

	private static class LabirintoValor implements Valor {
		private final Labirinto item;

		public LabirintoValor(Labirinto item) {
			this.item = item;
		}

		@Override
		public String getTitle() {
			return "Valor";
		}

		@Override
		public String get() {
			return item.getValor();
		}

		@Override
		public void set(String s) {
			item.setValor(s);
		}
	}

	public static void contemConteudo(Set<String> set, String string, boolean porParte) {
		for (Labirinto item : lista) {
			if (Util.existeEm(item.getValor(), string, porParte)) {
				set.add(item.getNome());
			}
		}
	}
}