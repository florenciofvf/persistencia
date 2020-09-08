package br.com.persist.complemento;

import java.util.Set;

public interface ComplementoListener {
	public void processarComplemento(String string);

	public Set<String> getColecaoComplemento();

	public String getComplementoPadrao();

	public String getTitle();
}