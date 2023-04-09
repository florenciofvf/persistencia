package br.com.persist.plugins.execucao;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;

public class ExecucaoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final ExecucaoContainer container;

	private ExecucaoFormulario(Formulario formulario, String conteudo) {
		super(ExecucaoMensagens.getString(ExecucaoConstantes.LABEL_EXECUCOES));
		container = new ExecucaoContainer(this, formulario, conteudo);
		container.setExecucaoFormulario(this);
		montarLayout();
	}

	private ExecucaoFormulario(ExecucaoContainer container) {
		super(ExecucaoMensagens.getString(ExecucaoConstantes.LABEL_EXECUCOES));
		container.setExecucaoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, ExecucaoContainer container) {
		ExecucaoFormulario form = new ExecucaoFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario, String conteudo) {
		ExecucaoFormulario form = new ExecucaoFormulario(formulario, conteudo);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setExecucaoFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(this);
	}
}