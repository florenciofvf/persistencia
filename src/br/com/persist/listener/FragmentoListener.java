package br.com.persist.listener;

import java.util.List;

import br.com.persist.util.Fragmento;

public interface FragmentoListener {
	public void configFragmento(Fragmento f);

	public List<String> getGruposFiltro();
}