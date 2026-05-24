package br.com.persist.plugins.expressao.local;

import java.util.Arrays;
import java.util.Map;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.constante.Constante;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.Invoke;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class LocalInvokeMapaInstrucao extends Instrucao implements Invoke {
	private String[] nomeFuncoes;
	private String nomeConstante;
	private String nomeMetodo;

	public LocalInvokeMapaInstrucao(int indice, String parametros) throws ExpressaoException {
		super(indice, LocalContexto.INVOKE_LOCAL_MAPA);
		int pos = parametros.indexOf(' ');
		String[] hierarquia = parametros.substring(0, pos).split(CIFRAO);
		nomeFuncoes = Arrays.copyOf(hierarquia, hierarquia.length - 1);
		nomeConstante = hierarquia[hierarquia.length - 1];
		nomeMetodo = parametros.substring(pos + 1);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		Funcao funcaoAlvo = getFuncaoAlvo(funcao, nomeFuncoes);
		Constante constante = funcaoAlvo.getConstante(nomeConstante);
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