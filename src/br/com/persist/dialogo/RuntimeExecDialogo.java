package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.container.RuntimeExecContainer;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class RuntimeExecDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final RuntimeExecContainer container;

	public RuntimeExecDialogo(Frame frame, Formulario formulario) {
		super(frame, Mensagens.getString(Constantes.LABEL_RUNTIME_EXEC));
		container = new RuntimeExecContainer(this, formulario, null, null);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}

	public static void criar(Formulario formulario) {
		RuntimeExecDialogo form = new RuntimeExecDialogo(formulario, formulario);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}
}