package br.com.persist.componente;

import java.awt.BorderLayout;
import java.awt.LayoutManager;

import javax.swing.JPanel;

public class Panel extends JPanel {
	private static final long serialVersionUID = 1L;

	public Panel(LayoutManager layout) {
		super(layout);
	}

	public Panel() {
		setLayout(new BorderLayout());
	}
}