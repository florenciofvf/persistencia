package br.com.persist.plugins.expressao.parametros;

import java.util.Arrays;
import java.util.Map;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.Invoke;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class ParametroInvokeMapaInstrucao extends Instrucao implements Invoke {
	private String[] nomeFuncoes;
	private String nomParametro;
	private String nomeMetodo;

	public ParametroInvokeMapaInstrucao(int indice, String parametros) throws ExpressaoException {
		super(indice, ParametroContexto.INVOKE_PARAM_MAPA);
		int pos = parametros.indexOf(' ');
		String[] hierarquia = parametros.substring(0, pos).split(CIFRAO);
		nomeFuncoes = Arrays.copyOf(hierarquia, hierarquia.length - 1);
		nomParametro = hierarquia[hierarquia.length - 1];
		nomeMetodo = parametros.substring(pos + 1);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		Funcao funcaoAlvo = getFuncaoAlvo(funcao, nomeFuncoes);
		Object valor = funcaoAlvo.getValorParametro(nomParametro);
		Map<Object, Object> mapa = (Map<Object, Object>) valor;
		Funcao funcaoValor = (Funcao) mapa.get(nomeMetodo);
		Funcao clone = funcaoValor.clonar();
		pilhaOperando.setArgumentos(clone);
		pilhaFuncao.push(clone);
		log("[" + ParametroContexto.INVOKE_PARAM_MAPA + get(nomParametro, nomeMetodo) + "] [funcao_valor->" + clone
				+ "]", pilhaOperando);
	}

	@Override
	public String toString() {
		return super.toString() + get(nomParametro, nomeMetodo);
	}
}