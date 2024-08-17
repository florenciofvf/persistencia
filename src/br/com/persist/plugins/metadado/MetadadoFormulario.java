package br.com.persist.plugins.metadado;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.conexao.Conexao;

public class MetadadoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final MetadadoContainer container;

	private MetadadoFormulario(Formulario formulario, Conexao conexao) throws ArgumentoException {
		super(formulario, Mensagens.getString(Constantes.LABEL_METADADOS));
		container = new MetadadoContainer(this, formulario, conexao);
		container.setMetadadoFormulario(this);
		montarLayout();
	}

	private MetadadoFormulario(MetadadoContainer container) {
		super(container.getFormulario(), Mensagens.getString(Constantes.LABEL_METADADOS));
		container.setMetadadoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, MetadadoContainer container) {
		MetadadoFormulario form = new MetadadoFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario, Conexao conexao) throws ArgumentoException {
		MetadadoFormulario form = new MetadadoFormulario(formulario, conexao);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setMetadadoFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(window);
	}
}