package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;

public class IFEqContexto extends Container {
	public static final String IF_EQ = "ifeq";
	int posicao;

	@Override
	public void salvar(PrintWriter pw) {
		pw.print(InstrucaoConstantes.PREFIXO_INSTRUCAO + " -");
		pw.print(InstrucaoConstantes.ESPACO + IF_EQ);
		pw.print(InstrucaoConstantes.ESPACO + posicao);
		pw.println();
	}
}