package br.com.persist.componente;

import java.awt.Font;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Preferencias;

public abstract class TextPane extends JTextPane {
	private static final long serialVersionUID = 1L;

	protected TextPane() {
		SwingUtilities.invokeLater(this::aplicarFontePreferencia);
	}

	private void aplicarFontePreferencia() {
		Font font = Preferencias.getFontPreferencia();
		if (font != null) {
			setFont(font);
		}
	}

	public void limpar() {
		setText(Constantes.VAZIO);
	}
}