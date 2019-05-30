package br.com.persist.comp;

import javax.swing.BorderFactory;
import javax.swing.JSplitPane;

public class SplitPane extends JSplitPane {
	private static final long serialVersionUID = 1L;

	public SplitPane(int orientacao) {
		super(orientacao);
		setBorder(BorderFactory.createEmptyBorder());
	}
}