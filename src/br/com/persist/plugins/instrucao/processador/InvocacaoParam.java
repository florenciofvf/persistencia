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
		Object valor = funcao.getValorParametro(nomeParam);
		if (valor == null) {
			throw new InstrucaoException("erro.valor_param", nomeParam);
		}
		if (!(valor instanceof Funcao)) {
			throw new InstrucaoException("erro.valor_param_nao_funcao", nomeParam, funcao.getNome(), valor.toString(),
					biblioteca.getNome());
		}
		Funcao funcaoParam = (Funcao) valor;
		Invocacao.validar(funcaoParam, exp, totalParam);
		Invocacao.setParametros(funcaoParam, pilhaOperando);
		pilhaFuncao.push(funcaoParam);
	}
}