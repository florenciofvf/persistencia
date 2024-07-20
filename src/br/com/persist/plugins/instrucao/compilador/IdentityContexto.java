package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

public class IdentityContexto extends Container {
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
		super.indexar(atomic);
		indice = atomic.getAndIncrement();
	}

	@Override
	public void salvar(PrintWriter pw) {
		if (ehParametro()) {
			print(pw, ParametroContexto.LOAD_PARAM, id);
		} else {
			print(pw, ConstanteContexto.LOAD_CONST, id);
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