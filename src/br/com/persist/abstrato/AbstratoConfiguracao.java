package br.com.persist.abstrato;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Window;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;

import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.FicharioHandler;
import br.com.persist.formulario.Formulario;

public abstract class AbstratoConfiguracao extends Panel implements WindowHandler, DialogHandler, FicharioHandler {
	private static final long serialVersionUID = 1L;
	protected final Formulario formulario;
	private final String titulo;

	protected AbstratoConfiguracao(Formulario formulario, String titulo) {
		this.formulario = Objects.requireNonNull(formulario);
		this.titulo = Objects.requireNonNull(titulo);
		destacar(false);
	}

	public void adicionadoAoFichario() {
	}

	@Override
	public void tabActivatedHandler(Fichario fichario) {
	}

	@Override
	public void dialogActivatedHandler(Dialog dialog) {
	}

	@Override
	public void dialogClosingHandler(Dialog dialog) {
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
	}

	@Override
	public void windowActivatedHandler(Window window) {
	}

	@Override
	public void windowClosingHandler(Window window) {
	}

	@Override
	public void windowOpenedHandler(Window window) {
	}

	public String getTitulo() {
		return titulo;
	}

	public void destacar(boolean b) {
		if (b) {
			Border border = BorderFactory.createLineBorder(Color.RED);
			setBorder(BorderFactory.createTitledBorder(border, Objects.requireNonNull(titulo)));
		} else {
			setBorder(BorderFactory.createTitledBorder(Objects.requireNonNull(titulo)));
		}
	}

	protected List<Atalho> getAtalhos() {
		return Collections.emptyList();
	}

	protected Panel getPanelAtalhos() {
		List<Atalho> atalhos = getAtalhos();
		if (atalhos == null || atalhos.isEmpty()) {
			return null;
		}
		Panel panel = new Panel();
		panel.add(new ScrollPane(new JTable(new AtalhoModelo(atalhos))));
		return panel;
	}
}

class AtalhoModelo extends AbstractTableModel {
	private static final String[] COLUNAS = { "TECLAS", "FUNCIONALIDADE" };
	private static final long serialVersionUID = 1L;
	private final transient List<Atalho> atalhos;

	public AtalhoModelo(List<Atalho> atalhos) {
		this.atalhos = Objects.requireNonNull(atalhos);
	}

	@Override
	public int getRowCount() {
		return atalhos.size();
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
		Atalho item = atalhos.get(rowIndex);
		if (columnIndex == 0) {
			return item.getTeclas();
		} else if (columnIndex == 1) {
			return item.getDescricao();
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		//
	}
}