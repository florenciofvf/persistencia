package br.com.persist.plugins.conexao;

public class ConexaoException extends Exception {
	private static final long serialVersionUID = 1L;
	private final transient Conexao conexao;

	public ConexaoException(Conexao conexao, Throwable cause) {
		super(cause);
		this.conexao = conexao;
	}

	public ConexaoException(Conexao conexao, String message) {
		super(message);
		this.conexao = conexao;
	}

	public Conexao getConexao() {
		return conexao;
	}
}