package br.com.persist.relacao;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.dialogo.AbstratoDialogo;
import br.com.persist.superficie.Superficie;

public class RelacaoConfigDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final RelacaoConfigContainer container;

	private RelacaoConfigDialogo(Frame frame, Superficie superficie, Relacao relacao) {
		super(frame, relacao.getOrigem().getId() + " / " + relacao.getDestino().getId());
		container = new RelacaoConfigContainer(this, superficie, relacao);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static RelacaoConfigDialogo criar(Frame frame, Superficie superficie, Relacao relacao) {
		RelacaoConfigDialogo form = new RelacaoConfigDialogo(frame, superficie, relacao);
		form.setLocationRelativeTo(frame);
		form.setVisible(true);
		return form;
	}
}