package br.com.persist.container;

import br.com.persist.comp.Panel;
import br.com.persist.principal.Formulario;
import br.com.persist.util.IJanela;

public abstract class AbstratoContainer extends Panel {
	private static final long serialVersionUID = 1L;
	protected final Formulario formulario;

	public AbstratoContainer(Formulario formulario) {
		this.formulario = formulario;
	}

	public Formulario getFormulario() {
		return formulario;
	}

	protected void destacarEmFormulario() {
	}

	protected void abrirEmFormulario() {
	}

	protected void retornoAoFichario() {
	}

	public abstract void setJanela(IJanela janela);
}