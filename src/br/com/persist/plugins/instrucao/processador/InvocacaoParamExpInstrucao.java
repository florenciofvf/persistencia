package br.com.persist.plugins.instrucao.processador;

import br.com.persist.plugins.instrucao.compilador.InvocacaoContexto;

public class InvocacaoParamExpInstrucao extends InvocacaoParam {
	public InvocacaoParamExpInstrucao() {
		super(InvocacaoContexto.INVOKE_PARAM_EXP, true);
	}

	@Override
	public Instrucao clonar() {
		return new InvocacaoParamExpInstrucao();
	}
}