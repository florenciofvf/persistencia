package br.com.persist.plugins.objeto.vinculo;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.objeto.Objeto;

public class Referencia {
	private final List<Coletor> coletores;
	private boolean vazioInvisivel;
	private final String tabela;
	private final String grupo;
	private final String campo;
	private boolean processado;
	Pesquisa pesquisa;

	public Referencia(String grupo, String tabela, String campo) {
		this.grupo = grupo == null ? "" : grupo.trim();
		this.campo = campo == null ? "" : campo.trim();
		coletores = new ArrayList<>();
		if (Util.estaVazio(tabela)) {
			throw new IllegalStateException("Tabela vazia.");
		}
		this.tabela = tabela;
	}

	public boolean refIgual(Referencia ref) {
		return ref != null && grupo.equalsIgnoreCase(ref.grupo) && tabela.equalsIgnoreCase(ref.tabela)
				&& campo.equalsIgnoreCase(ref.campo);
	}

	public boolean refIgual(Objeto objeto) {
		return objeto != null && grupo.equalsIgnoreCase(objeto.getGrupo())
				&& tabela.equalsIgnoreCase(objeto.getTabela2());
	}

	public boolean tabIgual(Referencia ref) {
		return ref != null && grupo.equalsIgnoreCase(ref.grupo) && tabela.equalsIgnoreCase(ref.tabela);
	}

	public Referencia clonar() {
		return new Referencia(grupo, tabela, campo);
	}

	public void inicializarColetores(List<String> numeros) {
		coletores.clear();
		for (String numero : numeros) {
			coletores.add(new Coletor(numero));
		}
	}

	public Coletor getColetor(String numero) {
		for (Coletor c : coletores) {
			if (c.getChave().equals(numero)) {
				return c;
			}
		}
		return null;
	}

	public void atualizarColetores(String numero) {
		for (Coletor c : coletores) {
			if (c.getChave().equals(numero)) {
				c.incrementarTotal();
			}
		}
	}

	public void setProcessado(boolean processado) {
		this.processado = processado;
	}

	public boolean isProcessado() {
		return processado;
	}

	public Pesquisa getPesquisa() {
		return pesquisa;
	}

	public String getGrupo() {
		return grupo;
	}

	public String getTabela() {
		return tabela;
	}

	public String getCampo() {
		return campo;
	}

	public boolean isVazioInvisivel() {
		return vazioInvisivel;
	}

	public void setVazioInvisivel(boolean vazioInvisivel) {
		this.vazioInvisivel = vazioInvisivel;
	}

	@Override
	public String toString() {
		return "grupo=" + grupo + ", tabela=" + tabela + ", campo=" + campo;
	}
}