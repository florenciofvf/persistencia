package br.com.persist.mensagem;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.io.File;

import br.com.persist.abstrato.AbstratoDialogo;

public class MensagemDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final MensagemContainer container;

	private MensagemDialogo(Dialog dialog, String titulo, String msg, File file) {
		super(dialog, file != null ? file.getAbsolutePath() : titulo);
		container = new MensagemContainer(this, msg, file);
		montarLayout();
	}

	private MensagemDialogo(Frame frame, String titulo, String msg, File file) {
		super(frame, file != null ? file.getAbsolutePath() : titulo);
		container = new MensagemContainer(this, msg, file);
		montarLayout();
	}

	private void montarLayout() {
		setModalityType(ModalityType.DOCUMENT_MODAL);
		add(BorderLayout.CENTER, container);
	}

	public static MensagemDialogo criar(Dialog dialog, String titulo, String msg, File file) {
		return new MensagemDialogo(dialog, titulo, msg, file);
	}

	public static MensagemDialogo criar(Frame frame, String titulo, String msg, File file) {
		return new MensagemDialogo(frame, titulo, msg, file);
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler();
	}

	public void setSel(String sel) {
		container.setSel(sel);
	}
}