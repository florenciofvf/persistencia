package br.com.persist.plugins.instrucao;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;

public class InstrucaoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final InstrucaoContainer container;

	private InstrucaoFormulario(Formulario formulario) {
		super(formulario, InstrucaoMensagens.getString(InstrucaoConstantes.LABEL_INSTRUCAO));
		container = new InstrucaoContainer(this, formulario);
		container.setInstrucaoFormulario(this);
		montarLayout();
	}

	private InstrucaoFormulario(InstrucaoContainer container) {
		super(container.getFormulario(), InstrucaoMensagens.getString(InstrucaoConstantes.LABEL_INSTRUCAO));
		container.setInstrucaoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, InstrucaoContainer container) {
		InstrucaoFormulario form = new InstrucaoFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario) {
		InstrucaoFormulario form = new InstrucaoFormulario(formulario);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setInstrucaoFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(window);
	}
}