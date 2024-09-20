package br.com.persist.plugins.instrucao.compilador;

public interface IFuncaoContexto {
	BibliotecaContexto getBiblioteca();

	ParametrosContexto getParametros();

	boolean isRetornoVoid();

	String getNome();
}