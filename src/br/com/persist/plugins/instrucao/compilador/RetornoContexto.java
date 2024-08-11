package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class RetornoContexto extends Container {
	public static final String RETURN = "return";

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
	public void indexar(Indexador indexador) {
		super.indexar(indexador);
		sequencia = indexador.get();
	}

	@Override
	public void salvar(PrintWriter pw) {
		super.salvar(pw);
		print(pw, RETURN);
	}

	@Override
	public String toString() {
		return "return >>> " + getExpressao().toString();
	}
}