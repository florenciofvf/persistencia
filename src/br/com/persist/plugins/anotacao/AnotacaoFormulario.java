package br.com.persist.plugins.anotacao;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;

public class AnotacaoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final AnotacaoContainer container;

	private AnotacaoFormulario(Formulario formulario) {
		super(AnotacaoMensagens.getString(AnotacaoConstantes.LABEL_ANOTACOES));
		container = new AnotacaoContainer(this, formulario);
		container.setAnotacaoFormulario(this);
		montarLayout();
	}

	private AnotacaoFormulario(AnotacaoContainer container) {
		super(AnotacaoMensagens.getString(AnotacaoConstantes.LABEL_ANOTACOES));
		container.setAnotacaoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, AnotacaoContainer container) {
		AnotacaoFormulario form = new AnotacaoFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario) {
		AnotacaoFormulario form = new AnotacaoFormulario(formulario);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setAnotacaoFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(this);
	}
}