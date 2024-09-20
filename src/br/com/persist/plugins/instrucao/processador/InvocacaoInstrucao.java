package br.com.persist.plugins.instrucao.processador;

import br.com.persist.plugins.instrucao.compilador.InvocacaoContexto;

public class InvocacaoInstrucao extends Invocacao {
	public InvocacaoInstrucao() {
		super(InvocacaoContexto.INVOKE);
	}

	@Override
	public Instrucao clonar() {
		return new InvocacaoInstrucao();
	}
}