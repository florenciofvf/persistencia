package br.com.persist.abstrato;

import java.util.Objects;

import javax.swing.BorderFactory;

import br.com.persist.componente.Panel;
import br.com.persist.formulario.Formulario;

public abstract class AbstratoConfiguracao extends Panel {
	private static final long serialVersionUID = 1L;
	protected final Formulario formulario;

	public AbstratoConfiguracao(Formulario formulario, String titulo) {
		Objects.requireNonNull(formulario);
		Objects.requireNonNull(titulo);
		this.formulario = formulario;
		setBorder(BorderFactory.createTitledBorder(titulo));
	}
}