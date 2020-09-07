package br.com.persist.componente;

import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.componente.SetValor.Valor;
import br.com.persist.util.Util;

public class SetValor {
	private SetValor() {
	}

	public static void view(Valor valor) {
		SetValorDialogo form = new SetValorDialogo(valor);
		form.setVisible(true);
	}

	public static interface Valor {
		void set(String s);

		String getTitle();

		String get();
	}
}

class SetValorDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private TextArea textArea = new TextArea();
	private final transient Valor valor;

	SetValorDialogo(Valor valor) {
		super((Frame) null, valor.getTitle());
		textArea.setText(valor.get());
		this.valor = valor;
		toolbar.ini(null);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, textArea);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, LIMPAR, COPIAR, COLAR);
		}

		@Override
		protected void limpar() {
			textArea.limpar();
		}

		@Override
		protected void copiar() {
			String string = Util.getString(textArea.getTextAreaInner());
			Util.setContentTransfered(string);
			copiarMensagem(string);
			textArea.requestFocus();
		}

		@Override
		protected void colar() {
			Util.getContentTransfered(textArea.getTextAreaInner());
		}
	}

	@Override
	public void executarAoFecharDialogo() {
		valor.set(textArea.getText());
	}

	@Override
	public void dispose() {
		valor.set(textArea.getText());
		super.dispose();
	}
}