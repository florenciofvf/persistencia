package br.com.persist.plugins.expressao.constante;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.ExpressaoUtil;
import br.com.persist.plugins.expressao.biblioteca.Biblioteca;
import br.com.persist.plugins.expressao.processador.Def;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class ConstanteDefineInstrucao extends Instrucao implements Def {
	private final String nomeConstante;

	public ConstanteDefineInstrucao(int indice, String parametros) throws ExpressaoException {
		super(indice, ConstanteContexto.DEF_CONST);
		nomeConstante = parametros;
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		Object valor = pilhaOperando.pop();
		Constante constante = new Constante(nomeConstante);
		Biblioteca biblioteca = funcao.getBiblioteca();
		biblioteca.addConstante(constante);
		constante.setValor(valor);
		if (ExpressaoConstantes.DEBUG_INSTRUCAO) {
			String string = ExpressaoUtil
					.completar("[DEF-CONST-" + nomeConstante + "] ######### (def const) ######### " + constante);
			ExpressaoUtil.print(string, pilhaOperando);
		}
	}

	@Override
	public String toString() {
		return super.toString() + " " + nomeConstante;
	}
}