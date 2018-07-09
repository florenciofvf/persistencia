package br.com.persist.tabela;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import br.com.persist.comp.Label;
import br.com.persist.comp.PanelBorder;
import br.com.persist.util.Constantes;
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

		setOpaque(true);
		setBackground(Color.RED);
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
			// addMouseListener(mouseListener);
			setIcon(Icones.SUCESSO);
			this.indice = indice;
			this.numero = numero;

			setOpaque(true);
			setBackground(Color.BLUE);
		}

		MouseListener mouseListener = new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				System.out.println("CabecalhoColuna.Ordenacao.enclosing_method()");
			}

			public void mouseClicked(MouseEvent e) {
				System.out.println("CabecalhoColuna.Ordenacao.enclosing_method()");
			}

			public void mousePressed(java.awt.event.MouseEvent e) {
				System.out.println(e.getClickCount());
				if (e.getClickCount() >= Constantes.DOIS) {
					if (numero) {
						setIcon(asc ? Icones.ASC_NUMERO : Icones.DESC_NUMERO);
					} else {
						setIcon(asc ? Icones.ASC_TEXTO : Icones.DESC_TEXTO);
					}
					asc = !asc;
					modelo.ordenar(indice, numero);
				}
			}
		};
	}

	private class Filtro extends Label {
		private static final long serialVersionUID = 1L;
		private final String coluna;

		Filtro(String coluna) {
			// addMouseListener(mouseListener);
			this.coluna = coluna;
			setText("...");

			setOpaque(true);
			setBackground(Color.GREEN);
		}

		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				System.out.println("Filtrar em " + coluna);
			};
		};
	}

	private class Descricao extends Label {
		private static final long serialVersionUID = 1L;

		Descricao(String nome) {
			setToolTipText(nome);
			setText(nome);

			setOpaque(true);
			setBackground(Color.PINK);
		}
	}
}