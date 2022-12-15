package br.com.persist.painel;

import java.awt.Component;

import javax.swing.JSplitPane;

public class PainelSeparador extends JSplitPane {
	private static final long serialVersionUID = -6437324355285130052L;

	public PainelSeparador(int orientation, Component left, Component right) {
		super(orientation, left, right);
	}

	public static PainelSeparador horizontal(Component left, Component right) {
		return new PainelSeparador(HORIZONTAL_SPLIT, left, right);
	}

	public static PainelSeparador vertical(Component left, Component right) {
		return new PainelSeparador(VERTICAL_SPLIT, left, right);
	}
}