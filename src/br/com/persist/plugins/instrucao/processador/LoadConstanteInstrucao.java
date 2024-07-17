package br.com.persist.plugins.instrucao.processador;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.ConstanteContexto;

public class LoadConstanteInstrucao extends Instrucao {
	private String nomeBiblio;
	private String nomeConst;

	public LoadConstanteInstrucao() {
		super(ConstanteContexto.LOAD_CONST);
	}

	@Override
	public Instrucao clonar() {
		return this;
	}

	@Override
	public void setParametros(String string) {
		String[] array = string.split("\\.");
		if (array.length == 2) {
			nomeBiblio = array[0];
			nomeConst = array[1];
		} else {
			nomeConst = array[0];
		}
	}

	@Override
	public void processar(CacheBiblioteca cacheBiblioteca, PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando)
			throws InstrucaoException {
		Biblioteca biblioteca;
		if (nomeBiblio != null) {
			biblioteca = cacheBiblioteca.getBiblioteca(nomeBiblio);
		} else {
			biblioteca = funcao.getBiblioteca();
		}
		Object valor = biblioteca.getValorConstante(nomeConst);
		pilhaOperando.push(valor);
	}
}