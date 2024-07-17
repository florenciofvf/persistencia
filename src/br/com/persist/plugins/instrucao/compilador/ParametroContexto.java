package br.com.persist.plugins.instrucao.compilador;

public class ParametroContexto extends Container {
	public static final String LOAD_PAR = "load_param";
	private final String nome;

	public ParametroContexto(Token token) {
		this.nome = token.getString();
		this.token = token;
	}

	public String getNome() {
		return nome;
	}

	@Override
	public String toString() {
		return nome;
	}
}