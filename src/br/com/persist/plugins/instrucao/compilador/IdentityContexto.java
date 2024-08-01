package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import br.com.persist.plugins.instrucao.compilador.Token.Tipo;

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

	@Override
	public String toString() {
		return id;
	}
}