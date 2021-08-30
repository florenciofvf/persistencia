package br.com.persist.componente;

import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.SetValor.Valor;

public class SetValor {
	private SetValor() {
	}

	public static void view(Component c, Valor valor) {
		Component comp = Util.getViewParent(c);
		SetValorDialogo form = null;
		if (comp instanceof Frame) {
			form = new SetValorDialogo((Frame) comp, valor);
		} else if (comp instanceof Dialog) {
			form = new SetValorDialogo((Dialog) comp, valor);
		} else {
			form = new SetValorDialogo((Frame) null, valor);
		}
		if (!Util.estaVazio(valor.get()) && valor.get().length() < 200) {
			form.pack();
		}
		form.setLocationRelativeTo(comp != null ? comp : c);
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

	SetValorDialogo(Frame frame, Valor valor) {
		super(frame, valor.getTitle());
		textArea.setText(valor.get());
		this.valor = valor;
		init();
	}

	SetValorDialogo(Dialog dialog, Valor valor) {
		super(dialog, valor.getTitle());
		textArea.setText(valor.get());
		this.valor = valor;
		init();
	}

	private void init() {
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
		protected void colar(boolean numeros, boolean letras) {
			Util.getContentTransfered(textArea.getTextAreaInner(), numeros, letras);
		}
	}

	@Override
	public void dialogClosingHandler(Dialog dialog) {
		valor.set(textArea.getText());
	}

	@Override
	public void dispose() {
		valor.set(textArea.getText());
		super.dispose();
	}
}