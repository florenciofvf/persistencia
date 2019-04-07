package br.com.persist.container;

import java.awt.BorderLayout;

import br.com.persist.Objeto;
import br.com.persist.comp.BarraButton;
import br.com.persist.comp.Panel;
import br.com.persist.comp.TextArea;
import br.com.persist.comp.TextField;
import br.com.persist.util.Action;
import br.com.persist.util.IJanela;
import br.com.persist.util.Icones;
import br.com.persist.util.Util;

public class ComplementoContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private TextField txtComplemento;

	public ComplementoContainer(IJanela janela, Objeto objeto, TextField txtComplemento) {
		textArea.setText(txtComplemento.getText());
		this.txtComplemento = txtComplemento;
		toolbar.ini(janela);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, textArea);
		add(BorderLayout.NORTH, toolbar);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action sucessoAcao = Action.actionIcon("label.aplicar", Icones.SUCESSO);

		@Override
		protected void ini(IJanela janela) {
			super.ini(janela);

			addButton(sucessoAcao);

			sucessoAcao.setActionListener(e -> {
				txtComplemento.setText(Util.normalizar(textArea.getText(), true));
				fechar();
			});
		}
	}
}