package br.com.persist.plugins.expressao.local;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.constante.Constante;
import br.com.persist.plugins.expressao.processador.Def;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class LocalDefineInstrucao extends Instrucao implements Def {
	private final String nomeLocal;

	public LocalDefineInstrucao(int indice, String parametros) throws ExpressaoException {
		super(indice, LocalContexto.DEF_LOCAL);
		nomeLocal = parametros;
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		Object valor = pilhaOperando.pop();
		Constante constante = new Constante(nomeLocal);
		funcao.addConstante(constante);
		constante.setValor(valor);
		log("[" + LocalContexto.DEF_LOCAL + " " + nomeLocal + "] [local_definida->" + constante + "]", pilhaOperando);
	}

	@Override
	public String toString() {
		return super.toString() + " " + nomeLocal;
	}
}