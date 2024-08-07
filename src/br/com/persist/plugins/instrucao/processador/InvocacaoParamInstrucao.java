package br.com.persist.plugins.instrucao.processador;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.InvocacaoContexto;

public class InvocacaoParamInstrucao extends Instrucao {
	public InvocacaoParamInstrucao() {
		super(InvocacaoContexto.INVOKE_PARAM);
	}

	@Override
	public Instrucao clonar() {
		return new InvocacaoParamInstrucao();
	}

	@Override
	public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
			PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
		Funcao funcaoParam = (Funcao) funcao.getValorParametro(parametros);
		InvocacaoInstrucao.setParametros(funcaoParam, pilhaOperando);
		pilhaFuncao.push(funcaoParam);
	}
}