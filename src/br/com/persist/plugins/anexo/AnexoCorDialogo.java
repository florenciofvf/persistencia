package br.com.persist.plugins.anexo;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;

public class AnexoCorDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final AnexoCorContainer container;

	private AnexoCorDialogo(Frame frame, Anexo anexo) {
		super(frame, anexo.toString());
		container = new AnexoCorContainer(this, anexo);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static AnexoCorDialogo criar(Frame frame, Anexo anexo) {
		return new AnexoCorDialogo(frame, anexo);
	}
}