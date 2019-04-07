package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import br.com.persist.container.MacroContainer;
import br.com.persist.util.IJanela;

public class MacroDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final MacroContainer container;

	public MacroDialogo(Frame frame) {
		super(frame, "Macro");
		container = new MacroContainer();
		montarLayout();
		pack();
		configurar();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}

	private void configurar() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				container.getLista().setSelectedIndex(0);
			}
		});
	}
}