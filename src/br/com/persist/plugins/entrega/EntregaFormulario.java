package br.com.persist.plugins.entrega;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;

public class EntregaFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final EntregaContainer container;

	private EntregaFormulario(Formulario formulario, String conteudo, String idPagina) {
		super(formulario, EntregaMensagens.getString(EntregaConstantes.LABEL_ENTREGA));
		container = new EntregaContainer(this, formulario, conteudo, idPagina);
		container.setEntregaFormulario(this);
		montarLayout();
	}

	private EntregaFormulario(EntregaContainer container) {
		super(container.getFormulario(), EntregaMensagens.getString(EntregaConstantes.LABEL_ENTREGA));
		container.setEntregaFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, EntregaContainer container) {
		EntregaFormulario form = new EntregaFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario, String conteudo, String idPagina) {
		EntregaFormulario form = new EntregaFormulario(formulario, conteudo, idPagina);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setEntregaFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(window);
	}
}