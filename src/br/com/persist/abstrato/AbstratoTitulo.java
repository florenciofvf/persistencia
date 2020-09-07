package br.com.persist.abstrato;

import javax.swing.Icon;

import br.com.persist.fichario.Titulo;

public abstract class AbstratoTitulo implements Titulo {

	@Override
	public String getTituloMin() {
		return null;
	}

	@Override
	public boolean isAtivo() {
		return true;
	}

	@Override
	public String getTitulo() {
		return null;
	}

	@Override
	public String getHint() {
		return null;
	}

	@Override
	public Icon getIcone() {
		return null;
	}
}