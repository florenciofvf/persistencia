package br.com.persist.plugins.quebra_log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import br.com.persist.arquivo.ArquivoUtil;
import br.com.persist.assistencia.Util;

public class QuebraLogModelo extends AbstractTableModel {
	private final transient List<QuebraLog> lista = new ArrayList<>();
	private static final String[] COLUNAS = { "NOME", "CAMINHO ABSOLUTO", "TAMANHO" };
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;

	public QuebraLog getQuebraLog(int i) {
		if (i >= 0 && i < lista.size()) {
			return lista.get(i);
		}
		return null;
	}

	public int adicionar(QuebraLog quebraLog) {
		if (quebraLog != null) {
			lista.add(quebraLog);
			return lista.size() - 1;
		}
		return -1;
	}

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
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		QuebraLog quebraLog = lista.get(rowIndex);
		if (columnIndex == 0) {
			return quebraLog.getNome();
		} else if (columnIndex == 1) {
			return quebraLog.getAbsolutePath();
		} else if (columnIndex == 2) {
			return quebraLog.getTamanho();
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		LOG.log(Level.FINEST, "setValueAt");
	}

	public void fragmentarArquivo(JTable table) {
		ThreadFragmento selecionado = null;
		ThreadFragmento primeiro = null;
		for (QuebraLog qlog : lista) {
			ThreadFragmento fragmento = new ThreadFragmento(qlog, table);
			if (primeiro == null) {
				primeiro = fragmento;
			} else {
				selecionado.proximo = fragmento;
			}
			selecionado = fragmento;
		}
		if (primeiro != null) {
			primeiro.start();
		}
	}

	private class ThreadFragmento extends Thread {
		ThreadFragmento proximo;
		final QuebraLog qlog;
		final JTable table;

		ThreadFragmento(QuebraLog qlog, JTable table) {
			this.table = table;
			this.qlog = qlog;
		}

		@Override
		public void run() {
			try {
				ArquivoUtil.copiar(qlog.getOrigem(), qlog.getFile(), qlog.getIndice(), qlog.getTamanhoBloco());
				qlog.atualizarTamanho();
				Util.ajustar(table, table.getGraphics());
				fireTableRowsUpdated(qlog.getRow(), qlog.getRow());
				table.addRowSelectionInterval(qlog.getRow(), qlog.getRow());
			} catch (IOException ex) {
				LOG.log(Level.SEVERE, ex.getMessage());
			}
			if (proximo != null) {
				proximo.start();
			}
		}
	}
}