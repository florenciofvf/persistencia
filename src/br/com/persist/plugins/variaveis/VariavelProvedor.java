package br.com.persist.plugins.variaveis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.componente.SetValor.Valor;
import br.com.persist.util.Constantes;
import br.com.persist.xml.XML;
import br.com.persist.xml.XMLUtil;

public class VariavelProvedor {
	private static final List<Variavel> lista = new ArrayList<>();
	private static final Logger LOG = Logger.getGlobal();
	private static final File file;

	private VariavelProvedor() {
	}

	static {
		file = new File(Constantes.VARIAVEIS + Constantes.SEPARADOR + "var.xml");
	}

	public static Variavel getVariavel(int indice) {
		if (indice >= 0 && indice < getSize()) {
			return lista.get(indice);
		}

		return null;
	}

	public static Variavel getVariavel(String nome) {
		for (Variavel v : lista) {
			if (v.getNome().equals(nome)) {
				return v;
			}
		}

		return null;
	}

	public static int getIndice(String nome) {
		for (int i = 0; i < lista.size(); i++) {
			Variavel v = lista.get(i);
			if (v.getNome().equals(nome)) {
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
		if (contem(variavel)) {
			return;
		}

		lista.add(variavel);
	}

	public static void inicializar() {
		lista.clear();

		try {
			if (file.exists() && file.canRead()) {
				XML.processar(file, new VariavelHandler());
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	public static void salvar() {
		try {
			XMLUtil util = new XMLUtil(file);
			util.prologo();

			util.abrirTag2(Constantes.VARIAVEIS);

			for (Variavel v : lista) {
				if (v.isValido()) {
					v.salvar(util);
				}
			}

			util.finalizarTag(Constantes.VARIAVEIS);
			util.close();
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
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
}