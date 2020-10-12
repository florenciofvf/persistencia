package br.com.persist.plugins.persistencia.tabela;

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
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.TextField;
import br.com.persist.plugins.persistencia.Coluna;
import br.com.persist.plugins.persistencia.OrdenacaoModelo;

public class CabecalhoColuna extends Panel implements TableCellRenderer {
	private static final long serialVersionUID = 1L;
	private final transient CabecalhoColunaListener listener;
	private final OrdenacaoModelo modelo;
	private final Ordenacao ordenacao;
	private final Descricao descricao;
	private final boolean comFiltro;
	private final Filtro filtro;

	public CabecalhoColuna(CabecalhoColunaListener listener, OrdenacaoModelo modelo, Coluna coluna, boolean comFiltro) {
		ordenacao = new Ordenacao(coluna.getIndice(), coluna.isNumero());
		setBorder(BorderFactory.createEtchedBorder());
		descricao = new Descricao(coluna.getNome());
		filtro = new Filtro(coluna.getNome());
		add(BorderLayout.CENTER, descricao);
		add(BorderLayout.WEST, ordenacao);
		this.comFiltro = comFiltro;
		this.listener = listener;
		this.modelo = modelo;
		if (comFiltro) {
			add(BorderLayout.EAST, filtro);
		}
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
			modelo.ordenar(indice, numero, asc);
			asc = !asc;
		}
	}

	private class Filtro extends Label {
		private static final long serialVersionUID = 1L;
		private final String coluna;
		private String filtroString;

		private Filtro(String coluna) {
			setIcon(Icones.FILTRO);
			this.coluna = coluna;
		}

		private void filtrar(int x, int y) {
			new FiltroCaixa(this, x, y);
		}

		private void restaurar() {
			if (!Util.estaVazio(filtroString)) {
				setIcon(Icones.OLHO);
			}
		}

		@Override
		public void limpar() {
			setIcon(Icones.FILTRO);
			filtroString = null;
		}
	}

	private class FiltroCaixa extends JDialog {
		private static final long serialVersionUID = 1L;
		final TextField textField = new TextField();
		final Filtro filtro;

		private FiltroCaixa(Filtro filtro, int x, int y) {
			super((Frame) null, true);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			textField.addKeyListener(keyListenerInner);
			addWindowListener(windowListenerInner);
			String string = filtro.filtroString;
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

		private transient KeyListener keyListenerInner = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					filtro.filtroString = textField.getText();
					SwingUtilities.invokeLater(() -> listener.filtrar(CabecalhoColuna.this, filtro.filtroString));
					dispose();
				} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					dispose();
				}
			}
		};

		private transient WindowListener windowListenerInner = new WindowAdapter() {
			@Override
			public void windowOpened(java.awt.event.WindowEvent e) {
				if (Util.isMac()) {
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

	public void limparFiltro() {
		filtro.limpar();
	}

	public void filtrar(int x, int y) {
		filtro.filtrar(x, y);
	}

	public String getFiltroComplemento() {
		String string = filtro.filtroString;
		return string == null ? Constantes.VAZIO : string;
	}

	public void copiar(CabecalhoColuna cabecalho) {
		if (cabecalho != null) {
			filtro.filtroString = cabecalho.filtro.filtroString;
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