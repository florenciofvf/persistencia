package br.com.persist.componente;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

public class ScrollPane extends JScrollPane {
	private static final long serialVersionUID = 1L;
	private static final int LARGURA = 12;

	public ScrollPane(Component view) {
		super(view);
		setBorder(BorderFactory.createEmptyBorder());
		getHorizontalScrollBar().setPreferredSize(new Dimension(0, LARGURA));
		getVerticalScrollBar().setPreferredSize(new Dimension(LARGURA, 0));
	}
}