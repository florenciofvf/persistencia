package br.com.persist.plugins.expressao.funcao;

import java.util.Map;

import br.com.persist.plugins.expressao.biblioteca.BibliotecaContexto;
import br.com.persist.plugins.expressao.parametros.ParametroContexto;

public interface IFuncaoContexto {
	public static final String VOID = "void";

	public Map<String, ParametroContexto> getMapaParametros();

	public BibliotecaContexto getBibliotecaContexto();

	public void configurarChaveParametro();

	public String getNome();
}