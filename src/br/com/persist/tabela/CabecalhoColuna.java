package br.com.persist.tabela;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import br.com.persist.comp.Label;
import br.com.persist.comp.PanelBorder;
import br.com.persist.comp.TextField;
import br.com.persist.modelo.OrdenacaoModelo;
import br.com.persist.objeto.PainelObjeto;
import br.com.persist.util.Icones;
import br.com.persist.util.Sistema;
import br.com.persist.util.Util;

public class CabecalhoColuna extends PanelBorder implements TableCellRenderer {
	private static final long serialVersionUID = 1L;
	private final PainelObjeto painelObjeto;
	private final OrdenacaoModelo modelo;
	private final Ordenacao ordenacao;
	private final Descricao descricao;
	private final boolean comFiltro;
	private final Filtro filtro;

	public CabecalhoColuna(PainelObjeto painelObjeto, OrdenacaoModelo modelo, Coluna coluna, boolean comFiltro) {
		ordenacao = new Ordenacao(coluna.getIndice(), coluna.isNumero());
		setBorder(BorderFactory.createEtchedBorder());
		descricao = new Descricao(coluna.getNome());
		filtro = new Filtro(coluna.getNome());
		add(BorderLayout.CENTER, descricao);
		add(BorderLayout.WEST, ordenacao);
		this.painelObjeto = painelObjeto;
		this.comFiltro = comFiltro;
		if (comFiltro) {
			add(BorderLayout.EAST, filtro);
		}
		this.modelo = modelo;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int rowIndex, int vColIndex) {
		return this;
	}

	private class Descricao extends Label {
		private static final long serialVersionUID = 1L;

		Descricao(String nome) {
			setHorizontalAlignment(CENTER);
			setToolTipText(nome);
			setText(nome);
		}
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

		void filtrar(int x, int y) {
			new FiltroCaixa(painelObjeto.getFrame(), this, x, y);
		}

		void restaurar() {
			if (!Util.estaVazio(filtro)) {
				setIcon(Icones.OLHO);
			}
		}
	}

	private class FiltroCaixa extends JDialog {
		private static final long serialVersionUID = 1L;
		final TextField textField = new TextField();
		final Filtro filtro;

		FiltroCaixa(Frame frame, Filtro filtro, int x, int y) {
			super(frame, true);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			textField.addKeyListener(keyListener_);
			addWindowListener(windowListener_);
			String string = filtro.filtro;
			setLayout(new BorderLayout());
			this.filtro = filtro;

			if (Util.estaVazio(string)) {
				string = "AND " + filtro.coluna + " IN ()";
			}

			add(BorderLayout.CENTER, textField);
			textField.setText(string);
			setLocation(x, y);
			pack();
			setVisible(true);
		}

		KeyListener keyListener_ = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					filtro.filtro = textField.getText();
					painelObjeto.processarObjeto(filtro.filtro, null, CabecalhoColuna.this);
					dispose();
				} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					dispose();
				}
			}
		};

		WindowListener windowListener_ = new WindowAdapter() {
			@Override
			public void windowOpened(java.awt.event.WindowEvent e) {
				if (Sistema.getInstancia().isMac()) {
					int alturaArea = textField.getHeight();
					int alturaForm = getHeight();
					setLocation(getX(), getY() + (alturaForm - alturaArea));
				}
			}
		};
	}

	public boolean isOrdenacao(int resto) {
		return resto <= 16;
	}

	public boolean isFiltro(int resto, int largura) {
		if (comFiltro) {
			return resto >= largura - 16;
		}

		return false;
	}

	public void ordenar() {
		ordenacao.ordenar();
	}

	public void filtrar(int x, int y) {
		filtro.filtrar(x, y);
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

	@Override
	public int hashCode() {
		return filtro.coluna != null ? filtro.coluna.hashCode() : -1;
	}
}