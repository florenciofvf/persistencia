package br.com.persist.plugins.objeto.vinculo;

import java.awt.Color;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

import br.com.persist.assistencia.Util;

public class Marcador {
	private final JComponent comp;

	public Marcador(JComponent comp) {
		this.comp = Objects.requireNonNull(comp);
	}

	public void aplicarIf(String string) {
		if (!Util.isEmpty(string)) {
			comp.setBorder(criarBorda());
		}
	}

	public static Border criarBorda() {
		return BorderFactory.createEtchedBorder(Color.RED, Color.BLUE);
	}
}