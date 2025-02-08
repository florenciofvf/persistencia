package br.com.persist.plugins.instrucao.processador;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.FuncaoContexto;

public class LoadFuncaoLambInstrucao extends Instrucao {
	private String nomeFuncao;

	public LoadFuncaoLambInstrucao() {
		super(FuncaoContexto.LOAD_FUNCTION_LAMB);
	}

	@Override
	public Instrucao clonar() {
		return new LoadFuncaoLambInstrucao();
	}

	@Override
	public void setParametros(String string) {
		nomeFuncao = string;
	}

	@Override
	public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
			PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
		Funcao clone = biblioteca.getFuncao(nomeFuncao).clonar();
		clone.setParent(funcao);
		pilhaOperando.push(clone);
	}
}