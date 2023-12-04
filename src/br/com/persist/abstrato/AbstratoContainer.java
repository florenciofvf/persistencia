package br.com.persist.abstrato;

import java.awt.Dialog;
import java.awt.Window;
import java.io.File;
import java.util.Map;

import br.com.persist.componente.Janela;
import br.com.persist.componente.Panel;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Pagina;
import br.com.persist.formulario.Formulario;

public abstract class AbstratoContainer extends Panel implements Pagina, WindowHandler, DialogHandler {
	private static final long serialVersionUID = 1L;
	protected final Formulario formulario;

	protected AbstratoContainer(Formulario formulario) {
		this.formulario = formulario;
	}

	public Formulario getFormulario() {
		return formulario;
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
}