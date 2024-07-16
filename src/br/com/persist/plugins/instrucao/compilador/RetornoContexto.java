package br.com.persist.plugins.instrucao.compilador;

import java.util.concurrent.atomic.AtomicInteger;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class RetornoContexto extends Container {
	public RetornoContexto() {
		contexto = Contextos.ABRE_PARENTESES;
		adicionar(new ExpressaoContexto());
	}

	public ExpressaoContexto getExpressao() {
		return (ExpressaoContexto) get(0);
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		contexto = Contextos.PONTO_VIRGULA;
		compilador.setContexto(getExpressao());
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
	}

	@Override
	public String toString() {
		return "return >>> " + getExpressao().toString();
	}
}