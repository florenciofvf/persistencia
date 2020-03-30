package br.com.persist.link_auto;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.util.Constantes;

public class GrupoLinkAuto {
	private final List<TabelaLinkAuto> tabelas;
	private final String campo;

	public GrupoLinkAuto(String campo) {
		tabelas = new ArrayList<>();
		this.campo = campo;
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
			sb.append(Constantes.TAB + tabela.getDescricao());

			if (i + 1 < tabelas.size()) {
				sb.append(",");
			}

			sb.append(Constantes.QL);
		}

		return sb.toString();
	}
}