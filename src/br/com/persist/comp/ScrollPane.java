package br.com.persist.comp;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

public class ScrollPane extends JScrollPane {
	private static final long serialVersionUID = 1L;

	public ScrollPane(Component view) {
		super(view);
		setBorder(BorderFactory.createEmptyBorder());
	}
}