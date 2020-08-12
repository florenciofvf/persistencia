package br.com.persist.ambiente;

import java.awt.BorderLayout;

import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.IJanela;

public class AmbienteFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final AmbienteContainer container;

	public AmbienteFormulario(Formulario formulario, String conteudo, AmbienteContainer.Ambiente ambiente) {
		super(ambiente.getDescricao());
		container = new AmbienteContainer(this, formulario, conteudo, ambiente);
		container.setAmbienteFormulario(this);
		montarLayout();
	}

	public AmbienteFormulario(AmbienteContainer container) {
		super(container.getAmbiente().getDescricao());
		container.setAmbienteFormulario(this);
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

	public void retornoAoFichario() {
		remove(container);
		container.setJanela(null);
		container.setAmbienteFormulario(null);
		Formulario formulario = container.getFormulario();
		formulario.getFichario().getAmbientes().retornoAoFichario(formulario, container);
		dispose();
	}
}