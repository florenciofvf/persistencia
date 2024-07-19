package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;

public class ParametroContexto extends Container {
	public static final String LOAD_PARAM = "load_param";
	private final String nome;

	public ParametroContexto(Token token) {
		this.nome = token.getString();
		this.token = token;
	}

	public String getNome() {
		return nome;
	}

	@Override
	public void salvar(PrintWriter pw) {
		pw.println(InstrucaoConstantes.PREFIXO_PARAMETRO + " " + nome);
	}

	@Override
	public String toString() {
		return nome;
	}
}