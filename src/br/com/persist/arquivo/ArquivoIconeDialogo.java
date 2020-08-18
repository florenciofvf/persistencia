package br.com.persist.arquivo;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.dialogo.AbstratoDialogo;
import br.com.persist.util.IJanela;

public class ArquivoIconeDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ArquivoIconeContainer container;

	ArquivoIconeDialogo(Frame frame, Arquivo arquivo) {
		super(frame, arquivo.toString());
		container = new ArquivoIconeContainer(this, arquivo);
		setTitle(container.getTotalIcones() + " - " + getTitle());
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}

	public static ArquivoIconeDialogo criar(Frame frame, Arquivo arquivo) {
		ArquivoIconeDialogo form = new ArquivoIconeDialogo(frame, arquivo);
		form.setLocationRelativeTo(frame);
		form.setVisible(true);
		return form;
	}
}