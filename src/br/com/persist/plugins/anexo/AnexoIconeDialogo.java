package br.com.persist.plugins.anexo;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;

public class AnexoIconeDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final AnexoIconeContainer container;

	private AnexoIconeDialogo(Frame frame, Anexo anexo) {
		super(frame, anexo.toString());
		container = new AnexoIconeContainer(this, anexo);
		setTitle(container.getTotalIcones() + " - " + getTitle());
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static AnexoIconeDialogo criar(Frame frame, Anexo anexo) {
		return new AnexoIconeDialogo(frame, anexo);
	}
}