package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.container.AnotacaoContainer;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class AnotacaoDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final AnotacaoContainer container;

	public AnotacaoDialogo(Frame frame) {
		super(frame, Mensagens.getString(Constantes.LABEL_ANOTACOES));
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