package br.com.persist.plugins.expressao.funcao;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblioteca.BibliotecaContexto;
import br.com.persist.plugins.expressao.biblioteca.CacheBiblioteca;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Indexador;
import br.com.persist.plugins.expressao.constante.ConstanteContexto;
import br.com.persist.plugins.expressao.local.LocalContexto;
import br.com.persist.plugins.expressao.nativo.ChaveContexto;
import br.com.persist.plugins.expressao.organiza.AliasContexto;
import br.com.persist.plugins.expressao.parametros.ParametroContexto;
import br.com.persist.plugins.expressao.parametros.ParametrosContexto;

public interface IFuncaoContexto {
	public static final String VOID = "void";

	public void ajusteChavesEInvocacoesIni(Map<String, AliasContexto> mapaAlias, CacheBiblioteca cache)
			throws ExpressaoException;

	public void setRefFuncaoInterna(ChaveContexto refFuncaoInterna);

	public void salvar(PrintWriter pw) throws ExpressaoException;

	public void configurarSaltosIni() throws ExpressaoException;

	public ConstanteContexto getConstanteContexto(String nome);

	public Map<String, ParametroContexto> getMapaParametros();

	public void ajusteFuncoesInternasIni(Indexador indexador);

	public LocalContexto getLocalContexto(String nome);

	public void listarFuncoesPre(List<Contexto> lista);

	public BibliotecaContexto getBibliotecaContexto();

	public void listarIni(List<Contexto> lista);

	public boolean isNomeOriginal(String nome);

	public ParametrosContexto getParametros();

	public boolean isRetornoVoid();

	public String getNome();
}