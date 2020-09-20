package br.com.persist.plugins.objeto.auto;

import br.com.persist.assistencia.Util;

public abstract class AbstratoGrupo {
	private final String campo;
	private final String nome;

	public AbstratoGrupo(String nome, String campo) {
		this.campo = campo == null ? "" : campo.trim();
		this.nome = nome.trim();
	}

	public String getCampo() {
		return campo;
	}

	public String getNome() {
		return nome;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(nome);
		if (!Util.estaVazio(campo)) {
			sb.append(".");
			sb.append(campo);
		}
		return sb.toString();
	}
}