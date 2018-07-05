package br.com.persist.comp;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JPanel;

public class PanelCenter extends JPanel {
	private static final long serialVersionUID = 1L;

	public PanelCenter() {
		super(new FlowLayout(FlowLayout.CENTER));
	}

	public PanelCenter(Component... componentes) {
		super(new FlowLayout(FlowLayout.CENTER));
		adicionar(componentes);
	}

	public void adicionar(Component... componentes) {
		for (Component comp : componentes) {
			add(comp);
		}
	}
}