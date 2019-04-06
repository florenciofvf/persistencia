package br.com.persist.formulario;

import java.awt.BorderLayout;

import br.com.persist.container.ConfigContainer;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class ConfigFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ConfigContainer container;

	public ConfigFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_ANOTACOES));
		container = new ConfigContainer(this, formulario);
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