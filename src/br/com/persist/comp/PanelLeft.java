package br.com.persist.comp;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JPanel;

public class PanelLeft extends JPanel {
	private static final long serialVersionUID = 1L;

	public PanelLeft() {
		super(new FlowLayout(FlowLayout.LEFT));
	}

	public PanelLeft(Component... componentes) {
		super(new FlowLayout(FlowLayout.LEFT));
		adicionar(componentes);
	}

	public void adicionar(Component... componentes) {
		for (Component comp : componentes) {
			add(comp);
		}
	}
}