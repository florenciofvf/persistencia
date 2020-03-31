package br.com.persist.busca_auto_apos;

public class TabelaBuscaAutoApos {
	private final String apelidoTabela;
	private final String apelido;
	private final String nome;

	public TabelaBuscaAutoApos(String apelidoTabela) {
		this.apelidoTabela = apelidoTabela;
		String n = apelidoTabela;

		if (n.startsWith("(")) {
			int pos2 = n.indexOf(')');
			apelido = n.substring(1, pos2);
			nome = n.substring(pos2 + 1);
		} else {
			apelido = "";
			nome = n;
		}
	}

	public String getApelidoTabela() {
		return apelidoTabela;
	}

	public String getApelido() {
		return apelido;
	}

	public String getNome() {
		return nome;
	}

	@Override
	public String toString() {
		return nome;
	}
}