package br.com.persist.fichario;

import javax.swing.Icon;

public interface Titulo {
	public String getTituloMin();

	public String getTitulo();

	public boolean isAtivo();

	public String getHint();

	public Icon getIcone();
}