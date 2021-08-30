package br.com.persist.abstrato;

import java.awt.Window;
import java.util.Map;

import br.com.persist.formulario.Formulario;

public abstract class AbstratoServico implements Servico, WindowHandler {
	@Override
	public void processar(Formulario formulario, Map<String, Object> args) {
	}

	@Override
	public int getOrdem() {
		return 0;
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