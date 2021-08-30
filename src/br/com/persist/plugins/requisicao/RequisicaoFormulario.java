package br.com.persist.plugins.requisicao;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;

public class RequisicaoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final RequisicaoContainer container;

	private RequisicaoFormulario(Formulario formulario, String conteudo, String idPagina) {
		super(RequisicaoMensagens.getString(RequisicaoConstantes.LABEL_REQUISICAO));
		container = new RequisicaoContainer(this, formulario, conteudo, idPagina);
		container.setRequisicaoFormulario(this);
		montarLayout();
	}

	private RequisicaoFormulario(RequisicaoContainer container) {
		super(RequisicaoMensagens.getString(RequisicaoConstantes.LABEL_REQUISICAO));
		container.setRequisicaoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, RequisicaoContainer container) {
		RequisicaoFormulario form = new RequisicaoFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario, String conteudo, String idPagina) {
		RequisicaoFormulario form = new RequisicaoFormulario(formulario, conteudo, idPagina);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setRequisicaoFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(window);
	}
}