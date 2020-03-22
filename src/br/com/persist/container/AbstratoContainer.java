package br.com.persist.container;

import br.com.persist.comp.Panel;
import br.com.persist.fichario.Fichario;

public abstract class AbstratoContainer extends Panel {
	private static final long serialVersionUID = 1L;
	protected transient Fichario fichario;

	public AbstratoContainer(Fichario fichario) {
		this.fichario = fichario;
	}

	public AbstratoContainer() {
	}

	public Fichario getFichario() {
		return fichario;
	}

	public void setFichario(Fichario fichario) {
		this.fichario = fichario;
	}

	protected void destacarForm() {
	}

	protected void abrirEmFormul() {
	}

	protected void destacarFicha() {
	}
}