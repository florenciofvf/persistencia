package br.com.persist.plugins.expressao.constante;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblioteca.Biblioteca;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class ConstanteDefineInstrucao extends Instrucao {
	public ConstanteDefineInstrucao() {
		super(ConstanteContexto.DEF_CONST);
	}

	@Override
	public Instrucao clonar() {
		return new ConstanteDefineInstrucao();
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		Object valor = pilhaOperando.pop();
		Constante constante = new Constante(parametros);
		Biblioteca biblioteca = funcao.getBiblioteca();
		biblioteca.addConstante(constante);
		constante.setValor(valor);
	}
}