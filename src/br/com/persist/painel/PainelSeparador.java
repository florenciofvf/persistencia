package br.com.persist.painel;

import java.awt.Component;

import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

public class PainelSeparador extends JSplitPane {
	private static final long serialVersionUID = -6437324355285130052L;

	public PainelSeparador(int orientation, Component left, Component right) {
		super(orientation, get(left), get(right));
		SwingUtilities.invokeLater(() -> setDividerLocation(0.5));
	}

	public static PainelSeparador horizontal(Component left, Component right) {
		return new PainelSeparador(HORIZONTAL_SPLIT, left, right);
	}

	public static PainelSeparador vertical(Component left, Component right) {
		return new PainelSeparador(VERTICAL_SPLIT, left, right);
	}

	private static Component get(Component c) {
		if (c instanceof PainelFichario || c instanceof PainelSeparador) {
			PainelContainer container = new PainelContainer();
			container.adicionar(c);
			return container;
		} else if (c instanceof PainelTransferable) {
			PainelTransferable aba = (PainelTransferable) c;
			PainelFichario fichario = new PainelFichario();
			fichario.addTab(aba.getTitle(), aba);
			PainelContainer container = new PainelContainer();
			container.adicionar(fichario);
			return container;
		} else {
			throw new IllegalStateException();
		}
	}
}