package br.com.persist.plugins.atributo;

public interface AtributoHandler {
	public void setString(String string) throws AtributoException;

	public void separador();

	public void iniMapa() throws AtributoException;

	public void fimMapa();
}