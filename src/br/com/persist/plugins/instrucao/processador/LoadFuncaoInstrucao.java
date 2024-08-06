package br.com.persist.plugins.instrucao.processador;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.FuncaoContexto;

public class LoadFuncaoInstrucao extends Instrucao {
	private String nomeBiblio;
	private String nomeFuncao;

	public LoadFuncaoInstrucao() {
		super(FuncaoContexto.LOAD_FUNCTION);
	}

	@Override
	public Instrucao clonar() {
		return new LoadFuncaoInstrucao();
	}

	@Override
	public void setParametros(String string) {
		String[] array = string.split("\\.");
		if (array.length == 2) {
			nomeBiblio = array[0];
			nomeFuncao = array[1];
		} else {
			nomeFuncao = array[0];
		}
	}

	@Override
	public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
			PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
		Biblioteca biblio;
		if (nomeBiblio != null) {
			biblio = cacheBiblioteca.getBiblioteca(nomeBiblio);
		} else {
			biblio = funcao.getBiblioteca();
		}
		Funcao clone = biblio.getFuncao(nomeFuncao).clonar();
		pilhaOperando.push(clone);
	}
}