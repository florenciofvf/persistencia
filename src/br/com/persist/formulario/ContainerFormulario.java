package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import br.com.persist.desktop.Container;
import br.com.persist.principal.Formulario;

public class ContainerFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final Container container;

	public ContainerFormulario(Formulario formulario, Container container, File file) {
		super(file.getName());
		container.setSuperficieFormulario(this);
		this.container = container;
		montarLayout();
		configurar();
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
		add(BorderLayout.CENTER, container);
	}
}