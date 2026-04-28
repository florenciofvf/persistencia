package br.com.persist.plugins.expressao.nativo;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblioteca.Biblioteca;
import br.com.persist.plugins.expressao.biblioteca.CacheBiblioteca;
import br.com.persist.plugins.expressao.biblioteca.LinkBibliotecaContexto;
import br.com.persist.plugins.expressao.compilador.Context;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Doc;
import br.com.persist.plugins.expressao.compilador.Indexador;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.Token.Tipo;
import br.com.persist.plugins.expressao.compilador.TokenManager;
import br.com.persist.plugins.expressao.constante.ConstanteContexto;
import br.com.persist.plugins.expressao.funcao.FuncaoContexto;
import br.com.persist.plugins.expressao.funcao.IFuncaoContexto;
import br.com.persist.plugins.expressao.organiza.AliasContexto;
import br.com.persist.plugins.expressao.parametros.ParametroContexto;
import br.com.persist.plugins.expressao.processador.Funcao;

public class ChaveContexto extends Contexto implements LinkBibliotecaContexto {
	private boolean processado;

	public ChaveContexto(Token token) {
		super(token);
	}

	@Override
	protected void ajusteChavesEInvocacoes(Map<String, AliasContexto> mapaAlias, CacheBiblioteca cache)
			throws ExpressaoException {
		if (processado) {
			return;
		}
		String chamada = token.getString();
		String[] array = chamada.split("\\.");
		processarChave(chamada, array);
		processarChave2(chamada, array, mapaAlias, cache);
		processarChaveN(chamada, array, cache);
		processado = true;
	}

	@Override
	public void processarChave(String chamada, String[] array) {
		if (array.length != 1) {
			return;
		}
		AtomicBoolean sucesso = new AtomicBoolean();
		List<String> lista = checarSeEhParametroDeFuncao(chamada, sucesso);
		if (sucesso.get()) {
			setPrefixo(ParametroContexto.LOAD_PARAM);
			setBiblio(montarString(lista, false));
			token.setStyle(Token.PARAMETRO);
		} else {
			IFuncaoContexto funcao = getBibliotecaContexto().getFuncao(chamada);
			if (funcao != null) {
				setPrefixo(
						funcao.isRetornoVoid() ? FuncaoContexto.LOAD_FUNCTION_VOID : FuncaoContexto.LOAD_FUNCTION_CRET);
			} else {
				setPrefixo(ConstanteContexto.LOAD_CONST);
				token.setStyle(Token.CONSTANTE);
			}
			setBiblio(THIS);
		}
	}

	@Override
	public void processarChave2(String chamada, String[] array, Map<String, AliasContexto> mapaAlias,
			CacheBiblioteca cache) throws ExpressaoException {
		if (array.length != 2) {
			return;
		}
		String alias = array[0];
		AliasContexto aliasContexto = mapaAlias.get(alias);
		if (aliasContexto == null) {
			throw new ExpressaoException("erro.alias.nao_mapeado", alias);
		}
		String string = aliasContexto.getBiblioteca() + "." + array[1];
		processarChaveN(string, string.split("\\."), cache);
	}

	@Override
	public void processarChaveN(String chamada, String[] array, CacheBiblioteca cache) throws ExpressaoException {
		if (array.length < 3) {
			return;
		}
		int pos = chamada.lastIndexOf(".");
		String nomeAbsoluto = chamada.substring(0, pos);
		String chave = chamada.substring(pos + 1);
		Biblioteca biblioteca = cache.getBiblioteca(nomeAbsoluto);
		Funcao funcao = biblioteca.getFuncao2(chave);
		if (funcao != null) {
			setPrefixo(funcao.isTipoVoid() ? FuncaoContexto.LOAD_FUNCTION_VOID : FuncaoContexto.LOAD_FUNCTION_CRET);
		} else {
			setPrefixo(ConstanteContexto.LOAD_CONST);
			token.setStyle(Token.CONSTANTE);
		}
		setBiblio(nomeAbsoluto);
		token = new Token(chave, Tipo.VIRTUAL, -1);
	}

	@Context("chave")
	@Doc("chave / chave2 / chaveN")
	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		tokenManager.invalidar(token);
	}

	@Override
	public void empilharLocal(List<Contexto> lista) {
		lista.add(this);
		empilharLocalNegativo(lista);
	}

	@Override
	public void listar(List<Contexto> lista) {
		lista.add(this);
		listarNegativo(lista);
	}

	@Override
	public void indexar(Indexador indexador) {
		indice = indexador.get3();
	}

	@Override
	public void salvar(PrintWriter pw) throws ExpressaoException {
		print(pw, getPrefixo(), getBiblio(), token.getString());
	}
}