package br.com.persist.plugins.objeto.internal;

import java.awt.Color;
import java.awt.Component;
import java.util.Objects;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.objeto.internal.InternalContainer.Toolbar;
import br.com.persist.plugins.persistencia.Coluna;
import br.com.persist.plugins.persistencia.OrdenacaoModelo;
import br.com.persist.plugins.persistencia.tabela.TabelaPersistencia;

public class ComparaRegistroRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	private final String nomeColuna;
	private final Toolbar toolbar;

	public ComparaRegistroRenderer(Toolbar toolbar, String nomeColuna) {
		this.nomeColuna = Objects.requireNonNull(nomeColuna);
		this.toolbar = Objects.requireNonNull(toolbar);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		TabelaPersistencia tabelaPersistencia = (TabelaPersistencia) table;
		OrdenacaoModelo modelo = tabelaPersistencia.getModelo();
		OrdenacaoModelo backup = tabelaPersistencia.getModeloBackup();

		if (backup == null) {
			toolbar.exceptionEnable("MODELO DE DADOS ANTERIOR NULO");
			return this;
		} else if (modelo.getRowCount() != backup.getRowCount()) {
			toolbar.exceptionEnable("TOTAL DE REGISTROS ATUAL: [" + modelo.getRowCount()
					+ "] TOTAL DE REGISTROS ANTERIOR: [" + backup.getRowCount() + "]");
			return this;
		}

		Coluna colunaModelo = modelo.getColuna(nomeColuna);
		Coluna colunaBackup = backup.getColuna(colunaModelo.getNome());

		if (colunaBackup == null) {
			colunaModelo.setStringComparaRegistro("NOVA COLUNA ADICIONADA: [" + colunaModelo.getNome() + "]");
			toolbar.exceptionEnable(colunaModelo.getStringComparaRegistro());
			setForeground(Color.WHITE);
			setBackground(Color.BLUE);
		} else {
			String strModelo = modelo.getValueAt(0, colunaModelo.getIndice()).toString();
			String strBackup = backup.getValueAt(0, colunaBackup.getIndice()).toString();
			if (!Util.isEmpty(strModelo) && Util.isEmpty(strBackup)) {
				colunaModelo.setStringComparaRegistro("NOVO VALOR EM: [" + nomeColuna + "]");
				toolbar.exceptionEnable(colunaModelo.getStringComparaRegistro());
				setForeground(Color.BLACK);
				setBackground(Color.GREEN);
			} else if (Util.isEmpty(strModelo) && !Util.isEmpty(strBackup)) {
				colunaModelo.setStringComparaRegistro(
						"REMOVIDO VALOR EM: [" + nomeColuna + "] VALOR ANTERIOR: [" + strBackup + "]");
				toolbar.exceptionEnable(colunaModelo.getStringComparaRegistro());
				setForeground(Color.BLACK);
				setBackground(Color.RED);
			} else if (!Util.isEmpty(strModelo) && !Util.isEmpty(strBackup) && !strModelo.equals(strBackup)) {
				colunaModelo.setStringComparaRegistro(
						"ALTERADO VALOR EM: [" + nomeColuna + "] VALOR ANTERIOR: [" + strBackup + "]");
				toolbar.exceptionEnable(colunaModelo.getStringComparaRegistro());
				setForeground(Color.BLACK);
				setBackground(Color.ORANGE);
			}
		}

		return this;
	}
}