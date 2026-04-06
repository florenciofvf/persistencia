package br.com.persist.plugins.expressao.biblioteca;

public interface LinkBiblioteca {
	public String getNomeBiblioAbsoluto();

	public boolean isRefLocal();

	public int getIndice();
}