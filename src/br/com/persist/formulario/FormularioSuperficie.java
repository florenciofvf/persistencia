package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
		configurar();
		setLocationRelativeTo(formulario);
		setVisible(true);
	}

	private void configurar() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				container.excluido();
			};
		});
	}

	private void montarLayout() {
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, container);
	}
}