package br.com.persist.plugins.expressao.parametros;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.Load;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class ParametroLoadInstrucao extends Instrucao implements Load {
	private String[] nomeFuncoes;
	private String nomeParametro;

	public ParametroLoadInstrucao(int indice, String parametros) throws ExpressaoException {
		super(indice, ParametroContexto.LOAD_PARAM);
		int pos = parametros.indexOf(' ');
		nomeFuncoes = parametros.substring(0, pos).split(CIFRAO);
		nomeParametro = parametros.substring(pos + 1);
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		Funcao funcaoAlvo = getFuncaoAlvo(funcao, nomeFuncoes);
		Object valor = funcaoAlvo.getValorParametro(nomeParametro);
		if (valor instanceof Funcao) {
			Funcao funcaoValor = ((Funcao) valor);
			funcaoValor.setParent(funcao);
		}
		pilhaOperando.push(valor);
		log("[LOAD-PARAM-" + get(nomeFuncoes, nomeParametro) + "] ######### (funcao alvo) ######### " + funcaoAlvo,
				pilhaOperando);
	}

	@Override
	public String toString() {
		return super.toString() + " " + get(nomeFuncoes, nomeParametro);
	}
}