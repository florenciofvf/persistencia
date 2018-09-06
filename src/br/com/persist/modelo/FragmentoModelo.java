package br.com.persist.modelo;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import br.com.persist.util.Fragmento;
import br.com.persist.util.XMLUtil;
import br.com.persist.xml.XML;

public class FragmentoModelo extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private final String[] COLUNAS = { "RESUMO", "GRUPO", "VALOR" };
	private static final File file = new File("fragmentos/fragmentos.xml");
	private final static List<Fragmento> fragmentos = new ArrayList<>();
	private final static List<Fragmento> filtrados = new ArrayList<>();
	private static boolean aberto;

	public FragmentoModelo() {
	}

	public List<Fragmento> getFragmentos() {
		return fragmentos;
	}

	public Fragmento getFragmento(int i) {
		return fragmentos.get(i);
	}

	public void adicionar(Fragmento f) {
		if (f != null) {
			fragmentos.add(f);
		}
	}

	public void primeiro(int indice) {
		if (indice > 0 && indice < getRowCount()) {
			Fragmento f = fragmentos.remove(indice);
			fragmentos.add(0, f);
		}
	}

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
		Fragmento f = fragmentos.get(rowIndex);
		String valor = aValue == null ? "" : aValue.toString();

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
		}
	}

	public void novo() {
		fragmentos.add(new Fragmento());
		fireTableDataChanged();
	}

	public void salvar() throws Exception {
		XMLUtil util = new XMLUtil(file);
		util.prologo();

		util.abrirTag2("fragmentos");

		for (Fragmento f : fragmentos) {
			if (f.isValida()) {
				f.salvar(util);
			}
		}

		for (Fragmento f : filtrados) {
			if (f.isValida()) {
				f.salvar(util);
			}
		}

		util.finalizarTag("fragmentos");
		util.close();
	}

	public void abrir() throws Exception {
		if (aberto) {
			return;
		}

		if (file.exists() && file.canRead()) {
			XML.processarFragmento(file, fragmentos);
			fireTableDataChanged();
			aberto = true;
		}
	}

	public void abrir2() throws Exception {
		fragmentos.clear();
		filtrados.clear();

		if (file.exists() && file.canRead()) {
			XML.processarFragmento(file, fragmentos);
			fireTableDataChanged();
			aberto = true;
		}
	}

	public void filtar(List<String> gruposFiltro) {
		Iterator<Fragmento> it = fragmentos.iterator();

		while (it.hasNext()) {
			Fragmento f = it.next();
			String g = f.getGrupo() != null ? f.getGrupo().toUpperCase() : null;

			if (!gruposFiltro.contains(g)) {
				it.remove();

				if (!filtrados.contains(f)) {
					filtrados.add(f);
				}
			}
		}
	}
}