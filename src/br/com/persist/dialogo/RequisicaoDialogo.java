package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.container.RequisicaoContainer;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class RequisicaoDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final RequisicaoContainer container;

	public RequisicaoDialogo(Frame frame) {
		super(frame, Mensagens.getString(Constantes.LABEL_REQUISICAO));
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