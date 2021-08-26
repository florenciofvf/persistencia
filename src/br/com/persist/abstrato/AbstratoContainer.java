package br.com.persist.abstrato;

import java.io.File;
import java.util.Map;

import br.com.persist.componente.Janela;
import br.com.persist.componente.Panel;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Pagina;
import br.com.persist.formulario.Formulario;

public abstract class AbstratoContainer extends Panel implements Pagina {
	private static final long serialVersionUID = 1L;
	protected final Formulario formulario;

	public AbstratoContainer(Formulario formulario) {
		this.formulario = formulario;
	}

	@Override
	public void processar(Formulario formulario, Map<String, Object> args) {
	}

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
	}

	@Override
	public void invertidoNoFichario(Fichario fichario) {
	}

	@Override
	public void excluindoDoFichario(Fichario fichario) {
	}

	public abstract void setJanela(Janela janela);

	@Override
	public File getFile() {
		return null;
	}
}