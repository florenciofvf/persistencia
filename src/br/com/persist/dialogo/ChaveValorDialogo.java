package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import br.com.persist.comp.BarraButton;
import br.com.persist.comp.TextArea;
import br.com.persist.util.ChaveValor;
import br.com.persist.util.IJanela;
import br.com.persist.util.Util;

public class ChaveValorDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private TextArea textArea = new TextArea();
	private transient ChaveValor chaveValor;

	public ChaveValorDialogo(ChaveValor chaveValor, String titulo) {
		super((Frame) null, titulo);
		textArea.setText(chaveValor.getValor());
		this.chaveValor = chaveValor;
		toolbar.ini(null);
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, textArea);
		add(BorderLayout.NORTH, toolbar);
	}

	@Override
	public void dispose() {
		chaveValor.setValor(textArea.getText());
		super.dispose();
	}

	@Override
	public void fechar() {
		dispose();
	}

	private void configurar() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				chaveValor.setValor(textArea.getText());
			}
		});
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(IJanela janela) {
			super.ini(janela, false, false);

			configCopiar1Acao();
		}

		@Override
		protected void copiar1() {
			String string = Util.getString(textArea.getTextAreaInner());
			Util.setContentTransfered(string);
		}
	}
}