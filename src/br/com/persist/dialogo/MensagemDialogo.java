package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.util.IJanela;

public class MensagemDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final MensagemContainer container;

	private MensagemDialogo(Dialog dialog, String titulo, String msg) {
		super(dialog, titulo);
		container = new MensagemContainer(this, msg);
		montarLayout();
	}

	private MensagemDialogo(Frame frame, String titulo, String msg) {
		super(frame, titulo);
		container = new MensagemContainer(this, msg);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}

	public static MensagemDialogo criar(Dialog dialog, String titulo, String msg) {
		return new MensagemDialogo(dialog, titulo, msg);
	}

	public static MensagemDialogo criar(Frame frame, String titulo, String msg) {
		return new MensagemDialogo(frame, titulo, msg);
	}
}