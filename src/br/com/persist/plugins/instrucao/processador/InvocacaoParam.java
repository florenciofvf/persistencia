package br.com.persist.plugins.instrucao.processador;

import br.com.persist.plugins.instrucao.InstrucaoException;

public abstract class InvocacaoParam extends Instrucao {
	private final boolean exp;

	protected InvocacaoParam(String nome, boolean exp) {
		super(nome);
		this.exp = exp;
	}

	@Override
	public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
			PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
		Funcao funcaoParam = (Funcao) funcao.getValorParametro(parametros);
		Invocacao.validar(funcaoParam, exp);
		Invocacao.setParametros(funcaoParam, pilhaOperando);
		pilhaFuncao.push(funcaoParam);
	}
}