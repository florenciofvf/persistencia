package br.com.persist.plugins.objeto.auto;

import br.com.persist.assistencia.Util;

public class TabelaLinkAuto extends AbstratoTabela {
	public TabelaLinkAuto(String apelido, String nome, String campo) {
		super(apelido, nome, campo);
	}

	public String getApelidoTabelaCampo() {
		return getApelidoTabelaCampo(getApelido(), getNome(), getCampo());
	}

	public static String getApelidoTabelaCampo(String apelido, String nome, String campo) {
		if (Util.estaVazio(apelido)) {
			return nome + "." + campo;
		}
		return "(" + apelido + ")" + nome + "." + campo;
	}
}