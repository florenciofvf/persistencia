package br.com.persist.mensagem;

import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.io.File;
import java.io.PrintWriter;

import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Panel;
import br.com.persist.componente.TextArea;

public class MensagemContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private final File file;

	public MensagemContainer(Janela janela, String string, File file) {
		this.file = file;
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

		public void ini(Janela janela) {
			if (file != null) {
				super.ini(janela, COPIAR, COLAR, SALVAR);
			} else {
				super.ini(janela, COPIAR, COLAR);
			}
		}

		@Override
		protected void copiar() {
			String string = Util.getString(textArea.getTextAreaInner());
			Util.setContentTransfered(string);
			copiarMensagem(string);
			textArea.requestFocus();
		}

		@Override
		protected void colar(boolean numeros, boolean letras) {
			Util.getContentTransfered(textArea.getTextAreaInner(), numeros, letras);
		}

		@Override
		protected void salvar() {
			try (PrintWriter pw = new PrintWriter(file)) {
				pw.print(textArea.getText());
				salvoMensagem();
			} catch (Exception e) {
				Util.mensagem(MensagemContainer.this, e.getMessage());
			}
		}
	}
}