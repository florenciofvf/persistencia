package br.com.persist.abstrato;

import br.com.persist.principal.Formulario;

public interface Servico {
	public void processar(Formulario formulario, String comando, Object objeto);

	public void fechandoFormulario(Formulario formulario);

	public void abrindoFormulario(Formulario formulario);

	public void visivelFormulario(Formulario formulario);

	public int getOrdem();
}