package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.container.RelacaoContainer;
import br.com.persist.desktop.Relacao;
import br.com.persist.desktop.Superficie;
import br.com.persist.util.IJanela;

public class RelacaoDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final RelacaoContainer container;

	public RelacaoDialogo(Frame frame, Superficie superficie, Relacao relacao) {
		super(frame, relacao.getOrigem().getId() + " / " + relacao.getDestino().getId());
		container = new RelacaoContainer(this, superficie, relacao);
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