package br.com.persist.componente;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class PanelCenter extends JPanel {
	private static final long serialVersionUID = 1L;

	public PanelCenter(Component... componentes) {
		super(new FlowLayout(FlowLayout.CENTER));
		adicionar(componentes);
	}

	public PanelCenter() {
		super(new FlowLayout(FlowLayout.CENTER));
	}

	public PanelCenter adicionar(Component... compts) {
		for (Component comp : compts) {
			add(comp);
		}
		return this;
	}

	public void borda() {
		setBorder(BorderFactory.createEtchedBorder());
	}
}