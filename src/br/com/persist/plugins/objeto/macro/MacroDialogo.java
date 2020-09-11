package br.com.persist.plugins.objeto.macro;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;

public class MacroDialogo extends AbstratoDialogo {
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

	public static MacroDialogo criar(Frame frame) {
		MacroDialogo form = new MacroDialogo(frame);
		form.setLocationRelativeTo(frame);
		form.setVisible(true);
		return form;
	}

	@Override
	public void executarAoAbrirDialogo() {
		container.selecionar(0);
	}
}