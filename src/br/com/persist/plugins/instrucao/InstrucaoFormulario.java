package br.com.persist.plugins.instrucao;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;

public class InstrucaoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final InstrucaoContainer container;

	private InstrucaoFormulario(Formulario formulario, String conteudo, String idPagina) {
		super(formulario, InstrucaoMensagens.getString(InstrucaoConstantes.LABEL_INSTRUCAO));
		container = new InstrucaoContainer(this, formulario, conteudo, idPagina);
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

	public static void criar(Formulario formulario, String conteudo, String idPagina) {
		InstrucaoFormulario form = new InstrucaoFormulario(formulario, conteudo, idPagina);
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