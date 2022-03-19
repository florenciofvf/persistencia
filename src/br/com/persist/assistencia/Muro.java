package br.com.persist.assistencia;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;

import br.com.persist.componente.Panel;

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

	public static Panel panelGrid(int borderTop, int borderBottom, Component... comps) {
		Panel container = new Panel(new GridLayout(0, 1));
		container.setBorder(BorderFactory.createMatteBorder(borderTop, 0, borderBottom, 0, Color.GRAY));
		for (Component c : comps) {
			container.add(c);
		}
		return container;
	}

	public static Panel panelGrid(Component... comps) {
		return panelGrid(0, 0, comps);
	}

	public static Panel panelGridBorderTop(Component... comps) {
		return panelGrid(1, 0, comps);
	}

	public static Panel panelGridBorderBottom(Component... comps) {
		return panelGrid(0, 1, comps);
	}
}