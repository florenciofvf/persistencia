package br.com.persist.plugins.fragmento;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.componente.SetValor.Valor;
import br.com.persist.util.Constantes;
import br.com.persist.xml.XML;
import br.com.persist.xml.XMLUtil;

public class FragmentoProvedor {
	private static final List<Fragmento> listaBackup = new ArrayList<>();
	private static final List<Fragmento> lista = new ArrayList<>();
	private static final Logger LOG = Logger.getGlobal();
	private static boolean filtrado;
	private static final File file;

	private FragmentoProvedor() {
	}

	static {
		file = new File(Constantes.FRAGMENTOS + Constantes.SEPARADOR + "fragmentos.xml");
	}

	public static Fragmento getFragmento(int indice) {
		if (indice >= 0 && indice < getSize()) {
			return lista.get(indice);
		}

		return null;
	}

	public static Fragmento getFragmento(String resumo) {
		for (Fragmento f : lista) {
			if (f.getResumo().equals(resumo)) {
				return f;
			}
		}

		return null;
	}

	public static int getIndice(String nome) {
		for (int i = 0; i < lista.size(); i++) {
			Fragmento f = lista.get(i);
			if (f.getResumo().equals(nome)) {
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
		if (contem(fragmento)) {
			return;
		}

		lista.add(fragmento);
	}

	public static void inicializar() {
		listaBackup.clear();
		lista.clear();

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

	public static void salvar() {
		try {
			XMLUtil util = new XMLUtil(file);
			util.prologo();

			util.abrirTag2(Constantes.FRAGMENTOS);

			for (Fragmento f : lista) {
				if (f.isValido()) {
					f.salvar(util);
				}
			}

			for (Fragmento f : listaBackup) {
				if (f.isValido()) {
					f.salvar(util);
				}
			}

			util.finalizarTag(Constantes.FRAGMENTOS);
			util.close();
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	public static void filtrarPeloGrupo(String grupo) {
		if (!filtrado) {
			Iterator<Fragmento> it = lista.iterator();

			while (it.hasNext()) {
				Fragmento f = it.next();

				if (f.getGrupo().equals(grupo)) {
					listaBackup.add(f);
					it.remove();
				}
			}

			inverter();
			filtrado = true;
		}
	}

	public static void removerFiltroPeloGrupo() {
		if (filtrado) {
			inverter();
			lista.addAll(listaBackup);
			listaBackup.clear();
			filtrado = false;
		}
	}

	private static void inverter() {
		List<Fragmento> lista1 = new ArrayList<>(lista);
		List<Fragmento> lista2 = new ArrayList<>(listaBackup);
		listaBackup.clear();
		lista.clear();

		listaBackup.addAll(lista1);
		lista.addAll(lista2);
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
}