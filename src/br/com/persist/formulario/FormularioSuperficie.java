package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JFrame;

public class FormularioSuperficie extends JFrame {
	private static final long serialVersionUID = 1L;
	private final Container container;

	public FormularioSuperficie(Formulario formulario, Container container, File file) {
		super(file.getName());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		container.setFormularioSuperficie(this);
		this.container = container;
		setSize(1000, 600);
		montarLayout();
		setLocationRelativeTo(formulario);
		setVisible(true);
	}

	private void montarLayout() {
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, container);
	}
}