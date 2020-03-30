package br.com.persist.busca_auto;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.util.Constantes;

public class GrupoBuscaAuto {
	private final List<TabelaBuscaAuto> tabelas;
	private final String nomeGrupoCampo;
	private final String campo;

	public GrupoBuscaAuto(String nomeGrupoCampo) {
		int pos = nomeGrupoCampo.indexOf('.');
		this.campo = nomeGrupoCampo.substring(pos + 1);
		this.nomeGrupoCampo = nomeGrupoCampo;
		tabelas = new ArrayList<>();
	}

	public void setArgumentos(List<String> argumentos) {
		for (TabelaBuscaAuto tabela : tabelas) {
			tabela.setArgumentos(argumentos);
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

	public String getDescricao() {
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
			TabelaBuscaAuto tabela = tabelas.get(i);
			sb.append(Constantes.TAB + tabela.getDescricao());

			if (i + 1 < tabelas.size()) {
				sb.append(",");
			}

			sb.append(Constantes.QL);
		}

		return sb.toString();
	}
}