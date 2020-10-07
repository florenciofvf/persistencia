package br.com.persist.plugins.objeto.internal;

import java.awt.Graphics;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;

public class InternalConfig {
	private String complemento;
	private Graphics graphics;
	private String conexao;
	private String tabela;
	private String grupo;

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

	public String getGrupo() {
		if (Util.estaVazio(grupo)) {
			grupo = Constantes.VAZIO;
		}
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
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
}