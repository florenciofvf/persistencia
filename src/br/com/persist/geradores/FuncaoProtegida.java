package br.com.persist.geradores;

public class FuncaoProtegida extends Funcao {
	protected FuncaoProtegida(String retorno, String nome, Parametros parametros) {
		super("FuncaoProtegida", "protected", retorno, nome, parametros);
	}
}