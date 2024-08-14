package br.com.persist.plugins.instrucao.processador;

import br.com.persist.plugins.instrucao.InstrucaoException;

public abstract class InvocacaoParam extends Instrucao {
	private final boolean checarTipo;

	protected InvocacaoParam(String nome, boolean checarTipo) {
		super(nome);
		this.checarTipo = checarTipo;
	}

	@Override
	public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
			PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
		Funcao funcaoParam = (Funcao) funcao.getValorParametro(parametros);
		if (checarTipo && funcaoParam.isTipoVoid()) {
			throw new InstrucaoException("erro.funcao_sem_retorno", funcaoParam.getNome(),
					funcaoParam.getBiblioteca().getNome());
		}
		InvocacaoInstrucao.setParametros(funcaoParam, pilhaOperando);
		pilhaFuncao.push(funcaoParam);
	}
}