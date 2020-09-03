package br.com.persist.abstrato;

import br.com.persist.principal.Formulario;

public abstract class AbstratoServico implements Servico {

	@Override
	public void processar(Formulario formulario, String comando, Object objeto) {
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