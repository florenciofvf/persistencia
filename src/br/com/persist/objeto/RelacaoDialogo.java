package br.com.persist.objeto;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.dialogo.AbstratoDialogo;

public class RelacaoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final RelacaoContainer container;

	private RelacaoDialogo(Frame frame, Superficie superficie, Relacao relacao) {
		super(frame, relacao.getOrigem().getId() + " / " + relacao.getDestino().getId());
		container = new RelacaoContainer(this, superficie, relacao);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static RelacaoDialogo criar(Frame frame, Superficie superficie, Relacao relacao) {
		RelacaoDialogo form = new RelacaoDialogo(frame, superficie, relacao);
		form.setLocationRelativeTo(frame);
		form.setVisible(true);
		return form;
	}
}