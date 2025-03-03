package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoException;

public interface Contexto {
	/*
	 * "(", "{"
	 */
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException;

	public void antesFinalizador(Compilador compilador, Token token) throws InstrucaoException;

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

	public void antesReservado(Compilador compilador, Token token) throws InstrucaoException;

	/*
	 * "const", "defun", "defun_native", "if", "while", "elseif", "else", "return"
	 */
	public void reservado(Compilador compilador, Token token) throws InstrucaoException;

	/*
	 * Exemplo: 'lamb(e){return e;}'
	 */
	public void lambda(Compilador compilador, Token token) throws InstrucaoException;

	/*
	 * Exemplo: 'texto'
	 */
	public void string(Compilador compilador, Token token) throws InstrucaoException;

	/*
	 * Exemplo: [], [cab:cauda]
	 */
	public void lista(Compilador compilador, Token token) throws InstrucaoException;

	/*
	 * Exemplo: [map.chave], [obj.atributo]
	 */
	public void mapa(Compilador compilador, Token token) throws InstrucaoException;

	/*
	 * Exemplo: 1, 46.9, -7, +7
	 */
	public void numero(Compilador compilador, Token token) throws InstrucaoException;

	public void antesIdentity(Compilador compilador, Token token) throws InstrucaoException;

	/*
	 * Exemplo: somar1, param, file.open
	 */
	public void identity(Compilador compilador, Token token) throws InstrucaoException;

	public void salvar(Compilador compilador, PrintWriter pw) throws InstrucaoException;
}