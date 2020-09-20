package br.com.persist.plugins.objeto.auto;

import java.util.ArrayList;
import java.util.List;

public class GrupoBuscaAutoApos {
	private final List<TabelaBuscaAutoApos> tabelas;
	private final String campo;

	public GrupoBuscaAutoApos(String campo) {
		tabelas = new ArrayList<>();
		this.campo = campo;
	}

	public List<TabelaBuscaAutoApos> getTabelas() {
		return tabelas;
	}

	public void add(TabelaBuscaAutoApos tabela) {
		tabelas.add(tabela);
	}

	public String getCampo() {
		return campo;
	}

	@Override
	public String toString() {
		return campo;
	}
}