package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import br.com.persist.comp.ScrollPane;
import br.com.persist.objeto.FormularioAbstrato;
import br.com.persist.util.Mensagens;

public class FormularioDesktop extends FormularioAbstrato {
	private static final long serialVersionUID = 1L;
	private final Desktop desktop;

	public FormularioDesktop(Formulario formulario) {
		super(Mensagens.getString("label.persistencia"));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		desktop = new Desktop(formulario, false);
		montarLayout();
		configurar();
		setLocationRelativeTo(formulario);
		setVisible(true);
	}

	private void configurar() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				desktop.distribuir();
			}
		});
	}

	private void montarLayout() {
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, new ScrollPane(desktop));
	}

	public Desktop getDesktop() {
		return desktop;
	}
}