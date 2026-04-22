package br.com.persist.plugins.expressao.funcao;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblioteca.BibliotecaContexto;
import br.com.persist.plugins.expressao.biblioteca.CacheBiblioteca;
import br.com.persist.plugins.expressao.compilador.Context;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Doc;
import br.com.persist.plugins.expressao.compilador.Indexador;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.Token.Tipo;
import br.com.persist.plugins.expressao.compilador.TokenExec;
import br.com.persist.plugins.expressao.compilador.TokenManager;
import br.com.persist.plugins.expressao.instrucoes.InstrucoesContexto;
import br.com.persist.plugins.expressao.nativo.ChaveContexto;
import br.com.persist.plugins.expressao.organiza.AliasContexto;
import br.com.persist.plugins.expressao.parametros.ParametroContexto;
import br.com.persist.plugins.expressao.parametros.ParametrosContexto;

public class FuncaoContexto extends Contexto implements IFuncaoContexto {
	private TokenExec[] execs = { new Chave(), new IniParametros(), new TipoRetornoOuIniInstrucoes() };
	public static final String LOAD_FUNCTION_INNER_CRET = "load_function_inner_cret";
	public static final String LOAD_FUNCTION_INNER_VOID = "load_function_inner_void";
	public static final String LOAD_FUNCTION_CRET = "load_function_cret";
	public static final String LOAD_FUNCTION_VOID = "load_function_void";
	public static final String PREFIXO_TIPO_VOID = "tipo_void";
	public static final String PREFIXO_FUNCAO = "funcao ";
	public static final String DEFUN = "defun";
	private ChaveContexto refFuncaoInterna;
	protected boolean retornoVoid;

	@Override
	public boolean isRetornoVoid() {
		return retornoVoid;
	}

	@Override
	public void ajusteChavesEInvocacoesIni(Map<String, AliasContexto> mapaAlias, CacheBiblioteca cache)
			throws ExpressaoException {
		ajusteChavesEInvocacoes(mapaAlias, cache);
	}

	public void setRefFuncaoInterna(ChaveContexto refFuncaoInterna) {
		this.refFuncaoInterna = refFuncaoInterna;
	}

	@Override
	public void salvar(PrintWriter pw) throws ExpressaoException {
		if (refFuncaoInterna != null) {
			refFuncaoInterna.salvar(pw);
			return;
		}
		if (parent instanceof BibliotecaContexto) {
			pw.println(PREFIXO_FUNCAO + getNome());
		} else {
			List<String> lista = listarHierarquia();
			String origem = montarString(lista);
			pw.println(PREFIXO_FUNCAO + getNome() + ExpressaoConstantes.ESPACO + origem);
		}
		if (retornoVoid) {
			pw.println(PREFIXO_TIPO_VOID);
		}
		getParametros().salvar(pw);
	}

	private List<String> listarHierarquia() {
		List<String> lista = new ArrayList<>();
		Contexto c = parent;
		while (c != null) {
			if (c instanceof FuncaoContexto) {
				FuncaoContexto funcao = (FuncaoContexto) c;
				lista.add(funcao.getNome());
			}
			c = c.getParent();
		}
		return lista;
	}

	@Override
	public void configurarSaltosIni() throws ExpressaoException {
		configurarSaltos();
	}

	@Override
	public Map<String, ParametroContexto> getMapaParametros() {
		return getParametros().getMapaParametros();
	}

	@Override
	public void ajusteFuncoesInternasIni(Indexador indexador) {
		ajusteFuncoesInternas(indexador);
	}

	@Override
	public void listarFuncoesPre(List<Contexto> lista) {
		lista.add(this);
	}

	@Override
	public void listarIni(List<Contexto> lista) {
		listar(lista);
	}

	public ParametrosContexto getParametros() {
		return (ParametrosContexto) getPrimeiro();
	}

	@Override
	public String getNome() {
		return token.getString();
	}

	public ChaveContexto getRefFuncaoInterna() {
		return refFuncaoInterna;
	}

	@Override
	protected void ajusteFuncoesInternasPre(Indexador indexador) {
		if (parent instanceof BibliotecaContexto) {
			return;
		}
		String string = indexador.get1() + "_" + token.getString();
		token = new Token(string, Tipo.VIRTUAL, -1);
		refFuncaoInterna = new ChaveContexto(token);
		refFuncaoInterna.setPrefixo(retornoVoid ? LOAD_FUNCTION_INNER_VOID : LOAD_FUNCTION_INNER_CRET);
		refFuncaoInterna.setBiblio(THIS);
	}

	@Override
	protected void selecionadoVia(TokenManager tokenManager, Contexto contexto) throws ExpressaoException {
		if (contexto instanceof InstrucoesContexto) {
			tokenManager.selecionarParentDe(this);
		}
	}

	@Context("funcao")
	@Doc({ "funcao chave parametros instrucoes", "funcao chave parametros void instrucoes" })
	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		checarIndiceEstado(tokenManager, execs, token);
		execs[indiceEstado].processar(tokenManager, token);
	}

	class IniParametros implements TokenExec {
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isAbreParentese()) {
				ParametrosContexto parametros = new ParametrosContexto();
				tokenManager.selecionar(parametros);
				adicionar(parametros);
				indiceEstado++;
			} else {
				tokenManager.invalidar(token);
			}
		}
	}

	class TipoRetornoOuIniInstrucoes implements TokenExec {
		@Override
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isAbreChave()) {
				InstrucoesContexto instrucoes = new InstrucoesContexto(true);
				tokenManager.selecionar(instrucoes);
				adicionar(instrucoes);
				indiceEstado++;
			} else if (VOID.equals(token.getString())) {
				execs = new TokenExec[] { new Chave(), new IniParametros(), new TipoRetornoOuIniInstrucoes(),
						new IniInstrucoes(true) };
				retornoVoid = true;
				indiceEstado++;
			} else {
				tokenManager.invalidar(token);
			}
		}
	}

	@Override
	public void empilharLocal(List<Contexto> lista) {
		if (refFuncaoInterna != null) {
			lista.add(this);
		} else {
			super.empilharLocal(lista);
		}
	}

	@Override
	public void listar(List<Contexto> lista) {
		if (refFuncaoInterna != null) {
			lista.add(this);
		} else {
			super.listar(lista);
		}
	}

	@Override
	public void indexar(Indexador indexador) {
		if (refFuncaoInterna != null) {
			refFuncaoInterna.indexar(indexador);
		}
	}
}