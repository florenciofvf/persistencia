package br.com.persist.plugins.instrucao.inst;

import br.com.persist.assistencia.Lista;
import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.pro.CacheBiblioteca;
import br.com.persist.plugins.instrucao.pro.Instrucao;
import br.com.persist.plugins.instrucao.pro.Metodo;
import br.com.persist.plugins.instrucao.pro.PilhaMetodo;
import br.com.persist.plugins.instrucao.pro.PilhaOperando;

public class LoadListaVazia extends Instrucao {
	protected static final Lista lista = new Lista();

	public LoadListaVazia(Metodo metodo) {
		super(metodo, InstrucaoConstantes.LOAD_LISTA_VAZIA);
	}

	@Override
	public Instrucao clonar(Metodo metodo) {
		return new LoadListaVazia(metodo);
	}

	@Override
	public void executar(PilhaMetodo pilhaMetodo, PilhaOperando pilhaOperando, CacheBiblioteca cacheBiblioteca)
			throws InstrucaoException {
		pilhaOperando.push(lista);
	}
}