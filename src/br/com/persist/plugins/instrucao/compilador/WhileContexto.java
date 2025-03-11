package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;

public class WhileContexto extends Container {
	private boolean faseExpressao;

	public WhileContexto() {
		contexto = Contextos.ABRE_PARENTESES;
		adicionar(new ExpressaoContexto());
		adicionar(new IFEqContexto());
		adicionar(new CorpoContexto());
		faseExpressao = true;
	}

	public ExpressaoContexto getExpressao() {
		return (ExpressaoContexto) get(0);
	}

	public CorpoContexto getCorpo() {
		return (CorpoContexto) get(2);
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		if (faseExpressao) {
			compilador.setContexto(getExpressao());
			contexto = Contextos.ABRE_CHAVES;
			faseExpressao = false;
		} else {
			compilador.setContexto(getCorpo());
			getCorpo().setFinalizadorPai(true);
			contexto = Contextos.FECHA_CHAVES;
		}
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		compilador.setContexto(getPai());
	}

	@Override
	public void indexar(Indexador indexador) {
		pontoDeslocamento = indexador.value();
		super.indexar(indexador);
	}

	@Override
	public String toString() {
		return InstrucaoConstantes.WHILE + " >>> " + getExpressao().toString();
	}
}