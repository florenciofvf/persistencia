package br.com.persist.plugins.variaveis;

import java.awt.BorderLayout;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;

public class VariavelFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final VariavelContainer container;

	private VariavelFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_VARIAVEIS));
		container = new VariavelContainer(this, formulario);
		container.setVariavelFormulario(this);
		montarLayout();
	}

	private VariavelFormulario(VariavelContainer container) {
		super(Mensagens.getString(Constantes.LABEL_VARIAVEIS));
		container.setVariavelFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, VariavelContainer container) {
		VariavelFormulario form = new VariavelFormulario(container);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static void criar(Formulario formulario) {
		VariavelFormulario form = new VariavelFormulario(formulario);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	@Override
	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setVariavelFormulario(null);
		fechar();
	}

	@Override
	public void executarAoAbrirFormulario() {
		container.formularioVisivel();
	}
}