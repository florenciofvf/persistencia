package br.com.persist.plugins.fragmento;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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

public class FragmentoProvedor {
	private static final List<Fragmento> lista = new ArrayList<>();
	private static final List<Fragmento> pasta = new ArrayList<>();
	private static final Logger LOG = Logger.getGlobal();
	private static final File file;

	private FragmentoProvedor() {
	}

	static {
		file = new File(FragmentoConstantes.FRAGMENTOS + Constantes.SEPARADOR + "fragmentos.xml");
	}

	public static File getFile() {
		return file;
	}

	public static int primeiro(int indice) {
		if (indice > 0 && indice < getSize()) {
			Fragmento item = lista.remove(indice);
			lista.add(0, item);
			return 0;
		}
		return -1;
	}

	public static int anterior(int indice) {
		if (indice > 0 && indice < getSize()) {
			Fragmento item = lista.remove(indice);
			indice--;
			lista.add(indice, item);
			return indice;
		}
		return -1;
	}

	public static int proximo(int indice) {
		if (indice + 1 < getSize()) {
			Fragmento item = lista.remove(indice);
			indice++;
			lista.add(indice, item);
			return indice;
		}
		return -1;
	}

	public static void excluir(int indice) {
		if (indice >= 0 && indice < getSize()) {
			lista.remove(indice);
		}
	}

	public static Fragmento getFragmento(int indice) {
		if (indice >= 0 && indice < getSize()) {
			return lista.get(indice);
		}
		return null;
	}

	public static void excluir(int[] indices) {
		List<Fragmento> lista = new ArrayList<>();
		for (int i : indices) {
			Fragmento f = getFragmento(i);
			if (f != null) {
				lista.add(f);
			}
		}
		for (Fragmento item : lista) {
			int indice = getIndice(item.getResumo());
			if (indice != -1) {
				excluir(indice);
			}
		}
	}

	public static Fragmento getFragmento(String resumo) {
		for (Fragmento item : lista) {
			if (item.getResumo().equals(resumo)) {
				return item;
			}
		}
		return null;
	}

	public static int getIndice(String resumo) {
		for (int i = 0; i < lista.size(); i++) {
			Fragmento item = lista.get(i);
			if (item.getResumo().equals(resumo)) {
				return i;
			}
		}
		return -1;
	}

	public static int getSize() {
		return lista.size();
	}

	public static boolean contem(Fragmento fragmento) {
		return contem(fragmento.getResumo());
	}

	public static boolean contem(String resumo) {
		return getFragmento(resumo) != null;
	}

	public static void adicionar(Fragmento fragmento) {
		if (!contem(fragmento)) {
			lista.add(fragmento);
		}
	}

	public static void inicializar() {
		lista.clear();
		pasta.clear();
		try {
			if (file.exists() && file.canRead()) {
				XML.processar(file, new FragmentoHandler());
			}
			ordenar();
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	private static void ordenar() {
		Collections.sort(lista, (o1, o2) -> o1.getGrupo().compareTo(o2.getGrupo()));
	}

	public static void salvar() throws XMLException {
		XMLUtil util = new XMLUtil(file);
		util.prologo();
		util.abrirTag2(FragmentoConstantes.FRAGMENTOS);
		salvarFragmentosLista(util);
		salvarFragmentosPasta(util);
		util.finalizarTag(FragmentoConstantes.FRAGMENTOS);
		util.close();
	}

	private static void salvarFragmentosLista(XMLUtil util) {
		for (Fragmento item : lista) {
			if (item.isValido()) {
				item.salvar(util);
			}
		}
	}

	private static void salvarFragmentosPasta(XMLUtil util) {
		for (Fragmento item : pasta) {
			if (item.isValido()) {
				item.salvar(util);
			}
		}
	}

	public static void filtrar(List<String> grupos) {
		removerFiltro();
		Iterator<Fragmento> it = lista.iterator();
		while (it.hasNext()) {
			Fragmento item = it.next();
			if (!contem(grupos, item.getGrupo())) {
				pasta.add(item);
				it.remove();
			}
		}
	}

	private static boolean contem(List<String> grupos, String grupo) {
		for (String item : grupos) {
			if (item.equalsIgnoreCase(grupo)) {
				return true;
			}
		}
		return false;
	}

	public static void removerFiltro() {
		Iterator<Fragmento> it = pasta.iterator();
		while (it.hasNext()) {
			Fragmento item = it.next();
			lista.add(item);
			it.remove();
		}
	}

	public static Valor getValor(int i) {
		Fragmento fragmento = getFragmento(i);
		return new FragmentoValor(fragmento);
	}

	private static class FragmentoValor implements Valor {
		private final Fragmento fragmento;

		public FragmentoValor(Fragmento fragmento) {
			this.fragmento = fragmento;
		}

		@Override
		public String getTitle() {
			return "Valor";
		}

		@Override
		public String get() {
			return fragmento.getValor();
		}

		@Override
		public void set(String s) {
			fragmento.setValor(s);
		}
	}

	public static void contemConteudo(Set<String> set, String string, boolean porParte) {
		for (Fragmento item : lista) {
			if (Util.existeEm(item.getValor(), string, porParte)) {
				set.add(item.getResumo());
			}
		}
	}
}