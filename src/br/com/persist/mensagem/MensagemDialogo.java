package br.com.persist.mensagem;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.io.File;
import java.util.List;

import javax.swing.text.BadLocationException;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Text;

public class MensagemDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final MensagemContainer container;

	private MensagemDialogo(Dialog dialog, String titulo, String msg, File file) {
		super(dialog, file != null ? file.getAbsolutePath() : titulo);
		container = new MensagemContainer(this, msg, file);
		montarLayout();
	}

	private MensagemDialogo(Dialog dialog, String titulo, List<Text> listaText) throws BadLocationException {
		super(dialog, titulo);
		container = new MensagemContainer(this, listaText);
		montarLayout();
	}

	private MensagemDialogo(Frame frame, String titulo, String msg, File file) {
		super(frame, file != null ? file.getAbsolutePath() : titulo);
		container = new MensagemContainer(this, msg, file);
		montarLayout();
	}

	private MensagemDialogo(Frame frame, String titulo, List<Text> listaText) throws BadLocationException {
		super(frame, titulo);
		container = new MensagemContainer(this, listaText);
		montarLayout();
	}

	private void montarLayout() {
		setModalityType(ModalityType.DOCUMENT_MODAL);
		add(BorderLayout.CENTER, container);
	}

	public static MensagemDialogo criar(Dialog dialog, String titulo, String msg, File file) {
		return new MensagemDialogo(dialog, titulo, msg, file);
	}

	public static MensagemDialogo criar(Dialog dialog, String titulo, List<Text> listaText)
			throws BadLocationException {
		return new MensagemDialogo(dialog, titulo, listaText);
	}

	public static MensagemDialogo criar(Frame frame, String titulo, String msg, File file) {
		return new MensagemDialogo(frame, titulo, msg, file);
	}

	public static MensagemDialogo criar(Frame frame, String titulo, List<Text> listaText) throws BadLocationException {
		return new MensagemDialogo(frame, titulo, listaText);
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler();
	}

	public void setSel(String sel) {
		container.setSel(sel);
	}
}