package br.com.persist.plugins.persistencia.tabela;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.JTable;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.plugins.persistencia.MemoriaModelo;
import br.com.persist.util.Util;

public class TabelaDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final TabelaContainer container;

	private TabelaDialogo(Dialog dialog, String titulo, MemoriaModelo modelo) {
		super(dialog, titulo + " [" + modelo.getRowCount() + "]");
		container = new TabelaContainer(this, modelo);
		montarLayout();
	}

	private TabelaDialogo(Frame frame, String titulo, MemoriaModelo modelo) {
		super(frame, titulo + " [" + modelo.getRowCount() + "]");
		container = new TabelaContainer(this, modelo);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void executarAoAbrirDialogo() {
		container.dialogoVisivel();
	}

	public static void criar(Dialog dialog, String titulo, MemoriaModelo modelo) {
		TabelaDialogo form = new TabelaDialogo(dialog, titulo, modelo);
		form.setLocationRelativeTo(dialog);
		form.setVisible(true);
	}

	public static void criar(Frame frame, String titulo, MemoriaModelo modelo) {
		TabelaDialogo form = new TabelaDialogo(frame, titulo, modelo);
		form.setLocationRelativeTo(frame);
		form.setVisible(true);
	}
}

class TabelaMemoria extends JTable {
	private static final long serialVersionUID = 1L;

	public TabelaMemoria(MemoriaModelo modelo) {
		super(modelo);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}
}

class TabelaContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private final TabelaMemoria tabela;

	public TabelaContainer(Janela janela, MemoriaModelo modelo) {
		tabela = new TabelaMemoria(modelo);
		toolbar.ini(janela);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(tabela));
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela);
		}
	}

	void dialogoVisivel() {
		Util.ajustar(tabela, getGraphics());
	}
}