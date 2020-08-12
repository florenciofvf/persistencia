package br.com.persist.mapeamento;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;

import br.com.persist.chave_valor.ChaveValor;
import br.com.persist.util.Constantes;
import br.com.persist.xml.XML;
import br.com.persist.xml.XMLUtil;

public class MapeamentoModelo extends AbstractTableModel {
	private static final File file = new File("mapeamento/mapa.xml");
	private static final List<ChaveValor> lista = new ArrayList<>();
	private static final String[] COLUNAS = { "CHAVE", "VALOR" };
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;

	@Override
	public int getRowCount() {
		return lista.size();
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
		ChaveValor cv = lista.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return cv.getChave();
		case 1:
			return cv.getValor();
		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		String valor = aValue == null ? Constantes.VAZIO : aValue.toString();
		ChaveValor cv = lista.get(rowIndex);

		switch (columnIndex) {
		case 0:
			cv.setChave(valor);
			break;
		case 1:
			cv.setValor(valor);
			break;
		default:
		}
	}

	public static ChaveValor getChaveValor(int i) {
		return lista.get(i);
	}

	public static boolean contem(String chave) {
		return get(chave) != null;
	}

	public static ChaveValor get(String chave) {
		for (ChaveValor cv : lista) {
			if (cv.getChave().equals(chave)) {
				return cv;
			}
		}

		return null;
	}

	public static void adicionar(ChaveValor cv) {
		if (cv != null && !lista.contains(cv)) {
			lista.add(cv);
		}
	}

	public static void novo() {
		adicionar(new ChaveValor(Constantes.TEMP));
	}

	public static void salvar() {
		try {
			XMLUtil util = new XMLUtil(file);
			util.prologo();

			util.abrirTag2("mapeamento");

			for (ChaveValor cv : lista) {
				if (cv.isValida()) {
					cv.salvar(util);
				}
			}

			util.finalizarTag("mapeamento");
			util.close();
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}

	public static void inicializar() {
		lista.clear();

		try {
			if (file.exists() && file.canRead()) {
				XML.processarMapeamento(file);
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Constantes.ERRO, e);
		}
	}
}