package br.com.persist.plugins.anexo;

import java.awt.BorderLayout;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;

public class AnexoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final AnexoContainer container;

	private AnexoFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_ANEXOS));
		container = new AnexoContainer(this, formulario);
		container.setAnexoFormulario(this);
		montarLayout();
	}

	private AnexoFormulario(AnexoContainer container) {
		super(Mensagens.getString(Constantes.LABEL_ANEXOS));
		container.setAnexoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, AnexoContainer container) {
		AnexoFormulario form = new AnexoFormulario(container);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static void criar(Formulario formulario) {
		AnexoFormulario form = new AnexoFormulario(formulario);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	@Override
	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setAnexoFormulario(null);
		fechar();
	}

	@Override
	public void executarAoAbrirFormulario() {
		container.formularioVisivel();
	}
}