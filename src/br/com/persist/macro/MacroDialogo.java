package br.com.persist.macro;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.dialogo.AbstratoDialogo;
import br.com.persist.util.IJanela;

public class MacroDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final MacroContainer container;

	private MacroDialogo(Frame frame) {
		super(frame, "Macro");
		container = new MacroContainer();
		montarLayout();
		pack();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void executarAoAbrirDialog() {
		container.getLista().setSelectedIndex(0);
	}

	@Override
	public void fechar() {
		dispose();
	}

	public static MacroDialogo criar(Frame frame) {
		MacroDialogo form = new MacroDialogo(frame);
		form.setLocationRelativeTo(frame);
		form.setVisible(true);

		return form;
	}
}