package br.com.persist.componente;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;

import br.com.persist.assistencia.Icone;

public class CheckBoxMenuItem extends JCheckBoxMenuItem {
	private static final long serialVersionUID = 1L;

	public CheckBoxMenuItem(String text, Icone icone) {
		super(text, icone != null ? icone.getIcon() : null);
	}

	public CheckBoxMenuItem(String text) {
		super(text);
	}

	public CheckBoxMenuItem(Action a) {
		super(a);
	}
}