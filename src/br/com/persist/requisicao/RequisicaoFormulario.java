package br.com.persist.requisicao;

import java.awt.BorderLayout;

import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;

public class RequisicaoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final RequisicaoContainer container;

	private RequisicaoFormulario(Formulario formulario, String conteudo, String idPagina) {
		super(Mensagens.getString(Constantes.LABEL_REQUISICAO));
		container = new RequisicaoContainer(this, formulario, conteudo, idPagina);
		container.setRequisicaoFormulario(this);
		montarLayout();
	}

	private RequisicaoFormulario(RequisicaoContainer container) {
		super(Mensagens.getString(Constantes.LABEL_REQUISICAO));
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
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static void criar(Formulario formulario, String conteudo, String idPagina) {
		RequisicaoFormulario form = new RequisicaoFormulario(formulario, conteudo, idPagina);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public void retornoAoFichario() {
		remove(container);
		container.setJanela(null);
		container.setRequisicaoFormulario(null);
		fechar();
	}
}