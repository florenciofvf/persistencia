package br.com.persist.parser;

public interface ParserListener {
	public void setParserTipo(Tipo tipo);

	public boolean somenteModelo();

	public String getModelo();

	public String getTitle();
}