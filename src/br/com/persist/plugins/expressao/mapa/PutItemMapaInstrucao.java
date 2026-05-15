package br.com.persist.plugins.expressao.mapa;

import java.util.Map;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.InstrucaoUtil;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;
import br.com.persist.plugins.expressao.processador.Push;

public class PutItemMapaInstrucao extends Instrucao implements Push {
	private final String chave;

	public PutItemMapaInstrucao(int indice, String parametros) throws ExpressaoException {
		super(indice, PutItemMapaContexto.PUT_ITEM_MAPA);
		chave = parametros;
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
		mapa.put(chave, item);
		pilhaOperando.push(mapa);
	}
}