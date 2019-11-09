package br.com.persist.formulario;

import java.awt.BorderLayout;

import br.com.persist.container.ComparacaoContainer;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class ComparacaoFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ComparacaoContainer container;

	public ComparacaoFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_COMPARACAO));
		container = new ComparacaoContainer(this);
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