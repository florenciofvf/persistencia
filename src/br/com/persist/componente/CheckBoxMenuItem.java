package br.com.persist.componente;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

public class CheckBoxMenuItem extends JCheckBoxMenuItem {
	private static final long serialVersionUID = 1L;

	public CheckBoxMenuItem(String text, Icon icon) {
		super(text, icon);
	}

	public CheckBoxMenuItem(String text) {
		super(text);
	}

	public CheckBoxMenuItem(Action a) {
		super(a);
	}
}