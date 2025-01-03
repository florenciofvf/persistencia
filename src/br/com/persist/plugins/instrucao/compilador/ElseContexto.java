package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;

public class ElseContexto extends Container {
	public ElseContexto() {
		contexto = Contextos.ABRE_CHAVES;
		adicionar(new CorpoContexto());
	}

	public CorpoContexto getCorpo() {
		return (CorpoContexto) get(0);
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		compilador.setContexto(getCorpo());
		getCorpo().setFinalizadorPai(true);
		contexto = Contextos.FECHA_CHAVES;
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
		return InstrucaoConstantes.ELSE;
	}
}