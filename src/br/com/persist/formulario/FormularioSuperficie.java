package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import br.com.persist.objeto.FormularioAbstrato;

public class FormularioSuperficie extends FormularioAbstrato {
	private static final long serialVersionUID = 1L;
	private final Container container;

	public FormularioSuperficie(Formulario formulario, Container container, File file) {
		super(file.getName());
		container.setFormularioSuperficie(this);
		setLocationRelativeTo(formulario);
		this.container = container;
		montarLayout();
		configurar();
		setVisible(true);
	}

	private void configurar() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				container.excluido();
			}
		});
	}

	private void montarLayout() {
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, container);
	}
}