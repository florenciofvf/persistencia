package br.com.persist.dialogo;

import java.awt.BorderLayout;

import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Panel;
import br.com.persist.componente.TextArea;
import br.com.persist.util.IJanela;
import br.com.persist.util.Util;

public class MensagemContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();

	public MensagemContainer(IJanela janela, String string) {
		textArea.setText(string);
		toolbar.ini(janela);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, textArea);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(IJanela janela) {
			super.ini(janela, false, false);
			configCopiar1Acao(false);
		}

		@Override
		protected void copiar1() {
			String string = Util.getString(textArea.getTextAreaInner());
			Util.setContentTransfered(string);
			copiar1Mensagem(string);
			textArea.requestFocus();
		}
	}
}