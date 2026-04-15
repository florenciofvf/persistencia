package br.com.persist.plugins.expressao.mapa;

import java.util.Map;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.InstrucaoUtil;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class PutItemMapaInstrucao extends Instrucao {
	public PutItemMapaInstrucao() {
		super(PutItemMapaContexto.PUT_ITEM_MAPA);
	}

	@Override
	public Instrucao novo() {
		return new PutItemMapaInstrucao();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		Object item = pilhaOperando.pop();
		Object objMapa = pilhaOperando.pop();
		InstrucaoUtil.checarMapa(objMapa);
		InstrucaoUtil.checarOperando(item);
		Map<Object, Object> mapa = (Map<Object, Object>) objMapa;
		mapa.put(parametros, item);
		pilhaOperando.push(mapa);
	}
}