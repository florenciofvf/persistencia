package br.com.persist.runtime_exec;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.dialogo.AbstratoDialogo;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;

public class RuntimeExecDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final RuntimeExecContainer container;

	private RuntimeExecDialogo(Frame frame, Formulario formulario) {
		super(frame, Mensagens.getString(Constantes.LABEL_RUNTIME_EXEC));
		container = new RuntimeExecContainer(this, formulario, null, null);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		RuntimeExecDialogo form = new RuntimeExecDialogo(formulario, formulario);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}
}