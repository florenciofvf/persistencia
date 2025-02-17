package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;

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
		AtomicBoolean paramSuper = new AtomicBoolean(false);
		if (ehParametro(id, paramSuper)) {
			salvarParametro(compilador, pw, paramSuper);
		} else if (ehFuncao()) {
			salvarFuncao(compilador, pw);
		} else {
			print(pw, ConstanteContexto.LOAD_CONST, id);
			if (tokenCor == null) {
				compilador.tokens.add(token.novo(Tipo.CONSTANTE));
			}
		}
		salvarNegativo(compilador, pw);
	}

	private void salvarParametro(Compilador compilador, PrintWriter pw, AtomicBoolean paramSuper) {
		if (paramSuper.get()) {
			print(pw, ParametroContexto.LOAD_PARAM_SUPER, id);
		} else {
			print(pw, ParametroContexto.LOAD_PARAM, id);
		}
		if (tokenCor == null) {
			compilador.tokens.add(token.novo(Tipo.PARAMETRO));
		}
	}

	private void salvarFuncao(Compilador compilador, PrintWriter pw) {
		char c = id.charAt(0);
		if (c >= '0' && c <= '9') {
			print(pw, FuncaoContexto.LOAD_FUNCTION_LAMB, id);
		} else {
			print(pw, FuncaoContexto.LOAD_FUNCTION, id);
		}
		if (tokenCor == null) {
			compilador.tokens.add(token.novo(Tipo.FUNCAO));
		}
	}

	private boolean ehFuncao() throws InstrucaoException {
		BibliotecaContexto biblio = getBiblioteca();
		if (biblio == null) {
			throw new InstrucaoException(ArgumentoContexto.ERRO_FUNCAO_PARENT, id);
		}
		String[] strings = id.split("\\.");
		if (strings.length == 1) {
			return biblio.contemFuncao(id);
		}
		Biblioteca biblioteca = null;
		try {
			biblioteca = biblio.cacheBiblioteca.getBiblioteca(strings[0], biblio);
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