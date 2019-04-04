package br.com.persist.anotacao;

import java.awt.BorderLayout;

import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class AnotacaoFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final AnotacaoContainer container;

	public AnotacaoFormulario() {
		super(Mensagens.getString("label.anotacoes"));
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