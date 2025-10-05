package br.com.persist.abstrato;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Window;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import br.com.persist.componente.Panel;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.FicharioHandler;
import br.com.persist.formulario.Formulario;

public abstract class AbstratoConfiguracao extends Panel implements WindowHandler, DialogHandler, FicharioHandler {
	private static final long serialVersionUID = 1L;
	protected final Formulario formulario;
	private final String titulo;

	protected AbstratoConfiguracao(Formulario formulario, String titulo) {
		this.formulario = Objects.requireNonNull(formulario);
		this.titulo = Objects.requireNonNull(titulo);
		destacar(false);
	}

	public void adicionadoAoFichario() {
	}

	@Override
	public void tabActivatedHandler(Fichario fichario) {
	}

	@Override
	public void dialogActivatedHandler(Dialog dialog) {
	}

	@Override
	public void dialogClosingHandler(Dialog dialog) {
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
	}

	@Override
	public void windowActivatedHandler(Window window) {
	}

	@Override
	public void windowClosingHandler(Window window) {
	}

	@Override
	public void windowOpenedHandler(Window window) {
	}

	public String getTitulo() {
		return titulo;
	}

	public void destacar(boolean b) {
		if (b) {
			Border border = BorderFactory.createLineBorder(Color.RED);
			setBorder(BorderFactory.createTitledBorder(border, Objects.requireNonNull(titulo)));
		} else {
			setBorder(BorderFactory.createTitledBorder(Objects.requireNonNull(titulo)));
		}
	}
}