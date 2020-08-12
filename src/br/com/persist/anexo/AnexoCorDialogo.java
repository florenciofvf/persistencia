package br.com.persist.anexo;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.arquivo.Arquivo;
import br.com.persist.dialogo.AbstratoDialogo;
import br.com.persist.util.IJanela;

public class AnexoCorDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final AnexoCorContainer container;

	public AnexoCorDialogo(Frame frame, Arquivo arquivo) {
		super(frame, arquivo.toString());
		container = new AnexoCorContainer(this, arquivo);
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