package br.com.persist.plugins.objeto.vinculo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.objeto.Instrucao;
import br.com.persist.plugins.objeto.Objeto;

public class ParaTabela {
	private final List<Instrucao> instrucoes;
	final Color corFonte;
	final String tabela;
	final String icone;

	public ParaTabela(String tabela, String icone, Color corFonte) {
		if (Util.estaVazio(tabela)) {
			throw new IllegalStateException("Tabela vazia.");
		}
		instrucoes = new ArrayList<>();
		this.corFonte = corFonte;
		this.tabela = tabela;
		this.icone = icone;
	}

	public List<Instrucao> getInstrucoes() {
		return instrucoes;
	}

	public void add(Instrucao i) {
		if (i != null) {
			instrucoes.add(i);
		}
	}

	public void config(Objeto objeto) {
		if (corFonte != null) {
			objeto.setCorFonte(corFonte);
		}
		if (icone != null) {
			objeto.setIcone(icone);
		}
	}

	public String getTabela() {
		return tabela;
	}

	public String getIcone() {
		return icone;
	}

	@Override
	public String toString() {
		return tabela;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof ParaTabela) {
			ParaTabela outro = (ParaTabela) obj;
			return tabela.equals(outro.tabela);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return tabela.hashCode();
	}
}