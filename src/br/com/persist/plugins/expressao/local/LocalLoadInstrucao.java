package br.com.persist.plugins.expressao.local;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.constante.Constante;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.Load;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class LocalLoadInstrucao extends Instrucao implements Load {
	private String[] nomeFuncoes;
	private String nomeLocal;

	public LocalLoadInstrucao(int indice, String parametros) throws ExpressaoException {
		super(indice, LocalContexto.LOAD_LOCAL);
		int pos = parametros.indexOf(' ');
		nomeFuncoes = parametros.substring(0, pos).split(CIFRAO);
		nomeLocal = parametros.substring(pos + 1);
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		Funcao funcaoAlvo = getFuncaoAlvo(funcao, nomeFuncoes);
		Constante constante = funcaoAlvo.getConstante(nomeLocal);
		pilhaOperando.push(constante.getValor());
		log("[" + LocalContexto.LOAD_LOCAL + get(nomeFuncoes, nomeLocal) + "] ######### (local load) ######### "
				+ constante, pilhaOperando);
	}

	@Override
	public String toString() {
		return super.toString() + get(nomeFuncoes, nomeLocal);
	}
}