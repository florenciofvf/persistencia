package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class InvocacaoContexto extends Container {
	public static final String INVOKE_PARAM = "invoke_param";
	public static final String INVOKE = "invoke";

	public InvocacaoContexto(Token token) {
		adicionar(new ArgumentoContexto(null));
		contexto = Contextos.ABRE_PARENTESES;
		this.token = token;
	}

	public ArgumentoContexto getArgumento() {
		return (ArgumentoContexto) get(0);
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		compilador.setContexto(getArgumento());
		contexto = Contextos.PONTO_VIRGULA;
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		compilador.setContexto(getPai());
	}

	@Override
	public void indexar(AtomicInteger atomic) {
		super.indexar(atomic);
		indice = atomic.getAndIncrement();
		indexarNegativo(atomic);
	}

	@Override
	public void salvar(PrintWriter pw) {
		super.salvar(pw);
		if (ehInvokeParam()) {
			print(pw, INVOKE_PARAM, token.string);
		} else {
			print(pw, INVOKE, token.string);
		}
		salvarNegativo(pw);
	}

	private boolean ehInvokeParam() {
		FuncaoContexto funcao = getFuncao();
		if (funcao == null) {
			throw new IllegalStateException();
		}
		ParametrosContexto parametros = funcao.getParametros();
		return parametros.contem(token.string);
	}

	@Override
	public String toString() {
		return "invocacao >>> " + getArgumento().toString();
	}
}