package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Token.Tipo;
import br.com.persist.plugins.instrucao.processador.Biblioteca;

public class IdentityContexto extends Container {
	private final String id;
	Token tokenCor;

	public IdentityContexto(Token token) {
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
	public void salvar(Compilador compilador, PrintWriter pw) throws InstrucaoException {
		if (tokenCor != null) {
			compilador.tokens.add(tokenCor);
		}
		if (ehParametro(id)) {
			print(pw, ParametroContexto.LOAD_PARAM, id);
			if (tokenCor == null) {
				compilador.tokens.add(token.novo(Tipo.PARAMETRO));
			}
		} else if (ehFuncao()) {
			print(pw, FuncaoContexto.LOAD_FUNCTION, id);
			if (tokenCor == null) {
				compilador.tokens.add(token.novo(Tipo.FUNCAO));
			}
		} else {
			print(pw, ConstanteContexto.LOAD_CONST, id);
			if (tokenCor == null) {
				compilador.tokens.add(token.novo(Tipo.CONSTANTE));
			}
		}
		salvarNegativo(compilador, pw);
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