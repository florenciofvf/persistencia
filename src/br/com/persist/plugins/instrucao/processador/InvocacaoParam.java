package br.com.persist.plugins.instrucao.processador;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;

public abstract class InvocacaoParam extends Instrucao {
	private final boolean exp;
	private String nomeParam;
	private int totalParam;

	protected InvocacaoParam(String nome, boolean exp) {
		super(nome);
		this.exp = exp;
	}

	@Override
	public void setParametros(String parametros) {
		String[] array = parametros.split(InstrucaoConstantes.ESPACO);
		totalParam = Integer.parseInt(array[1]);
		nomeParam = array[0];
	}

	@Override
	public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
			PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
		Funcao funcaoParam = (Funcao) funcao.getValorParametro(nomeParam);
		Invocacao.validar(funcaoParam, exp, totalParam);
		Invocacao.setParametros(funcaoParam, pilhaOperando);
		pilhaFuncao.push(funcaoParam);
	}
}