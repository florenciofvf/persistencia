package br.com.persist.plugins.expressao.constante;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.PilhaFuncao;
import br.com.persist.plugins.expressao.processador.PilhaOperando;

public class ConstanteLoadInstrucao extends Instrucao {
	private String nomeBiblio;
	private String nomeConstante;

	public ConstanteLoadInstrucao() {
		super(ConstanteContexto.LOAD_CONST);
	}

	@Override
	public Instrucao clonar() {
		return new ConstanteLoadInstrucao();
	}

	@Override
	public void setParametros(String string) {
		String[] array = string.split("\\.");
		if (array.length == 2) {
			nomeBiblio = array[0];
			nomeConstante = array[1];
		} else {
			nomeConstante = array[0];
		}
	}

	@Override
	public void processar(Funcao funcao, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws ExpressaoException {
		/*Biblioteca biblio;
		if (nomeBiblio != null) {
			biblio = cacheBiblioteca.getBiblioteca(nomeBiblio, biblioteca);
		} else {
			biblio = funcao.getBiblioteca();
		}
		Constante constante = biblio.getConstante(nomeConstante);
		pilhaOperando.push(constante.getValor());*/
	}
}