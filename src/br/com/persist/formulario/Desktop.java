package br.com.persist.formulario;

import javax.swing.JDesktopPane;

public class Desktop extends JDesktopPane {
	private static final long serialVersionUID = 1L;
	private final Formulario formulario;

	public Desktop(Formulario formulario) {
		this.formulario = formulario;
	}
}