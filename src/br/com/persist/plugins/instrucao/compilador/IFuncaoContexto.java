package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;

public interface IFuncaoContexto {
	String getNome() throws InstrucaoException;

	BibliotecaContexto getBiblioteca();

	ParametrosContexto getParametros();

	IFuncaoContexto getFuncaoParent();

	boolean isRetornoVoid();
}