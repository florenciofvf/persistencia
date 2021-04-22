package br.com.persist.plugins.requisicao;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.formulario.Formulario;

public class RequisicaoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final RequisicaoContainer container;

	private RequisicaoDialogo(Frame frame, Formulario formulario) {
		super(frame, RequisicaoMensagens.getString(RequisicaoConstantes.LABEL_REQUISICAO));
		container = new RequisicaoContainer(this, formulario, null, null);
		container.setRequisicaoDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		RequisicaoDialogo form = new RequisicaoDialogo(formulario, formulario);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	@Override
	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setRequisicaoDialogo(null);
		fechar();
	}

	@Override
	public void executarAoAbrirDialogo() {
		container.dialogoVisivel();
	}
}