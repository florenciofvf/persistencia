package br.com.persist.formulario;

import java.awt.BorderLayout;

import br.com.persist.container.ArvoreContainer;
import br.com.persist.principal.Formulario;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class ArvoreFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ArvoreContainer container;

	public ArvoreFormulario(Formulario formulario) {
		super(Mensagens.getString("label.arquivos"));
		container = new ArvoreContainer(this, formulario, this);
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