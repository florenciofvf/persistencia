package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Token.Tipo;
import br.com.persist.plugins.instrucao.processador.Biblioteca;

public class IdentityContexto extends Container {
	private Token tokenIdentity;
	private final String id;

	public IdentityContexto(Token token) {
		this.id = token.getString();
		this.token = token;
	}

	public String getId() {
		return id;
	}

	@Override
	public void indexar(AtomicInteger atomic) {
		indice = atomic.getAndIncrement();
		indexarNegativo(atomic);
	}

	@Override
	public void filtroConstParam(List<Token> coletor) {
		coletor.add(tokenIdentity);
	}

	@Override
	public void salvar(PrintWriter pw) {
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

	private boolean ehParametro() {
		FuncaoContexto funcao = getFuncao();
		if (funcao == null) {
			throw new IllegalStateException();
		}
		ParametrosContexto parametros = funcao.getParametros();
		return parametros.contem(id);
	}

	private boolean ehFuncao() {
		BibliotecaContexto biblio = getBiblioteca();
		if (biblio == null) {
			throw new IllegalStateException();
		}
		String[] strings = id.split("\\.");
		if (strings.length == 1) {
			return biblio.contemFuncao(id);
		}
		Biblioteca biblioteca = null;
		try {
			biblioteca = biblio.cacheBiblioteca.getBiblioteca(strings[0]);
		} catch (InstrucaoException ex) {
			throw new IllegalStateException(ex.getMessage());
		}
		return biblioteca.contemFuncao(strings[1]);
	}

	@Override
	public String toString() {
		return id;
	}
}