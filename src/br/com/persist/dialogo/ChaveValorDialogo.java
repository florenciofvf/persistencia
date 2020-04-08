package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import br.com.persist.comp.TextArea;
import br.com.persist.util.ChaveValor;
import br.com.persist.util.IJanela;

public class ChaveValorDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private TextArea textArea = new TextArea();
	private transient ChaveValor chaveValor;

	public ChaveValorDialogo(ChaveValor chaveValor, String titulo) {
		super((Frame) null, titulo);
		textArea.setText(chaveValor.getValor());
		this.chaveValor = chaveValor;
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, textArea);
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
}