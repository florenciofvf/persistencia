package br.com.persist.plugins.objeto.vinculo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.persist.assistencia.Util;

public class Grupo {
	private final String nome;
	private final Referencia referencia;
	private final List<Referencia> referencias;
	private final List<Referencia> referenciasLink;

	public Grupo(String nome, Referencia ref) {
		Objects.requireNonNull(ref);
		if (Util.estaVazio(nome)) {
			throw new IllegalStateException("Nome do grupo vazio.");
		}
		referenciasLink = new ArrayList<>();
		referencias = new ArrayList<>();
		this.referencia = ref;
		this.nome = nome;
	}

	public boolean igual(Grupo grupo) {
		return grupo != null && referencia.refIgual(grupo.referencia);
	}

	public String getNome() {
		return nome;
	}

	public Referencia getReferencia() {
		return referencia;
	}

	public List<Referencia> getClonarReferencias() {
		List<Referencia> lista = new ArrayList<>();
		for (Referencia ref : referencias) {
			lista.add(ref.clonar());
		}
		return lista;
	}

	public List<Referencia> getReferencias() {
		return referencias;
	}

	public void add(Referencia ref) {
		if (ref != null && !contem(ref)) {
			referencias.add(ref);
			ref.grupo = this;
		}
	}

	public void addLink(Referencia ref) {
		if (ref != null && !contemLink(ref)) {
			referenciasLink.add(ref);
			ref.grupo = this;
		}
	}

	public void add(List<Referencia> referencias) {
		for (Referencia ref : referencias) {
			add(ref);
		}
	}

	public void addLink(List<Referencia> referencias) {
		for (Referencia ref : referencias) {
			addLink(ref);
		}
	}

	public boolean contem(Referencia ref) {
		for (Referencia r : referencias) {
			if (r.refIgual(ref)) {
				return true;
			}
		}
		return false;
	}

	public boolean contemLink(Referencia ref) {
		for (Referencia r : referenciasLink) {
			if (r.refIgual(ref)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "nome=" + nome + ", REF=" + referencia;
	}
}