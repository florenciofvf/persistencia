package br.com.persist.componente;

import javax.swing.Action;
import javax.swing.JToggleButton;

public class ToggleButton extends JToggleButton {
	private static final long serialVersionUID = 1L;

	public ToggleButton(Action action) {
		super(action);
	}

	public void click() {
		doClick();
		setSelected(true);
		requestFocus();
	}
}