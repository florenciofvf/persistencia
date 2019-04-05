package br.com.persist.formulario;

import java.awt.BorderLayout;

import br.com.persist.container.AnotacaoContainer;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class AnotacaoFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final AnotacaoContainer container;

	public AnotacaoFormulario() {
		super(Mensagens.getString(Constantes.LABEL_ANOTACOES));
		container = new AnotacaoContainer(this);
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