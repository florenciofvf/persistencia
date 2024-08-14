package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;

public interface Contexto {
	/*
	 * "(", "{"
	 */
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException;

	/*
	 * ")", "}", ";"
	 */
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException;

	/*
	 * ","
	 */
	public void separador(Compilador compilador, Token token) throws InstrucaoException;

	/*
	 * Matematicos: "+", "-", "*", "/", "%" Logicos: "^", "&", "|" Comparativos:
	 * "=", "!=", "<", ">", "<=", ">=", Lista/Tipo ":"
	 */
	public void operador(Compilador compilador, Token token) throws InstrucaoException;

	/*
	 * "const", "function", "function_native", "if", "elseif", "else", "return"
	 */
	public void reservado(Compilador compilador, Token token) throws InstrucaoException;

	/*
	 * Exemplo: 'texto'
	 */
	public void string(Compilador compilador, Token token) throws InstrucaoException;

	/*
	 * Exemplo: 1, 46.9, -7, +7
	 */
	public void numero(Compilador compilador, Token token) throws InstrucaoException;

	/*
	 * Exemplo: somar1, param, file.open
	 */
	public void identity(Compilador compilador, Token token) throws InstrucaoException;
}