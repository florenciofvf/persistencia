package br.com.persist.formulario;

import java.awt.BorderLayout;

import br.com.persist.container.FragmentoContainer;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class FragmentoFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final FragmentoContainer container;

	public FragmentoFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_CONEXAO));
		container = new FragmentoContainer(this, null);
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