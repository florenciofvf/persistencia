package br.com.persist.abstrato;

import br.com.persist.componente.Janela;
import br.com.persist.componente.Panel;
import br.com.persist.fichario.Pagina;
import br.com.persist.principal.Formulario;

public abstract class AbstratoContainer extends Panel implements Pagina {
	private static final long serialVersionUID = 1L;
	protected final Formulario formulario;

	public AbstratoContainer(Formulario formulario) {
		this.formulario = formulario;
	}

	public abstract void setJanela(Janela janela);

	public Formulario getFormulario() {
		return formulario;
	}

	protected void destacarEmFormulario() {
	}

	protected void clonarEmFormulario() {
	}

	protected void abrirEmFormulario() {
	}

	protected void retornoAoFichario() {
	}
}