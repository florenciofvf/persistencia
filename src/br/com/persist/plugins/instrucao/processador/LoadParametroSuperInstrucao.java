package br.com.persist.plugins.instrucao.processador;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.ParametroContexto;

public class LoadParametroSuperInstrucao extends Instrucao {
	public LoadParametroSuperInstrucao() {
		super(ParametroContexto.LOAD_PARAM_SUPER);
	}

	@Override
	public Instrucao clonar() {
		return new LoadParametroSuperInstrucao();
	}

	@Override
	public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
			PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
		if (funcao.getParent() == null) {
			throw new InstrucaoException("erro.funcao_parent_inexistente", funcao.getNome(), biblioteca.getNome());
		}
		Object valor = funcao.getParent().getValorParametro(parametros);
		if (valor instanceof Funcao) {
			valor = ((Funcao) valor).clonar();
		}
		pilhaOperando.push(valor);
	}
}