package br.com.persist.plugins.objeto.auto;

import java.util.ArrayList;
import java.util.List;

public class GrupoBuscaAutoApos {
	private final List<TabelaBuscaAutoApos> tabelas;
	private final String campo;

	public GrupoBuscaAutoApos(String campo) {
		TabelaBuscaAuto.checarCampo(campo);
		tabelas = new ArrayList<>();
		this.campo = campo;
	}

	public List<TabelaBuscaAutoApos> getTabelas() {
		return tabelas;
	}

	public void add(TabelaBuscaAutoApos tabela) {
		for (TabelaBuscaAutoApos obj : tabelas) {
			if (obj.igual(tabela)) {
				return;
			}
		}

		tabelas.add(tabela);
	}

	public boolean contemLimparFormulariosRestantes() {
		for (TabelaBuscaAutoApos obj : tabelas) {
			if ("LIMPAR_FORMULARIOS_RESTANTES".equalsIgnoreCase(obj.getNome())) {
				return true;
			}
		}

		return false;
	}

	public String getCampo() {
		return campo;
	}

	@Override
	public String toString() {
		return campo;
	}
}