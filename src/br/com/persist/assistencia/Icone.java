package br.com.persist.assistencia;

import javax.swing.Icon;

public class Icone {
	private final Icon icon;

	public Icone(Icon icon) {
		this.icon = icon;
	}

	public Icone() {
		this(null);
	}

	public Icon getIcon() {
		return icon;
	}
}