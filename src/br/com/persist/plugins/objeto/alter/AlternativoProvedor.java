package br.com.persist.plugins.objeto.alter;

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

public class AlternativoProvedor {
	private static final List<Alternativo> lista = new ArrayList<>();
	private static final List<Alternativo> pasta = new ArrayList<>();
	private static final Logger LOG = Logger.getGlobal();
	private static final File file;

	private AlternativoProvedor() {
	}

	static {
		file = new File(AlternativoConstantes.ALTERNATIVOS + Constantes.SEPARADOR + "alternativos.xml");
	}

	public static Alternativo getAlternativo(int indice) {
		if (indice >= 0 && indice < getSize()) {
			return lista.get(indice);
		}
		return null;
	}

	public static void excluir(int[] indices) {
		List<Alternativo> lista = new ArrayList<>();
		for (int i : indices) {
			Alternativo f = getAlternativo(i);
			if (f != null) {
				lista.add(f);
			}
		}
		for (Alternativo frag : lista) {
			int indice = getIndice(frag.getResumo());
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

	public static Alternativo getAlternativo(String resumo) {
		for (Alternativo f : lista) {
			if (f.getResumo().equals(resumo)) {
				return f;
			}
		}
		return null;
	}

	public static int getIndice(String resumo) {
		for (int i = 0; i < lista.size(); i++) {
			Alternativo f = lista.get(i);
			if (f.getResumo().equals(resumo)) {
				return i;
			}
		}
		return -1;
	}

	public static int getSize() {
		return lista.size();
	}

	public static boolean contem(Alternativo alternativo) {
		return contem(alternativo.getResumo());
	}

	public static boolean contem(String resumo) {
		return getAlternativo(resumo) != null;
	}

	public static void adicionar(Alternativo alternativo) {
		if (!contem(alternativo)) {
			lista.add(alternativo);
		}
	}

	public static void inicializar() {
		lista.clear();
		pasta.clear();
		try {
			if (file.exists() && file.canRead()) {
				XML.processar(file, new AlternativoHandler());
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
		util.abrirTag2(AlternativoConstantes.ALTERNATIVOS);
		salvarAlternativosLista(util);
		salvarAlternativosPasta(util);
		util.finalizarTag(AlternativoConstantes.ALTERNATIVOS);
		util.close();
	}

	private static void salvarAlternativosLista(XMLUtil util) {
		for (Alternativo f : lista) {
			if (f.isValido()) {
				f.salvar(util);
			}
		}
	}

	private static void salvarAlternativosPasta(XMLUtil util) {
		for (Alternativo f : pasta) {
			if (f.isValido()) {
				f.salvar(util);
			}
		}
	}

	public static void filtrar(List<String> grupos) {
		removerFiltro();
		Iterator<Alternativo> it = lista.iterator();
		while (it.hasNext()) {
			Alternativo f = it.next();
			if (!contem(grupos, f.getGrupo())) {
				pasta.add(f);
				it.remove();
			}
		}
	}

	private static boolean contem(List<String> grupos, String grupo) {
		for (String s : grupos) {
			if (s.equalsIgnoreCase(grupo)) {
				return true;
			}
		}
		return false;
	}

	public static void removerFiltro() {
		Iterator<Alternativo> it = pasta.iterator();
		while (it.hasNext()) {
			Alternativo f = it.next();
			lista.add(f);
			it.remove();
		}
	}

	public static Valor getValor(int i) {
		Alternativo alternativo = getAlternativo(i);
		return new AlternativoValor(alternativo);
	}

	private static class AlternativoValor implements Valor {
		private final Alternativo alternativo;

		public AlternativoValor(Alternativo alternativo) {
			this.alternativo = alternativo;
		}

		@Override
		public String getTitle() {
			return "Valor";
		}

		@Override
		public String get() {
			return alternativo.getValor();
		}

		@Override
		public void set(String s) {
			alternativo.setValor(s);
		}
	}

	public static void contemConteudo(Set<String> set, String string, boolean porParte) {
		for (Alternativo item : lista) {
			if (Util.existeEm(item.getValor(), string, porParte)) {
				set.add(item.getResumo());
			}
		}
	}
}