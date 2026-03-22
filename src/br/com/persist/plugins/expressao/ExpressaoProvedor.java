package br.com.persist.plugins.expressao;

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

public class ExpressaoProvedor {
	private static final List<Expressao> lista = new ArrayList<>();
	private static final Logger LOG = Logger.getGlobal();
	private static final File file;

	private ExpressaoProvedor() {
	}

	static {
		file = new File(ExpressaoConstantes.EXPRESSOES + Constantes.SEPARADOR + "expressao.xml");
	}

	public static Expressao getExpressao(String nome) {
		for (Expressao item : lista) {
			if (item.getNome().equals(nome)) {
				return item;
			}
		}
		return null;
	}

	public static void excluir(int[] indices) {
		List<Expressao> lista = new ArrayList<>();
		for (int i : indices) {
			Expressao item = getExpressao(i);
			if (item != null) {
				lista.add(item);
			}
		}
		for (Expressao item : lista) {
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

	public static Expressao getExpressao(int indice) {
		if (indice >= 0 && indice < getSize()) {
			return lista.get(indice);
		}
		return null;
	}

	public static int getIndice(String nome) {
		for (int i = 0; i < lista.size(); i++) {
			Expressao item = lista.get(i);
			if (item.getNome().equals(nome)) {
				return i;
			}
		}
		return -1;
	}

	public static int getSize() {
		return lista.size();
	}

	public static boolean contem(Expressao expressao) {
		return contem(expressao.getNome());
	}

	public static boolean contem(String nome) {
		return getExpressao(nome) != null;
	}

	public static void adicionar(Expressao expressao) {
		if (!contem(expressao)) {
			lista.add(expressao);
		}
	}

	public static void inicializar() {
		lista.clear();
		try {
			if (file.exists() && file.canRead()) {
				XML.processar(file, new ExpressaoXMLHandler());
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	public static void salvar() throws XMLException {
		XMLUtil util = new XMLUtil(file);
		util.prologo();
		util.abrirTag2("EXPRESSOESS");
		salvarExpressao(util);
		util.finalizarTag("EXPRESSOESS");
		util.close();
	}

	private static void salvarExpressao(XMLUtil util) {
		for (Expressao item : lista) {
			if (item.isValido()) {
				item.salvar(util);
			}
		}
	}

	public static Valor getValor(int i) {
		Expressao item = getExpressao(i);
		return new ExpressaoValor(item);
	}

	private static class ExpressaoValor implements Valor {
		private final Expressao item;

		public ExpressaoValor(Expressao item) {
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
		for (Expressao item : lista) {
			if (Util.existeEm(item.getValor(), string, porParte)) {
				set.add(item.getNome());
			}
		}
	}
}
