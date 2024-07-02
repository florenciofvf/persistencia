package br.com.persist.plugins.instrucao.compilador;

public interface Contexto {
	/*
	 * "{", "{", "["
	 */
	public void inicializador(Compilador compilador, Token token);

	/*
	 * "]", "}", ")", ";"
	 */
	public void finalizador(Compilador compilador, Token token);

	/*
	 * ","
	 */
	public void separador(Compilador compilador, Token token);

	/*
	 * Matematicos: "+", "-", "*", "/", "%" Logicos: "^", "&", "|" Comparativos:
	 * "=", "!=", "<", ">", "<=", ">="
	 */
	public void operador(Compilador compilador, Token token);

	/*
	 * "const", "function", "if", "elseif", "else", "return"
	 */
	public void reservado(Compilador compilador, Token token);

	/*
	 * Exemplo: 'texto'
	 */
	public void string(Compilador compilador, Token token);

	/*
	 * Exemplo: 1, 46.9, -7, +7
	 */
	public void numero(Compilador compilador, Token token);

	/*
	 * Exemplo: somar1, param, file.open
	 */
	public void identity(Compilador compilador, Token token);
}