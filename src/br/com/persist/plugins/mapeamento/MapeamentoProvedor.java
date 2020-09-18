package br.com.persist.plugins.mapeamento;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.assistencia.Constantes;
import br.com.persist.componente.SetValor.Valor;
import br.com.persist.marca.XML;
import br.com.persist.marca.XMLUtil;

public class MapeamentoProvedor {
	private static final List<Mapeamento> lista = new ArrayList<>();
	private static final Logger LOG = Logger.getGlobal();
	private static final File file;

	private MapeamentoProvedor() {
	}

	static {
		file = new File(Constantes.MAPEAMENTOS + Constantes.SEPARADOR + "mapa.xml");
	}

	public static Mapeamento getMapeamento(int indice) {
		if (indice >= 0 && indice < getSize()) {
			return lista.get(indice);
		}

		return null;
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
		if (contem(mapeamento)) {
			return;
		}

		lista.add(mapeamento);
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

	public static void salvar() {
		try {
			XMLUtil util = new XMLUtil(file);
			util.prologo();

			util.abrirTag2(Constantes.MAPEAMENTOS);

			for (Mapeamento m : lista) {
				if (m.isValido()) {
					m.salvar(util);
				}
			}

			util.finalizarTag(Constantes.MAPEAMENTOS);
			util.close();
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
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
}