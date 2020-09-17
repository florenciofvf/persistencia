package br.com.persist.plugins.conexao;

public class ConexaoInfo {
	private final String conexaoAtual;
	private final String conexaoFile;
	private final String nomeAba;

	public ConexaoInfo(String conexaoAtual, String conexaoFile, String nomeAba) {
		this.conexaoAtual = conexaoAtual;
		this.conexaoFile = conexaoFile;
		this.nomeAba = nomeAba;
	}

	public String getConexaoAtual() {
		return conexaoAtual;
	}

	public String getConexaoFile() {
		return conexaoFile;
	}

	public String getNomeAba() {
		return nomeAba;
	}
}