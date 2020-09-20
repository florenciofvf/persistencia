package br.com.persist.plugins.objeto.auto;

import java.util.ArrayList;
import java.util.List;

public class GrupoBuscaAuto extends AbstratoGrupo {
	private final GrupoBuscaAutoApos grupoBuscaAutoApos;
	private final List<TabelaBuscaAuto> tabelas;

	public GrupoBuscaAuto(String nome, String campo) {
		super(nome, campo);
		grupoBuscaAutoApos = new GrupoBuscaAutoApos(campo);
		tabelas = new ArrayList<>();
	}

	public void add(TabelaBuscaAuto tabela) {
		tabelas.add(tabela);
	}

	public GrupoBuscaAutoApos getGrupoBuscaAutoApos() {
		return grupoBuscaAutoApos;
	}

	public void inicializarColetores(List<String> numeros) {
		for (TabelaBuscaAuto tabela : tabelas) {
			tabela.inicializarColetores(numeros);
		}
	}

	public void setProcessado(boolean b) {
		for (TabelaBuscaAuto tabela : tabelas) {
			tabela.setProcessado(b);
		}
	}

	public boolean isProcessado() {
		for (TabelaBuscaAuto tabela : tabelas) {
			if (tabela.isProcessado()) {
				return true;
			}
		}
		return false;
	}

	public List<TabelaBuscaAuto> getTabelas() {
		return tabelas;
	}
}