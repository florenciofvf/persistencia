package br.com.persist.plugins.instrucao.processador;

import br.com.persist.plugins.instrucao.compilador.InvocacaoContexto;

public class InvocacaoExpInstrucao extends Invocacao {
	public InvocacaoExpInstrucao() {
		super(InvocacaoContexto.INVOKE_EXP);
	}

	@Override
	public Instrucao clonar() {
		return new InvocacaoExpInstrucao();
	}
}