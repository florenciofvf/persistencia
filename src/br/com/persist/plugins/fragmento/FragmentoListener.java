package br.com.persist.plugins.fragmento;

import java.util.List;

public interface FragmentoListener {
	public void aplicarFragmento(List<Fragmento> fragmentos);

	public List<String> getGrupoFiltro();
}