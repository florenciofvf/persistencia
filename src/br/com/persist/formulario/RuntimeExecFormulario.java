package br.com.persist.formulario;

import java.awt.BorderLayout;

import br.com.persist.container.RuntimeExecContainer;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class RuntimeExecFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final RuntimeExecContainer container;

	public RuntimeExecFormulario(Formulario formulario, String conteudo, String idPagina) {
		super(Mensagens.getString(Constantes.LABEL_RUNTIME_EXEC));
		container = new RuntimeExecContainer(this, formulario, conteudo, idPagina);
		container.setRuntimeExecFormulario(this);
		montarLayout();
	}

	public RuntimeExecFormulario(RuntimeExecContainer container) {
		super(Mensagens.getString(Constantes.LABEL_RUNTIME_EXEC));
		container.setRuntimeExecFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}

	public static void criar(Formulario formulario, RuntimeExecContainer container) {
		RuntimeExecFormulario form = new RuntimeExecFormulario(container);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static void criar(Formulario formulario, String conteudo, String idPagina) {
		RuntimeExecFormulario form = new RuntimeExecFormulario(formulario, conteudo, idPagina);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public void retornoAoFichario() {
		remove(container);
		container.setJanela(null);
		container.setRuntimeExecFormulario(null);
		Formulario formulario = container.getFormulario();
		formulario.getFichario().getRuntimeExec().retornoAoFichario(formulario, container);
		dispose();
	}
}