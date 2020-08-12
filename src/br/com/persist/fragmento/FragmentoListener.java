package br.com.persist.fragmento;

import java.util.List;

public interface FragmentoListener {
	public void configFragmento(Fragmento f);

	public List<String> getGruposFiltro();
}