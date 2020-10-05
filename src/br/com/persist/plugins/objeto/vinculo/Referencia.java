package br.com.persist.plugins.objeto.vinculo;

import br.com.persist.assistencia.Util;

public class Referencia {
	private final String apelido;
	private final String tabela;
	private final String campo;
	Grupo grupo;

	public Referencia(String apelido, String tabela, String campo) {
		this.apelido = apelido == null ? "" : apelido.trim();
		this.campo = campo == null ? "" : campo.trim();
		if (Util.estaVazio(tabela)) {
			throw new IllegalStateException("Tabela vazia.");
		}
		this.tabela = tabela;
	}

	public boolean refIgual(Referencia ref) {
		return ref != null && apelido.equalsIgnoreCase(ref.apelido) && tabela.equalsIgnoreCase(ref.tabela)
				&& campo.equalsIgnoreCase(ref.campo);
	}

	public boolean tabIgual(Referencia ref) {
		return ref != null && apelido.equalsIgnoreCase(ref.apelido) && tabela.equalsIgnoreCase(ref.tabela);
	}

	public Referencia clonar() {
		return new Referencia(apelido, tabela, campo);
	}

	public Grupo getGrupo() {
		return grupo;
	}

	public String getApelido() {
		return apelido;
	}

	public String getTabela() {
		return tabela;
	}

	public String getCampo() {
		return campo;
	}

	@Override
	public String toString() {
		return "apelido=" + apelido + ", tabela=" + tabela + ", campo=" + campo;
	}
}