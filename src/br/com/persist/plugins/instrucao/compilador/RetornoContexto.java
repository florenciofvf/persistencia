package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class RetornoContexto extends Container {
	public static final AbreParenteseOuFinalizar PARENTESE_OU_FINALIZAR = new AbreParenteseOuFinalizar();
	public static final String RETURN = "return";

	public RetornoContexto() {
		adicionar(new ExpressaoContexto());
		contexto = PARENTESE_OU_FINALIZAR;
	}

	public ExpressaoContexto getExpressao() {
		return (ExpressaoContexto) get(0);
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		compilador.setContexto(getExpressao());
		contexto = Contextos.PONTO_VIRGULA;
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		FuncaoContexto funcao = getFuncao();
		if (funcao == null) {
			compilador.invalidar(token);
		} else if (funcao.isRetornoVoid() && getExpressao().getSize() > 0) {
			compilador.invalidar(token, funcao.getNome() + " --> deve retornar void");
		} else if (!funcao.isRetornoVoid() && getExpressao().isEmpty()) {
			compilador.invalidar(token, funcao.getNome() + " --> deve retornar um valor");
		}
		compilador.setContexto(getPai());
	}

	@Override
	public void indexar(Indexador indexador) {
		pontoDeslocamento = indexador.value();
		super.indexar(indexador);
		sequencia = indexador.get();
	}

	@Override
	public void salvar(PrintWriter pw) throws InstrucaoException {
		super.salvar(pw);
		print(pw, RETURN);
	}

	@Override
	public String toString() {
		return "return >>> " + getExpressao().toString();
	}
}

class AbreParenteseOuFinalizar extends AbstratoContexto {
	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		if (!"(".equals(token.getString())) {
			compilador.invalidar(token);
		}
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		if (!";".equals(token.getString())) {
			compilador.invalidar(token);
		}
	}
}