package br.com.persist.plugins.objeto.internal;

import java.awt.Graphics;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.ObjetoException;

public class InternalConfig {
	private final String conexao;
	private final String tabela;
	private final String grupo;
	private String complemento;
	private Graphics graphics;

	public InternalConfig(String conexao, String grupo, String tabela) throws ObjetoException {
		this.grupo = grupo == null ? "" : grupo;
		this.conexao = conexao;
		if (Util.isEmpty(tabela)) {
			throw new ObjetoException("Tabela vazia.");
		}
		this.tabela = tabela;
	}

	public InternalConfig(String conexao) {
		this.conexao = conexao;
		this.tabela = "";
		this.grupo = "";
	}

	public boolean igual(Objeto objeto) {
		return objeto != null && grupo.equalsIgnoreCase(objeto.getGrupo())
				&& tabela.equalsIgnoreCase(objeto.getTabela());
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