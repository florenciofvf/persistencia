package br.com.persist.fichario;

import br.com.persist.assistencia.Icone;

public interface Titulo {
	public String getTituloMin();

	public String getTitulo();

	public boolean isAtivo();

	public String getHint();

	public Icone getIcone();
}