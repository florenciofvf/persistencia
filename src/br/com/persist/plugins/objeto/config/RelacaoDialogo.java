package br.com.persist.plugins.objeto.config;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.plugins.objeto.ObjetoSuperficie;
import br.com.persist.plugins.objeto.Relacao;

public class RelacaoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final RelacaoContainer container;

	private RelacaoDialogo(Frame frame, ObjetoSuperficie objetoSuperficie, Relacao relacao)
			throws AssistenciaException {
		super(frame, relacao.getOrigem().getId() + " / " + relacao.getDestino().getId());
		setSize(Constantes.SIZE2);
		container = new RelacaoContainer(this, objetoSuperficie, relacao);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static RelacaoDialogo criar(Frame frame, ObjetoSuperficie objetoSuperficie, Relacao relacao)
			throws AssistenciaException {
		RelacaoDialogo form = new RelacaoDialogo(frame, objetoSuperficie, relacao);
		form.setLocationRelativeTo(frame);
		form.setVisible(true);
		return form;
	}
}