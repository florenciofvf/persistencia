package br.com.persist.comp;

import javax.swing.Action;
import javax.swing.JButton;

public class Button extends JButton {
	private static final long serialVersionUID = 1L;

	public Button(Action action) {
		super(action);
	}

	public Button() {
	}
}