package br.com.persist.plugins.expressao.processador;

public class InstrucaoItem {
	final Instrucao instrucao;
	InstrucaoItem proximo;

	public InstrucaoItem(Instrucao instrucao) {
		this.instrucao = instrucao;
	}

	@Override
	public String toString() {
		return instrucao.toString();
	}
}