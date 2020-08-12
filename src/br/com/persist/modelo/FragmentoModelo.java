package br.com.persist.modelo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;

import br.com.persist.fragmento.Fragmento;
import br.com.persist.util.Constantes;
import br.com.persist.xml.XML;
import br.com.persist.xml.XMLColetor;
import br.com.persist.xml.XMLUtil;

public class FragmentoModelo extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private static final String[] COLUNAS = { "RESUMO", "GRUPO", "VALOR" };
	private static final File file = new File("fragmentos/fragmentos.xml");
	private static final List<Fragmento> fragmentos = new ArrayList<>();
	private static final List<Fragmento> auxiliares = new ArrayList<>();
	private static final Logger LOG = Logger.getGlobal();

	@Override
	public int getRowCount() {
		return fragmentos.size();
	}

	@Override
	public int getColumnCount() {
		return COLUNAS.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return COLUNAS[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Fragmento f = fragmentos.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return f.getResumo();
		case 1:
			return f.getGrupo();
		case 2:
			return f.getValor();
		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		String valor = aValue == null ? Constantes.VAZIO : aValue.toString();
		Fragmento f = fragmentos.get(rowIndex);

		switch (columnIndex) {
		case 0:
			f.setResumo(valor);
			break;
		case 1:
			f.setGrupo(valor);
			break;
		case 2:
			f.setValor(valor);
			break;
		default:
		}
	}

	public static Fragmento getFragmento(int i) {
		return fragmentos.get(i);
	}

	public static void adicionar(Fragmento f) {
		if (f != null) {
			fragmentos.add(f);
		}
	}

	public static void novo() {
		fragmentos.add(new Fragmento());
	}

	public static void salvar() {
		try {
			XMLUtil util = new XMLUtil(file);
			util.prologo();

			util.abrirTag2("fragmentos");

			for (Fragmento f : fragmentos) {
				if (f.isValida()) {
					f.salvar(util);
				}
			}

			for (Fragmento f : auxiliares) {
				if (f.isValida()) {
					f.salvar(util);
				}
			}

			util.finalizarTag("fragmentos");
			util.close();
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	public static void inicializar() {
		fragmentos.clear();
		auxiliares.clear();

		try {
			XMLColetor coletor = new XMLColetor();

			if (file.exists() && file.canRead()) {
				XML.processarFragmento(file, coletor);
			}

			fragmentos.addAll(coletor.getFragmentos());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	public static void filtar(List<String> filtroGrupos) {
		Iterator<Fragmento> it = fragmentos.iterator();

		while (it.hasNext()) {
			Fragmento f = it.next();
			String g = f.getGrupo() != null ? f.getGrupo().toUpperCase() : null;

			if (!filtroGrupos.contains(g)) {
				auxiliares.add(f);
				it.remove();
			}
		}
	}

	public static void reiniciar() {
		Iterator<Fragmento> it = auxiliares.iterator();

		while (it.hasNext()) {
			Fragmento f = it.next();
			fragmentos.add(f);
			it.remove();
		}
	}

	public static void ordenar() {
		Collections.sort(fragmentos, (o1, o2) -> {
			String s1 = o1.getGrupo() != null ? o1.getGrupo().toUpperCase() : Constantes.VAZIO;
			String s2 = o2.getGrupo() != null ? o2.getGrupo().toUpperCase() : Constantes.VAZIO;
			return s1.compareTo(s2);
		});
	}
}