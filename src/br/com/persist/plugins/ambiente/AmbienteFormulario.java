package br.com.persist.plugins.ambiente;

import java.awt.BorderLayout;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;

public class AmbienteFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final AmbienteContainer container;

	private AmbienteFormulario(Formulario formulario, String conteudo, AmbienteContainer.Ambiente ambiente) {
		super(ambiente.getDescricao());
		container = new AmbienteContainer(this, formulario, conteudo, ambiente);
		container.setAmbienteFormulario(this);
		montarLayout();
	}

	private AmbienteFormulario(AmbienteContainer container) {
		super(container.getAmbiente().getDescricao());
		container.setAmbienteFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, AmbienteContainer container) {
		AmbienteFormulario form = new AmbienteFormulario(container);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static void criar(Formulario formulario, String conteudo, AmbienteContainer.Ambiente ambiente) {
		AmbienteFormulario form = new AmbienteFormulario(formulario, conteudo, ambiente);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	@Override
	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setAmbienteFormulario(null);
		fechar();
	}

	@Override
	public void executarAoAbrirFormulario() {
		container.formularioVisivel();
	}
}