package br.com.persist.formulario;

import java.awt.BorderLayout;

import br.com.persist.container.AnexoContainer;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class AnexoFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final AnexoContainer container;

	public AnexoFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_ANEXOS));
		container = new AnexoContainer(this, formulario);
		container.setAnexoFormulario(this);
		montarLayout();
	}

	public AnexoFormulario(AnexoContainer container) {
		super(Mensagens.getString(Constantes.LABEL_ANEXOS));
		container.setAnexoFormulario(this);
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

	public void retornoAoFichario() {
		remove(container);
		container.setJanela(null);
		container.setAnexoFormulario(null);
		Formulario formulario = container.getFormulario();
		formulario.getFichario().getAnexos().retornoAoFichario(formulario, container);
		dispose();
	}
}