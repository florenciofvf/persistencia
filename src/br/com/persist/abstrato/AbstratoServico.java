package br.com.persist.abstrato;

import java.util.Map;

import br.com.persist.formulario.Formulario;

public abstract class AbstratoServico implements Servico {

	@Override
	public void processar(Formulario formulario, Map<String, Object> args) {
	}

	@Override
	public void fechandoFormulario(Formulario formulario) {
	}

	@Override
	public void abrindoFormulario(Formulario formulario) {
	}

	@Override
	public void visivelFormulario(Formulario formulario) {
	}

	@Override
	public int getOrdem() {
		return 0;
	}
}