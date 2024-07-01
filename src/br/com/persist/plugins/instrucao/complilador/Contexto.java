package br.com.persist.plugins.instrucao.complilador;

public interface Contexto {
	/*
	 * "{", "{", "["
	 */
	public void inicializador(Token token);

	/*
	 * "]", "}", ")", ";"
	 */
	public void finalizador(Token token);

	/*
	 * ",", "."
	 */
	public void separador(Token token);

	/*
	 * Matematicos: "+", "-", "*", "/", "%" Logicos: "^", "&", "|" Comparativos:
	 * "=", "!=", "<", ">", "<=", ">="
	 */
	public void operador(Token token);

	/*
	 * "const", "function", "if", "elseif", "else", "return"
	 */
	public void reservado(Token token);

	/*
	 * Exemplo: 'texto'
	 */
	public void string(Token token);

	/*
	 * Exemplo: 1, 46.9, -7, +7
	 */
	public void numero(Token token);

	/*
	 * Exemplo: somar1, param
	 */
	public void identity(Token token);
}