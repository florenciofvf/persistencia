package br.com.persist.plugins.objeto.auto;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Constantes;

public class GrupoLinkAuto {
	private final List<TabelaLinkAuto> tabelas;
	private final String campo;

	public GrupoLinkAuto(String campo) {
		tabelas = new ArrayList<>();
		this.campo = campo;
		TabelaBuscaAuto.checarCampo(campo);
	}

	public static GrupoLinkAuto criar(String campo) {
		return new GrupoLinkAuto(campo);
	}

	public void add(TabelaLinkAuto tabela) {
		for (TabelaLinkAuto obj : tabelas) {
			if (obj.igual(tabela)) {
				return;
			}
		}

		tabelas.add(tabela);
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