package br.com.persist.tabela;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import br.com.persist.comp.Label;
import br.com.persist.comp.PanelBorder;
import br.com.persist.util.Icones;

public class CabecalhoColuna extends PanelBorder implements TableCellRenderer {
	private static final long serialVersionUID = 1L;
	private final ModeloOrdenacao modelo;
	private final Ordenacao ordenacao;
	private final Descricao descricao;
	private final Filtro filtro;

	public CabecalhoColuna(ModeloOrdenacao modelo, Coluna coluna) {
		ordenacao = new Ordenacao(coluna.getIndice(), coluna.isNumero());
		descricao = new Descricao(coluna.getNome());
		filtro = new Filtro(coluna.getNome());
		add(BorderLayout.CENTER, descricao);
		add(BorderLayout.WEST, ordenacao);
		add(BorderLayout.EAST, filtro);
		this.modelo = modelo;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int rowIndex, int vColIndex) {
		return this;
	}

	private class Ordenacao extends Label {
		private static final long serialVersionUID = 1L;
		private final int indice;
		private boolean numero;
		private boolean asc;

		Ordenacao(int indice, boolean numero) {
			setIcon(Icones.UM_PIXEL);
			this.indice = indice;
			this.numero = numero;
		}

		void ordenar() {
			if (numero) {
				setIcon(asc ? Icones.ASC_NUMERO : Icones.DESC_NUMERO);
			} else {
				setIcon(asc ? Icones.ASC_TEXTO : Icones.DESC_TEXTO);
			}
			asc = !asc;
			modelo.ordenar(indice, numero, !asc);
		}
	}

	private class Filtro extends Label {
		private static final long serialVersionUID = 1L;
		private final String coluna;

		Filtro(String coluna) {
			setIcon(Icones.FILTRO);
			this.coluna = coluna;
		}

		void filtrar() {
			System.out.println("Filtrar em >>> " + coluna);
		}
	}

	private class Descricao extends Label {
		private static final long serialVersionUID = 1L;

		Descricao(String nome) {
			setToolTipText(nome);
			setText(nome);
		}
	}

	public boolean isOrdenacao(int resto) {
		return resto <= 16;
	}

	public boolean isFiltro(int resto, int largura) {
		return resto >= largura - 16;
	}

	public void ordenar() {
		ordenacao.ordenar();
	}

	public void filtrar() {
		filtro.filtrar();
	}
}