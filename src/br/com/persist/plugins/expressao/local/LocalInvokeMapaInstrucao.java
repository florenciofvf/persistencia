package br.com.persist.plugins.expressao.local;

import java.util.Map;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.constante.Constante;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.Invoke;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class LocalInvokeMapaInstrucao extends Instrucao implements Invoke {
	private String nomeConstante;
	private String nomeMetodo;

	public LocalInvokeMapaInstrucao(int indice, String parametros) throws ExpressaoException {
		super(indice, LocalContexto.INVOKE_LOCAL_MAPA);
		String[] array = parametros.split(ExpressaoConstantes.ESPACO);
		nomeConstante = array[0];
		nomeMetodo = array[1];
	}

	@Override
	@SuppressWarnings("unchecked")
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		Constante constante = funcao.getConstante(nomeConstante);
		Map<Object, Object> mapa = (Map<Object, Object>) constante.getValor();
		Funcao funcaoValor = (Funcao) mapa.get(nomeMetodo);
		Funcao clone = funcaoValor.clonar();
		pilhaOperando.setArgumentos(clone);
		pilhaFuncao.push(clone);
		log("[" + LocalContexto.INVOKE_LOCAL_MAPA + get(nomeConstante, nomeMetodo) + "] [funcao_valor->" + clone + "]",
				pilhaOperando);
	}

	@Override
	public String toString() {
		return super.toString() + get(nomeConstante, nomeMetodo);
	}
}