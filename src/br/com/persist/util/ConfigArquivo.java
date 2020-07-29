package br.com.persist.util;

import java.awt.Graphics;

public class ConfigArquivo {
	private final boolean checarApelido;
	private String complemento;
	private Graphics graphics;
	private String conexao;
	private String apelido;
	private String tabela;

	public ConfigArquivo(boolean checarApelido) {
		this.checarApelido = checarApelido;
	}

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

	public Graphics getGraphics() {
		return graphics;
	}

	public void setGraphics(Graphics graphics) {
		this.graphics = graphics;
	}

	public boolean isChecarApelido() {
		return checarApelido;
	}
}