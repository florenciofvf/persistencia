package br.com.persist.link_auto;

public class TabelaLinkAuto {
	private final String apelidoTabelaCampo;
	private final String apelido;
	private final String campo;
	private final String nome;

	public TabelaLinkAuto(String apelidoTabelaCampo) {
		this.apelidoTabelaCampo = apelidoTabelaCampo;
		int pos = apelidoTabelaCampo.indexOf('.');
		String n = apelidoTabelaCampo.substring(0, pos);

		if (n.startsWith("(")) {
			int pos2 = n.indexOf(')');
			apelido = n.substring(1, pos2);
			nome = n.substring(pos2 + 1);
		} else {
			apelido = "";
			nome = n;
		}

		campo = apelidoTabelaCampo.substring(pos + 1);
	}

	public String getApelidoTabelaCampo() {
		return apelidoTabelaCampo;
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