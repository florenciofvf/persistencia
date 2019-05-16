package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.Arquivo;
import br.com.persist.container.ArquivoIconeContainer;
import br.com.persist.util.IJanela;

public class ArquivoIconeDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ArquivoIconeContainer container;

	public ArquivoIconeDialogo(Frame frame, Arquivo arquivo) {
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
}