package br.com.persist.abstrato;

import java.awt.Component;
import java.io.File;

import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Pagina;

public abstract class AbstratoPagina implements Pagina {

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
	}

	@Override
	public void excluindoDoFichario(Fichario fichario) {
	}

	@Override
	public Component getComponent() {
		return null;
	}

	@Override
	public File getFile() {
		return null;
	}
}