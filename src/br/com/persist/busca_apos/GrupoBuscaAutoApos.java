package br.com.persist.busca_apos;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.busca_auto.GrupoBuscaAuto;
import br.com.persist.util.Constantes;

public class GrupoBuscaAutoApos {
	private final List<TabelaBuscaAutoApos> tabelas;
	private final String nomeGrupoCampo;
	private final String campo;

	public GrupoBuscaAutoApos(String nomeGrupoCampo) {
		int pos = nomeGrupoCampo.indexOf('.');
		this.campo = nomeGrupoCampo.substring(pos + 1);
		this.nomeGrupoCampo = nomeGrupoCampo;
		tabelas = new ArrayList<>();
	}

	public boolean igual(GrupoBuscaAuto grupo) {
		return grupo != null && nomeGrupoCampo.equals(grupo.getNomeGrupoCampo());
	}

	public List<TabelaBuscaAutoApos> getTabelas() {
		return tabelas;
	}

	public String getNomeGrupoCampo() {
		return nomeGrupoCampo;
	}

	public String getCampo() {
		return campo;
	}

	@Override
	public String toString() {
		return nomeGrupoCampo;
	}

	public String getDetalhe() {
		StringBuilder sb = new StringBuilder(nomeGrupoCampo + "=" + Constantes.QL);

		for (int i = 0; i < tabelas.size(); i++) {
			TabelaBuscaAutoApos tabela = tabelas.get(i);
			sb.append(Constantes.TAB + tabela.getApelidoTabela());

			if (i + 1 < tabelas.size()) {
				sb.append(",");
			}

			sb.append(Constantes.QL);
		}

		return sb.toString();
	}
}