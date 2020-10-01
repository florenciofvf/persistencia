package br.com.persist.plugins.objeto.auto;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;

public class GrupoLinkAuto {
	private final List<TabelaLinkAuto> tabelas;
	private final String campo;

	public GrupoLinkAuto(String campo) {
		tabelas = new ArrayList<>();
		this.campo = campo;
		checarCampo(campo);
	}

	public static void checarCampo(String campo) {
		if (Util.estaVazio(campo)) {
			throw new IllegalStateException("Nome do campo vazio.");
		}
	}

	public static GrupoLinkAuto criar(String campo) {
		return new GrupoLinkAuto(campo);
	}

	public void add(TabelaLinkAuto tabela) {
		if (tabela != null && !contem(tabela)) {
			tabelas.add(tabela);
		}
	}

	private boolean contem(TabelaLinkAuto tabela) {
		for (TabelaLinkAuto tab : tabelas) {
			if (tab.igual(tabela)) {
				return true;
			}
		}
		return false;
	}

	public void add(List<TabelaLinkAuto> lista) {
		for (TabelaLinkAuto tabela : lista) {
			add(tabela);
		}
	}

	public List<TabelaLinkAuto> getTabelas() {
		return tabelas;
	}

	public String getCampo() {
		return campo;
	}

	@Override
	public String toString() {
		return campo;
	}

	public String getDetalhe() {
		StringBuilder sb = new StringBuilder(campo + "=" + Constantes.QL);
		for (int i = 0; i < tabelas.size(); i++) {
			TabelaLinkAuto tabela = tabelas.get(i);
			sb.append(Constantes.TAB + tabela.getApelidoTabelaCampo());
			if (i + 1 < tabelas.size()) {
				sb.append(",");
			}
			sb.append(Constantes.QL);
		}
		return sb.toString();
	}
}