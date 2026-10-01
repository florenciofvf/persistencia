package br.com.persist.mensagem;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;
import java.util.List;

import javax.swing.text.BadLocationException;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Dimensao;
import br.com.persist.assistencia.DimensaoUtil;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Text;

public class MensagemDialogo extends AbstratoDialogo implements Dimensao {
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
		String string = container.getString();
		if (Preferencias.ajusteAuto(string)) {
			DimensaoUtil.ajustar(this);
		}
	}

	public void setSel(String sel) {
		container.setSel(sel);
	}

	@Override
	public boolean contemScrollHorizontal() {
		return container.contemScrollHorizontal();
	}

	@Override
	public boolean contemScrollVertical() {
		return container.contemScrollVertical();
	}

	@Override
	public void somarALargura(int valor) {
		Dimension dimension = getSize();
		dimension.width += valor;
		setSize(dimension);
	}

	@Override
	public void somarAAltura(int valor) {
		Dimension dimension = getSize();
		dimension.height += valor;
		setSize(dimension);
	}

	@Override
	public int getLargura() {
		return getWidth();
	}

	@Override
	public int getAltura() {
		return getHeight();
	}
}