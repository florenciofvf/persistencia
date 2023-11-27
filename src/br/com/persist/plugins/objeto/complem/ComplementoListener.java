package br.com.persist.plugins.objeto.complem;

import java.util.Set;

public interface ComplementoListener {
	public void processarComplemento(String string);

	public Set<String> getColecaoComplemento();

	public String getComplemento();

	public String getTitle();
}