package br.com.persist.plugins.instrucao.processador;

import br.com.persist.plugins.instrucao.compilador.InvocacaoContexto;

public class InvocacaoParamInstrucao extends InvocacaoParam {
	public InvocacaoParamInstrucao() {
		super(InvocacaoContexto.INVOKE_PARAM, false);
	}

	@Override
	public Instrucao clonar() {
		return new InvocacaoParamInstrucao();
	}
}