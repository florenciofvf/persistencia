package br.com.persist.plugins.mapa.organiza;

import br.com.persist.plugins.mapa.Forma;

public interface Organizador {
	public void parametros(String string);

	public void organizar(Forma forma);

	public void reiniciar();
}