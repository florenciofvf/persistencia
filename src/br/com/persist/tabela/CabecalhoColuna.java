package br.com.persist.tabela;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import br.com.persist.comp.Label;
import br.com.persist.comp.PanelBorder;
import br.com.persist.formulario.FormularioObjeto;
import br.com.persist.util.Icones;
import br.com.persist.util.Util;

public class CabecalhoColuna extends PanelBorder implements TableCellRenderer {
	private static final long serialVersionUID = 1L;
	private final FormularioObjeto formulario;
	private final ModeloOrdenacao modelo;
	private final Ordenacao ordenacao;
	private final Descricao descricao;
	private final Filtro filtro;

	public CabecalhoColuna(FormularioObjeto formulario, ModeloOrdenacao modelo, Coluna coluna) {
		ordenacao = new Ordenacao(coluna.getIndice(), coluna.isNumero());
		setBorder(BorderFactory.createEtchedBorder());
		descricao = new Descricao(coluna.getNome());
		filtro = new Filtro(coluna.getNome());
		add(BorderLayout.CENTER, descricao);
		add(BorderLayout.WEST, ordenacao);
		add(BorderLayout.EAST, filtro);
		this.formulario = formulario;
		this.modelo = modelo;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int rowIndex, int vColIndex) {
		return this;
	}

	private class Ordenacao extends Label {
		private static final long serialVersionUID = 1L;
		private boolean asc = true;
		private final int indice;
		private boolean numero;

		Ordenacao(int indice, boolean numero) {
			setIcon(Icones.ORDEM);
			this.indice = indice;
			this.numero = numero;
		}

		void ordenar() {
			if (numero) {
				setIcon(asc ? Icones.ASC_NUMERO : Icones.DESC_NUMERO);
			} else {
				setIcon(asc ? Icones.ASC_TEXTO : Icones.DESC_TEXTO);
			}
			modelo.ordenar(indice, numero, asc);
			asc = !asc;
		}
	}

	private class Filtro extends Label {
		private static final long serialVersionUID = 1L;
		private final String coluna;
		private String filtro;

		Filtro(String coluna) {
			setIcon(Icones.FILTRO);
			this.coluna = coluna;
		}

		void filtrar() {
			String string = filtro;

			if (Util.estaVazio(string)) {
				string = "AND " + coluna + " IN (   )";
			}

			String complemento = Util.getStringInput(CabecalhoColuna.this, coluna, string);

			if (complemento != null) {
				filtro = complemento;
				formulario.processarObjeto(complemento, null, CabecalhoColuna.this);
			}
		}

		void restaurar() {
			if (!Util.estaVazio(filtro)) {
				setIcon(Icones.OLHO);
			}
		}
	}

	private class Descricao extends Label {
		private static final long serialVersionUID = 1L;

		Descricao(String nome) {
			setHorizontalAlignment(CENTER);
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

	public String getFiltroComplemento() {
		String string = filtro.filtro;
		return string == null ? "" : string;
	}

	public void copiar(CabecalhoColuna cabecalho) {
		if (cabecalho != null) {
			filtro.filtro = cabecalho.filtro.filtro;
			filtro.restaurar();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CabecalhoColuna) {
			CabecalhoColuna outro = (CabecalhoColuna) obj;
			return filtro.coluna.equalsIgnoreCase(outro.filtro.coluna);
		}

		return false;
	}
}