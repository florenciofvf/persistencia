package br.com.persist.plugins.objeto.internal;

import java.awt.Graphics;

public class InternalConfig {
	private final String conexao;
	private final String tabela;
	private final String grupo;
	private String complemento;
	private Graphics graphics;

	public InternalConfig(String conexao, String grupo, String tabela) {
		this.grupo = grupo == null ? "" : grupo;
		this.conexao = conexao;
		this.tabela = tabela;
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

	public String getGrupo() {
		return grupo;
	}

	public String getTabela() {
		return tabela;
	}

	public Graphics getGraphics() {
		return graphics;
	}

	public void setGraphics(Graphics graphics) {
		this.graphics = graphics;
	}
}