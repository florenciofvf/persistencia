package br.com.persist.plugins.mapeamento;

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

public class MapeamentoProvedor {
	private static final List<Mapeamento> lista = new ArrayList<>();
	private static final Logger LOG = Logger.getGlobal();
	private static final File file;

	private MapeamentoProvedor() {
	}

	static {
		file = new File(MapeamentoConstantes.MAPEAMENTOS + Constantes.SEPARADOR + "mapa.xml");
	}

	public static Mapeamento getMapeamento(int indice) {
		if (indice >= 0 && indice < getSize()) {
			return lista.get(indice);
		}
		return null;
	}

	public static void excluir(int[] indices) {
		List<Mapeamento> lista = new ArrayList<>();
		for (int i : indices) {
			Mapeamento m = getMapeamento(i);
			if (m != null) {
				lista.add(m);
			}
		}
		for (Mapeamento map : lista) {
			int indice = getIndice(map.getNome());
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

	public static Mapeamento getMapeamento(String nome) {
		for (Mapeamento m : lista) {
			if (m.getNome().equals(nome)) {
				return m;
			}
		}
		return null;
	}

	public static int getIndice(String nome) {
		for (int i = 0; i < lista.size(); i++) {
			Mapeamento m = lista.get(i);
			if (m.getNome().equals(nome)) {
				return i;
			}
		}
		return -1;
	}

	public static int getSize() {
		return lista.size();
	}

	public static boolean contem(Mapeamento mapeamento) {
		return contem(mapeamento.getNome());
	}

	public static boolean contem(String nome) {
		return getMapeamento(nome) != null;
	}

	public static void adicionar(Mapeamento mapeamento) {
		if (!contem(mapeamento)) {
			lista.add(mapeamento);
		}
	}

	public static void inicializar() {
		lista.clear();
		try {
			if (file.exists() && file.canRead()) {
				XML.processar(file, new MapeamentoHandler());
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	public static void salvar() throws XMLException {
		XMLUtil util = new XMLUtil(file);
		util.prologo();
		util.abrirTag2(MapeamentoConstantes.MAPEAMENTOS);
		salvarMapeamentos(util);
		util.finalizarTag(MapeamentoConstantes.MAPEAMENTOS);
		util.close();
	}

	private static void salvarMapeamentos(XMLUtil util) {
		for (Mapeamento m : lista) {
			if (m.isValido()) {
				m.salvar(util);
			}
		}
	}

	public static Valor getValor(int i) {
		Mapeamento mapeamento = getMapeamento(i);
		return new MapeamentoValor(mapeamento);
	}

	private static class MapeamentoValor implements Valor {
		private final Mapeamento mapeamento;

		public MapeamentoValor(Mapeamento mapeamento) {
			this.mapeamento = mapeamento;
		}

		@Override
		public String getTitle() {
			return "Valor";
		}

		@Override
		public String get() {
			return mapeamento.getValor();
		}

		@Override
		public void set(String s) {
			mapeamento.setValor(s);
		}
	}

	public static void contemConteudo(Set<String> set, String string, boolean porParte) {
		for (Mapeamento item : lista) {
			if (Util.existeEm(item.getValor(), string, porParte)) {
				set.add(item.getNome());
			}
		}
	}
}