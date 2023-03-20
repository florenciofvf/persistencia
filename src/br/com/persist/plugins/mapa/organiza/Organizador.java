package br.com.persist.plugins.mapa.organiza;

import br.com.persist.plugins.mapa.Objeto;

public interface Organizador {
	public void parametros(String string);

	public void organizar(Objeto objeto);

	public void reiniciar();
}