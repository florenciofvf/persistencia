package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;
import java.util.List;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Token.Tipo;
import br.com.persist.plugins.instrucao.processador.Biblioteca;

public class ListaContexto extends Container {
	private Token tokenIdentity;
	private final String id;

	public ListaContexto(Token token) {
		this.id = token.getString();
		this.token = token;
	}

	public String getId() {
		return id;
	}

	@Override
	public void indexar(Indexador indexador) {
		sequencia = indexador.get();
		indexarNegativo(indexador);
	}

	@Override
	public void filtroConstParam(List<Token> coletor) {
		coletor.add(tokenIdentity);
	}

	public static boolean ehListaVazia(String string) {
		return string != null && string.length() < 3;
	}

	@Override
	public void salvar(PrintWriter pw) throws InstrucaoException {
		if (ehListaVazia(id)) {
			print(pw, InvocacaoContexto.INVOKE_EXP, "ilist.create");
			return;
		}
		if (ehParametro()) {
			print(pw, ParametroContexto.LOAD_PARAM, id);
			tokenIdentity = token.novo(Tipo.PARAMETRO);
		} else if (ehFuncao()) {
			print(pw, FuncaoContexto.LOAD_FUNCTION, id);
			tokenIdentity = token.novo(Tipo.FUNCAO);
		} else {
			print(pw, ConstanteContexto.LOAD_CONST, id);
			tokenIdentity = token.novo(Tipo.CONSTANTE);
		}
		salvarNegativo(pw);
	}

	private boolean ehParametro() throws InstrucaoException {
		FuncaoContexto funcao = getFuncao();
		if (funcao == null) {
			throw new InstrucaoException("erro.funcao_parent", id);
		}
		ParametrosContexto parametros = funcao.getParametros();
		return parametros.contem(id);
	}

	private boolean ehFuncao() throws InstrucaoException {
		BibliotecaContexto biblio = getBiblioteca();
		if (biblio == null) {
			throw new InstrucaoException("erro.funcao_parent", id);
		}
		String[] strings = id.split("\\.");
		if (strings.length == 1) {
			return biblio.contemFuncao(id);
		}
		Biblioteca biblioteca = null;
		try {
			biblioteca = biblio.cacheBiblioteca.getBiblioteca(strings[0]);
		} catch (InstrucaoException ex) {
			throw new InstrucaoException(ex.getMessage(), false);
		}
		return biblioteca.contemFuncao(strings[1]);
	}

	@Override
	public String toString() {
		return id;
	}
}