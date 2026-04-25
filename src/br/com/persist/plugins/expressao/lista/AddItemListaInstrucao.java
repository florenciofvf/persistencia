package br.com.persist.plugins.expressao.lista;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblionativo.Lista;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.InstrucaoUtil;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class AddItemListaInstrucao extends Instrucao {
	public AddItemListaInstrucao(int indice) throws ExpressaoException {
		super(indice, AddItemListaContexto.ADD_ITEM_LISTA);
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		Object item = pilhaOperando.pop();
		Object objLista = pilhaOperando.pop();
		InstrucaoUtil.checarLista(objLista);
		InstrucaoUtil.checarOperando(item);
		Lista lista = (Lista) objLista;
		lista.add(item);
		pilhaOperando.push(lista);
	}
}