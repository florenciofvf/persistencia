package br.com.persist.container;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import br.com.persist.comp.BarraButton;
import br.com.persist.comp.Label;
import br.com.persist.comp.Panel;
import br.com.persist.comp.TextField;
import br.com.persist.desktop.Superficie;
import br.com.persist.util.Action;
import br.com.persist.util.IJanela;

public class CircularContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private TextField txtGrauTotal = new TextField("360");
	private TextField txtGrauOrigem = new TextField("0");
	private TextField txtRaio = new TextField("300");
	private final Superficie superficie;
	private final Tipo tipo;

	public CircularContainer(IJanela janela, Superficie superficie, Tipo tipo) {
		this.superficie = superficie;
		toolbar.ini(janela);
		this.tipo = tipo;
		montarLayout();
	}

	public static enum Tipo {
		EXPORTACAO, IMPORTACAO, NORMAL
	}

	private void montarLayout() {
		Panel panel = new Panel(new GridLayout(3, 2));
		panel.add(new Label("label.raio"));
		panel.add(txtRaio);
		panel.add(new Label("label.grau_origem"));
		panel.add(txtGrauOrigem);
		panel.add(new Label("label.grau_total"));
		panel.add(txtGrauTotal);

		add(BorderLayout.CENTER, panel);
		add(BorderLayout.NORTH, toolbar);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action atualizarAcao = Action.actionIconBaixar();

		@Override
		public void ini(IJanela janela) {
			super.ini(janela);

			addButton(atualizarAcao);

			atualizarAcao.setActionListener(e -> atualizar());
		}

		private void atualizar() {
		}
	}
}