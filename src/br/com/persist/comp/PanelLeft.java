package br.com.persist.comp;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JPanel;

public class PanelLeft extends JPanel {
	private static final long serialVersionUID = 1L;

	public PanelLeft(Component... componentes) {
		super(new FlowLayout(FlowLayout.LEFT));
		adicionar(componentes);
	}

	public PanelLeft() {
		super(new FlowLayout(FlowLayout.LEFT));
	}

	public PanelLeft adicionar(Component... compts) {
		for (Component comp : compts) {
			add(comp);
		}

		return this;
	}
}