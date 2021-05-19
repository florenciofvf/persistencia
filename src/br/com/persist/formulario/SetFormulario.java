package br.com.persist.formulario;

import java.util.concurrent.atomic.AtomicReference;

public interface SetFormulario {
	public void set(AtomicReference<Formulario> ref);
}