package br.com.persist.plugins.objeto.vinculo;

import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.util.Objects;

import javax.swing.JTable;

import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;

public class OrdenarContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final transient OrdenarListener listener;
	private final Toolbar toolbar = new Toolbar();

	public OrdenarContainer(Janela janela, OrdenarListener listener) {
		this.listener = Objects.requireNonNull(listener);
		toolbar.ini(janela);
		montarLayout();
	}

	private void montarLayout() {
		JTable table = new JTable(new OrdenarModelo(listener.getPesquisas()));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		add(BorderLayout.CENTER, new ScrollPane(table));
		add(BorderLayout.NORTH, toolbar);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, SALVAR);
		}

		@Override
		protected void salvar() {
			listener.salvar();
			fechar();
		}
	}
}