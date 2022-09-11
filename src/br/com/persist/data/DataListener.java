package br.com.persist.data;

public interface DataListener {
	public void setParserTipo(Tipo tipo);

	public boolean somenteModelo();

	public String getModelo();

	public String getTitle();
}