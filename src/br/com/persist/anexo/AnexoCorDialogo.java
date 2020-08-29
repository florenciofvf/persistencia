package br.com.persist.anexo;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.arquivo.Arquivo;
import br.com.persist.dialogo.AbstratoDialogo;

public class AnexoCorDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final AnexoCorContainer container;

	private AnexoCorDialogo(Frame frame, Arquivo arquivo) {
		super(frame, arquivo.toString());
		container = new AnexoCorContainer(this, arquivo);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static AnexoCorDialogo criar(Frame frame, Arquivo arquivo) {
		return new AnexoCorDialogo(frame, arquivo);
	}
}