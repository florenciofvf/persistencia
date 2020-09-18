package br.com.persist.abstrato;

import java.awt.Component;
import java.io.File;
import java.util.Map;

import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Pagina;
import br.com.persist.formulario.Formulario;

public abstract class AbstratoPagina implements Pagina {
	@Override
	public void processar(Formulario formulario, Map<String, Object> args) {
	}

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