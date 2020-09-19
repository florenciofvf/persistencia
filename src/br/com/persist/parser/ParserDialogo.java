package br.com.persist.parser;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;

public class ParserDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final ParserContainer container;

	private ParserDialogo(Dialog dialog, ParserListener listener) {
		super(dialog, listener.getTitle());
		container = new ParserContainer(this, listener);
		montarLayout();
	}

	private ParserDialogo(Frame frame, ParserListener listener) {
		super(frame, listener.getTitle());
		container = new ParserContainer(this, listener);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static ParserDialogo criar(Dialog dialog, ParserListener listener) {
		return new ParserDialogo(dialog, listener);
	}

	public static ParserDialogo criar(Frame frame, ParserListener listener) {
		return new ParserDialogo(frame, listener);
	}
}