package br.com.persist.plugins.instrucao.processador;

public class InstrucaoItem {
	final Instrucao instrucao;
	InstrucaoItem proximo;

	public InstrucaoItem(Instrucao instrucao) {
		this.instrucao = instrucao;
	}
}