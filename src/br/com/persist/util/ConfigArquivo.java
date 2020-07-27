package br.com.persist.util;

public class ConfigArquivo {
	private String complemento;
	private String conexao;
	private String apelido;
	private String tabela;

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getConexao() {
		return conexao;
	}

	public void setConexao(String conexao) {
		this.conexao = conexao;
	}

	public String getApelido() {
		return apelido;
	}

	public void setApelido(String apelido) {
		this.apelido = apelido;
	}

	public String getTabela() {
		return tabela;
	}

	public void setTabela(String tabela) {
		this.tabela = tabela;
	}
}