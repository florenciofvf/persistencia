package br.com.persist.abstrato;

import java.util.Map;

import br.com.persist.formulario.Formulario;

public interface Servico extends WindowHandler {
	public void processar(Formulario formulario, Map<String, Object> args);

	public int getOrdem();
}