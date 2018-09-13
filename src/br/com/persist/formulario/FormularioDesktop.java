package br.com.persist.formulario;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import br.com.persist.comp.ScrollPane;
import br.com.persist.util.Mensagens;

public class FormularioDesktop extends JFrame {
	private static final long serialVersionUID = 1L;
	private final Desktop desktop;

	public FormularioDesktop(Formulario formulario) {
		super(Mensagens.getString("label.persistencia"));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		desktop = new Desktop(formulario, false);
		setSize(1000, 600);
		montarLayout();
		setLocationRelativeTo(formulario);
		setVisible(true);
	}

	private void montarLayout() {
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, new ScrollPane(desktop));
	}

	public Desktop getDesktop() {
		return desktop;
	}
}