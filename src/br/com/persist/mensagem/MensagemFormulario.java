package br.com.persist.mensagem;

import java.awt.BorderLayout;
import java.io.File;

import br.com.persist.abstrato.AbstratoFormulario;

public class MensagemFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final MensagemContainer container;

	private MensagemFormulario(String titulo, String msg, File file) {
		super(titulo);
		container = new MensagemContainer(this, msg, file);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static MensagemFormulario criar(String titulo, String msg, File file) {
		return new MensagemFormulario(titulo, msg, file);
	}
}