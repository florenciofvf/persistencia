package br.com.persist.plugins.expressao.funcao;

import java.util.List;
import java.util.Map;

import br.com.persist.plugins.expressao.biblioteca.BibliotecaContexto;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.nativo.ChaveContexto;
import br.com.persist.plugins.expressao.parametros.ParametroContexto;

public interface IFuncaoContexto {
	public static final String VOID = "void";

	public void setRefFuncaoInterna(ChaveContexto refFuncaoInterna);

	public Map<String, ParametroContexto> getMapaParametros();

	public void listarFuncoesPre(List<Contexto> lista);

	public BibliotecaContexto getBibliotecaContexto();

	public void configurarChaveParametro();

	public String getNome();
}