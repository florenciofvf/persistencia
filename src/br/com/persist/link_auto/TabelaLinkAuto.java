package br.com.persist.link_auto;

import br.com.persist.busca_auto.TabelaBuscaAuto;

public class TabelaLinkAuto {
	private final String apelido;
	private final String campo;
	private final String nome;

	public TabelaLinkAuto(String apelidoTabelaCampo, String contextoDebug) {
		int pos = apelidoTabelaCampo.indexOf('.');
		TabelaBuscaAuto.checarPos(pos,
				"SEM CAMPO DEFINIDO NO LINK AUTO -> " + contextoDebug + " > " + apelidoTabelaCampo);
		String[] arrayApelidoTabela = TabelaBuscaAuto.separarApelidoTabela(apelidoTabelaCampo.substring(0, pos));
		campo = apelidoTabelaCampo.substring(pos + 1).trim();
		TabelaBuscaAuto.checarCampo(campo);
		apelido = arrayApelidoTabela[0];
		nome = arrayApelidoTabela[1];
	}

	public boolean igual(TabelaLinkAuto tabela) {
		return apelido.equals(tabela.apelido) && nome.equals(tabela.nome);
	}

	public String getApelidoTabelaCampo() {
		return TabelaBuscaAuto.getApelidoTabelaCampo(apelido, nome, campo);
	}

	public String getApelido() {
		return apelido;
	}

	public String getCampo() {
		return campo;
	}

	public String getNome() {
		return nome;
	}

	@Override
	public String toString() {
		return nome;
	}
}