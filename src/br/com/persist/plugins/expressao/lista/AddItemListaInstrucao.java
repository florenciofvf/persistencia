package br.com.persist.plugins.expressao.lista;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblionativo.Lista;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.InstrucaoUtil;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class AddItemListaInstrucao extends Instrucao {
	public AddItemListaInstrucao() {
		super(AddItemListaContexto.ADD_ITEM_LISTA);
	}

	@Override
	public Instrucao novo() {
		return new AddItemListaInstrucao();
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		Object operandoD = pilhaOperando.pop();
		Object operandoE = pilhaOperando.pop();
		InstrucaoUtil.checarLista(operandoD);
		InstrucaoUtil.checarOperando(operandoE);
		Lista lista = (Lista) operandoD;
		lista.add(operandoE);
		pilhaOperando.push(lista);
	}
}