package br.com.persist.plugins.expressao.nativo;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblioteca.LinkBibliotecaContexto;
import br.com.persist.plugins.expressao.compilador.Context;
import br.com.persist.plugins.expressao.compilador.Contexto;
import br.com.persist.plugins.expressao.compilador.Doc;
import br.com.persist.plugins.expressao.compilador.Indexador;
import br.com.persist.plugins.expressao.compilador.Token;
import br.com.persist.plugins.expressao.compilador.TokenManager;
import br.com.persist.plugins.expressao.constante.ConstanteContexto;
import br.com.persist.plugins.expressao.invocacao.InvocacaoContexto;
import br.com.persist.plugins.expressao.organiza.AliasContexto;
import br.com.persist.plugins.expressao.parametros.ParametroContexto;

public class ChaveContexto extends Contexto implements LinkBibliotecaContexto {
	private boolean ehParametro;
	private String biblio;
	private String constt;
	private String alias;

	public ChaveContexto(Token token) {
		super(token);
		initLink();
	}

	public void initLink() {
		String chamada = token.getString();
		String[] array = chamada.split("\\.");
		if (array.length == 1) {
			biblio = InvocacaoContexto.THIS;
			constt = array[0];
		} else if (array.length == 2) {
			alias = array[0];
			constt = array[1];
		} else {
			int pos = chamada.lastIndexOf(".");
			biblio = chamada.substring(0, pos);
			constt = chamada.substring(pos + 1);
		}
	}

	@Context("chave")
	@Doc("chave / chave2 / chaveN")
	@Override
	public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
		tokenManager.invalidar(token);
	}

	@Override
	protected void configurarChaveParametroPre(Map<String, ParametroContexto> mapaParametros) {
		ehParametro = mapaParametros.get(token.getString()) != null;
	}

	@Override
	public void configurarLinkBibliotecaPre(Map<String, AliasContexto> mapaAlias) throws ExpressaoException {
		if (ehParametro) {
			return;
		}
		if (alias != null) {
			AliasContexto aliasContexto = mapaAlias.get(alias);
			if (aliasContexto == null) {
				throw new ExpressaoException("erro.alias.nao_mapeado", alias);
			}
			biblio = aliasContexto.getBiblioteca();
		}
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
		if (ehParametro) {
			print(pw, ParametroContexto.LOAD_PARAM, token.getString());
		} else {
			print(pw, ConstanteContexto.LOAD_CONST, biblio, constt);
		}
	}
}