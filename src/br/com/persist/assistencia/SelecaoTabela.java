package br.com.persist.assistencia;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import br.com.persist.componente.Label;

public class SelecaoTabela implements Busca {
	private List<Integer> lista = new ArrayList<>();
	final JTable tabela;
	final String string;
	int indice;
	int coluna;

	public SelecaoTabela(JTable tabela, String string, int coluna) {
		this.string = Objects.requireNonNull(string).toUpperCase();
		this.tabela = Objects.requireNonNull(tabela);
		this.coluna = coluna;
		inicializar();
	}

	private void inicializar() {
		indice = 0;
		TableModel tableModel = tabela.getModel();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			Object item = tabela.getValueAt(i, coluna);
			if (item != null) {
				String itemValor = item.toString().toUpperCase();
				if (itemValor.indexOf(string) != -1) {
					lista.add(i);
				}
			}
		}
	}

	public boolean igual(String string) {
		if (this.string == null && string == null) {
			return true;
		}
		if (this.string == null || string == null) {
			return false;
		}
		string = string.toUpperCase();
		return this.string.equals(string);
	}

	public String getString() {
		return string;
	}

	public int getTotal() {
		return lista.size();
	}

	public int getIndice() {
		return indice;
	}

	private void selecionarLinha(int linha) {
		tabela.addRowSelectionInterval(linha, linha);
		int colunaView = tabela.convertColumnIndexToView(coluna);
		Rectangle rect = tabela.getCellRect(linha, colunaView, true);
		if (rect != null) {
			tabela.scrollRectToVisible(rect);
		}
	}

	public void selecionar(Label label) {
		if (label == null) {
			return;
		}
		if (indice < getTotal()) {
			Integer linha = lista.get(indice);
			if (linha >= tabela.getModel().getRowCount()) {
				inicializar();
				return;
			}
			selecionarLinha(linha);
			indice++;
			label.setText(indice + "/" + getTotal());
		} else {
			tabela.clearSelection();
			label.limpar();
			indice = 0;
		}
	}
}