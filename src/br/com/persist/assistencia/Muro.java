package br.com.persist.assistencia;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;

public class Muro extends Box {
	private static final long serialVersionUID = 1L;

	public Muro() {
		super(BoxLayout.Y_AXIS);
	}

	public void camada(Component... comp) {
		Box box = Box.createHorizontalBox();
		for (Component c : comp) {
			box.add(c);
		}
		add(box);
	}
}