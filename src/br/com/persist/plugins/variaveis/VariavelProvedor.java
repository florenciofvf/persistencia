package br.com.persist.plugins.variaveis;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.SetValor.Valor;
import br.com.persist.marca.XML;
import br.com.persist.marca.XMLException;
import br.com.persist.marca.XMLUtil;

public class VariavelProvedor {
	private static final List<Variavel> lista = new ArrayList<>();
	private static final Logger LOG = Logger.getGlobal();
	private static final File file;

	private VariavelProvedor() {
	}

	static {
		file = new File(VariavelConstantes.VARIAVEIS + Constantes.SEPARADOR + "var.xml");
	}

	public static Variavel getVariavel(int indice) {
		if (indice >= 0 && indice < getSize()) {
			return lista.get(indice);
		}
		return null;
	}

	public static void excluir(int[] indices) {
		List<Variavel> lista = new ArrayList<>();
		for (int i : indices) {
			Variavel v = getVariavel(i);
			if (v != null) {
				lista.add(v);
			}
		}
		for (Variavel item : lista) {
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

	public static Variavel getVariavel(String nome) {
		for (Variavel item : lista) {
			if (item.getNome().equals(nome)) {
				return item;
			}
		}
		return null;
	}

	public static int getIndice(String nome) {
		for (int i = 0; i < lista.size(); i++) {
			Variavel item = lista.get(i);
			if (item.getNome().equals(nome)) {
				return i;
			}
		}
		return -1;
	}

	public static int getSize() {
		return lista.size();
	}

	public static boolean contem(Variavel variavel) {
		return contem(variavel.getNome());
	}

	public static boolean contem(String nome) {
		return getVariavel(nome) != null;
	}

	public static void adicionar(Variavel variavel) {
		if (!contem(variavel)) {
			lista.add(variavel);
		}
	}

	public static void adicionar(String nome, String valor) throws ArgumentoException {
		adicionar(new Variavel(nome, valor));
	}

	public static void inicializar() {
		lista.clear();
		try {
			if (file.exists() && file.canRead()) {
				XML.processar(file, new VariavelHandler());
			}
			lista.sort(new Compara());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	private static class Compara implements Comparator<Variavel> {
		@Override
		public int compare(Variavel o1, Variavel o2) {
			return o1.getNome().compareTo(o2.getNome());
		}
	}

	public static void salvar() throws XMLException {
		XMLUtil util = new XMLUtil(file);
		util.prologo();
		util.abrirTag2(VariavelConstantes.VARIAVEIS);
		salvarVariaveis(util);
		util.finalizarTag(VariavelConstantes.VARIAVEIS);
		util.close();
	}

	private static void salvarVariaveis(XMLUtil util) {
		for (Variavel v : lista) {
			if (v.isValido()) {
				v.salvar(util);
			}
		}
	}

	public static String substituir(String string) {
		if (string != null) {
			for (Variavel v : lista) {
				string = string.replaceAll(Constantes.SEP + v.getNome() + Constantes.SEP, v.getValor());
			}
		}
		return string;
	}

	public static Valor getValor(int i) {
		Variavel variavel = getVariavel(i);
		return new VariavelValor(variavel);
	}

	private static class VariavelValor implements Valor {
		private final Variavel variavel;

		public VariavelValor(Variavel variavel) {
			this.variavel = variavel;
		}

		@Override
		public String getTitle() {
			return "Valor";
		}

		@Override
		public String get() {
			return variavel.getValor();
		}

		@Override
		public void set(String s) {
			variavel.setValor(s);
		}
	}

	public static void contemConteudo(Set<String> set, String string, boolean porParte) {
		for (Variavel item : lista) {
			if (Util.existeEm(item.getValor(), string, porParte)) {
				set.add(item.getNome());
			}
		}
	}
}