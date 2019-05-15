package br.com.persist.formulario;

import java.awt.BorderLayout;

import br.com.persist.container.AnexoContainer;
import br.com.persist.principal.Formulario;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class AnexoFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final AnexoContainer container;

	public AnexoFormulario(Formulario formulario) {
		super(Mensagens.getString("label.anexos"));
		container = new AnexoContainer(this, formulario, this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}
}