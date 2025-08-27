package br.com.persist.plugins.objeto.vinculo;

import java.awt.Color;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

import br.com.persist.assistencia.Util;

public class Marcador {
	private JComponent comp;

	public void aplicarIf(String string) {
		if (comp != null && !Util.isEmpty(string)) {
			comp.setBorder(criarBorda());
			if (comp instanceof AbstractButton) {
				((AbstractButton) comp).setBorderPainted(true);
			}
		}
	}

	public JComponent getComp() {
		return comp;
	}

	public void setComp(JComponent comp) {
		this.comp = comp;
	}

	public static Border criarBorda() {
		return BorderFactory.createLineBorder(getColorVinculo(), 3);
	}

	public static Color getColorVinculo() {
		return Color.MAGENTA;
	}

	public static Border criarBordaMacro() {
		return BorderFactory.createLineBorder(getColorMacro(), 3);
	}

	public static Color getColorMacro() {
		return Color.PINK;
	}

	public static void aplicarBordaMacro(JComponent comp) {
		if (comp != null) {
			comp.setBorder(criarBordaMacro());
			if (comp instanceof AbstractButton) {
				((AbstractButton) comp).setBorderPainted(true);
			}
		}
	}
}