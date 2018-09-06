package br.com.persist.formulario;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class FormularioDesktop extends JFrame {
	private static final long serialVersionUID = 1L;
	private final Desktop desktop;

	public FormularioDesktop(Formulario formulario) {
		desktop = new Desktop(formulario, false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(1000, 600);
		montarLayout();
		setLocationRelativeTo(formulario);
		setVisible(true);
	}

	private void montarLayout() {
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, desktop);
	}
}