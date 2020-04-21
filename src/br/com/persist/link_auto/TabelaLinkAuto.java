package br.com.persist.link_auto;

import br.com.persist.util.Constantes;
import br.com.persist.util.Util;

public class TabelaLinkAuto {
	private final String apelidoTabelaCampo;
	private final String apelido;
	private final String campo;
	private final String nome;

	public TabelaLinkAuto(String apelidoTabelaCampo, String contextoDebug) {
		this.apelidoTabelaCampo = apelidoTabelaCampo;
		int pos = apelidoTabelaCampo.indexOf('.');
		Util.checarPos(pos, "SEM CAMPO DEFINIDO NO LINK AUTO -> " + contextoDebug + " > " + apelidoTabelaCampo);
		String n = apelidoTabelaCampo.substring(0, pos);

		if (n.startsWith("(")) {
			int pos2 = n.indexOf(')');
			apelido = n.substring(1, pos2);
			nome = n.substring(pos2 + 1);
		} else {
			apelido = Constantes.VAZIO;
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