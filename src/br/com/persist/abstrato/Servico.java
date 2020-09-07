package br.com.persist.abstrato;

import java.util.Map;

import br.com.persist.principal.Formulario;

public interface Servico {
	public void processar(Formulario formulario, Map<String, Object> args);

	public void fechandoFormulario(Formulario formulario);

	public void abrindoFormulario(Formulario formulario);

	public void visivelFormulario(Formulario formulario);

	public int getOrdem();
}