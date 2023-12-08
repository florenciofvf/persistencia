package br.com.persist.plugins.atributo;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;

public class AtributoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final AtributoContainer container;

	private AtributoFormulario(Formulario formulario, String conteudo, String idPagina) {
		super(formulario, AtributoMensagens.getString(AtributoConstantes.LABEL_ATRIBUTO));
		container = new AtributoContainer(this, formulario, conteudo, idPagina);
		container.setAtributoFormulario(this);
		montarLayout();
	}

	private AtributoFormulario(AtributoContainer container) {
		super(container.getFormulario(), AtributoMensagens.getString(AtributoConstantes.LABEL_ATRIBUTO));
		container.setAtributoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, AtributoContainer container) {
		AtributoFormulario form = new AtributoFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario, String conteudo, String idPagina) {
		AtributoFormulario form = new AtributoFormulario(formulario, conteudo, idPagina);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setAtributoFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(window);
	}
}