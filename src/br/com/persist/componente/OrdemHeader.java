package br.com.persist.componente;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import br.com.persist.assistencia.Icones;

public class OrdemHeader extends Panel implements TableCellRenderer {
	private static final long serialVersionUID = 1L;
	private final Ordenacao ordenacao;
	private final Descricao descricao;
	private final OrdemModel model;

	public OrdemHeader(OrdemModel model, String nome, int indice) {
		setBorder(BorderFactory.createEtchedBorder());
		ordenacao = new Ordenacao(indice, false);
		descricao = new Descricao(nome);
		add(BorderLayout.CENTER, descricao);
		add(BorderLayout.WEST, ordenacao);
		this.model = model;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int rowIndex, int vColIndex) {
		return this;
	}

	private class Descricao extends Label {
		private static final long serialVersionUID = 1L;

		private Descricao(String nome) {
			setHorizontalAlignment(CENTER);
			setToolTipText(nome);
			setText(nome);
		}
	}

	private class Ordenacao extends Label {
		private static final long serialVersionUID = 1L;
		private final boolean numero;
		private boolean asc = true;
		private final int indice;

		private Ordenacao(int indice, boolean numero) {
			setIcon(Icones.ORDEM);
			this.indice = indice;
			this.numero = numero;
		}

		private void ordenar() {
			if (numero) {
				setIcon(asc ? Icones.ASC_NUMERO : Icones.DESC_NUMERO);
			} else {
				setIcon(asc ? Icones.ASC_TEXTO : Icones.DESC_TEXTO);
			}
			model.ordenar(indice, numero, asc);
			asc = !asc;
		}
	}

	public boolean isOrdenacao(int resto) {
		return resto <= 16;
	}

	public void ordenar() {
		ordenacao.ordenar();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OrdemHeader) {
			OrdemHeader outro = (OrdemHeader) obj;
			return descricao.getText().equalsIgnoreCase(outro.descricao.getText());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return descricao.getText().hashCode();
	}
}