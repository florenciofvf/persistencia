package br.com.persist.plugins.objeto.auto;

import br.com.persist.assistencia.Util;

public abstract class AbstratoTabela {
	private final String apelido;
	private final String campo;
	private final String nome;

	public AbstratoTabela(String apelido, String nome, String campo) {
		this.apelido = apelido == null ? "" : apelido.trim();
		this.campo = campo == null ? "" : campo.trim();
		this.nome = nome.trim();
	}

	public boolean igual(AbstratoTabela tabela) {
		return tabela != null && apelido.equals(tabela.apelido) && nome.equals(tabela.nome)
				&& campo.equals(tabela.campo);
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
		StringBuilder sb = new StringBuilder(apelido);
		if (!Util.estaVazio(apelido)) {
			sb.append(".");
		}
		sb.append(nome);
		if (!Util.estaVazio(campo)) {
			sb.append(".");
			sb.append(campo);
		}
		return sb.toString();
	}
}