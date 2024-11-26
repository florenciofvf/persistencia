package br.com.persist.componente;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class PanelBloco extends JPanel {
	private static final long serialVersionUID = 1L;

	public PanelBloco(Component... componentes) {
		super(new GridLayout(1, 0));
		adicionar(componentes);
	}

	public PanelBloco() {
		super(new GridLayout(1, 0));
	}

	public PanelBloco adicionar(Component... compts) {
		for (Component comp : compts) {
			add(comp);
		}
		return this;
	}

	public void borda() {
		setBorder(BorderFactory.createEtchedBorder());
	}
}