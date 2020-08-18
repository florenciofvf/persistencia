package br.com.persist.propriedades;

import java.awt.BorderLayout;

import br.com.persist.componente.BarraButton;
import br.com.persist.componente.TextArea;
import br.com.persist.container.AbstratoContainer;
import br.com.persist.principal.Formulario;
import br.com.persist.util.IJanela;
import br.com.persist.util.Util;

public class PropriedadesContainer extends AbstratoContainer {
	private static final long serialVersionUID = 1L;
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();

	public PropriedadesContainer(IJanela janela, Formulario formulario, String conteudo) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		abrir(conteudo);
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, textArea);
		add(BorderLayout.NORTH, toolbar);
	}

	private void abrir(String conteudo) {
		textArea.setText(conteudo);
	}

	public void setJanela(IJanela janela) {
		toolbar.setJanela(janela);
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