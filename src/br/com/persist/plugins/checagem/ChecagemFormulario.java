package br.com.persist.plugins.checagem;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;

public class ChecagemFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final ChecagemContainer container;

	private ChecagemFormulario(Formulario formulario, String conteudo, String idPagina) {
		super(ChecagemMensagens.getString(ChecagemConstantes.LABEL_CHECAGEM));
		container = new ChecagemContainer(this, formulario, conteudo, idPagina);
		container.setChecagemFormulario(this);
		montarLayout();
	}

	private ChecagemFormulario(ChecagemContainer container) {
		super(ChecagemMensagens.getString(ChecagemConstantes.LABEL_CHECAGEM));
		container.setChecagemFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, ChecagemContainer container) {
		ChecagemFormulario form = new ChecagemFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario, String conteudo, String idPagina) {
		ChecagemFormulario form = new ChecagemFormulario(formulario, conteudo, idPagina);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setChecagemFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(window);
	}
}