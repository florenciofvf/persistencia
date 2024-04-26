package br.com.persist.plugins.fragmento;

import java.util.List;

public interface FragmentoListener {
	public void aplicarFragmento(List<Fragmento> fragmentos, boolean concatenar, boolean and);

	public List<String> getGrupoFiltro();
}