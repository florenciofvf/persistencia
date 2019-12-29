package br.com.persist.formulario;

import java.awt.BorderLayout;

import br.com.persist.container.RequisicaoContainer;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class RequisicaoFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final RequisicaoContainer container;

	public RequisicaoFormulario() {
		super(Mensagens.getString(Constantes.LABEL_REQUISICAO));
		container = new RequisicaoContainer(this);
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