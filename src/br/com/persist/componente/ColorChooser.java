package br.com.persist.componente;

import java.awt.Color;
import java.awt.Component;
import java.awt.HeadlessException;

import javax.swing.JColorChooser;

public class ColorChooser extends JColorChooser {
	private static final long serialVersionUID = -8849221518360649530L;

	public ColorChooser(Color initialColor) {
		super(initialColor);
	}

	public ColorChooser() {
		super();
	}

	public static Color showDialog(Component component, String title, Color initialColor) throws HeadlessException {
		return JColorChooser.showDialog(component, title, initialColor);
	}
}