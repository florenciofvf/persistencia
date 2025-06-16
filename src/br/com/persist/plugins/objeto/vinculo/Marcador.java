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
		return BorderFactory.createLineBorder(Color.MAGENTA, 3);
	}

	public static Border criarBordaMacro() {
		return BorderFactory.createLineBorder(Color.PINK, 3);
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