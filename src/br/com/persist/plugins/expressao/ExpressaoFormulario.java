package br.com.persist.plugins.expressao;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;

public class ExpressaoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final ExpressaoContainer container;

	private ExpressaoFormulario(Formulario formulario) {
		super(formulario, ExpressaoMensagens.getString(ExpressaoConstantes.LABEL_EXPRESSAO));
		container = new ExpressaoContainer(this, formulario);
		container.setExpressaoFormulario(this);
		montarLayout();
	}

	private ExpressaoFormulario(ExpressaoContainer container) {
		super(container.getFormulario(), ExpressaoMensagens.getString(ExpressaoConstantes.LABEL_EXPRESSAO));
		container.setExpressaoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, ExpressaoContainer container) {
		ExpressaoFormulario form = new ExpressaoFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario) {
		ExpressaoFormulario form = new ExpressaoFormulario(formulario);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setExpressaoFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(window);
	}
}