package br.com.persist.plugins.fragmento;

import java.util.List;

public interface FragmentoListener {
	public void aplicarFragmento(Fragmento f);

	public List<String> getGrupoFiltro();
}