package br.com.persist.plugins.objeto.vinculo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.persist.assistencia.Util;
import br.com.persist.plugins.objeto.Objeto;

public class Pesquisa {
	private final String nome;
	private final Referencia referencia;
	private final List<Referencia> referencias;
	private final List<Referencia> referenciasLink;
	private final List<Referencia> referenciasApos;

	public Pesquisa(String nome, Referencia ref) {
		Objects.requireNonNull(ref);
		if (Util.estaVazio(nome)) {
			throw new IllegalStateException("Nome da pesquisa vazia.");
		}
		referenciasApos = new ArrayList<>();
		referenciasLink = new ArrayList<>();
		referencias = new ArrayList<>();
		this.referencia = ref;
		this.nome = nome;
	}

	public void processar(Objeto objeto) {
		if (referencia.refIgual(objeto)) {
			objeto.getPesquisas().add(this);
			return;
		}
		for (Referencia ref : referencias) {
			if (ref.refIgual(objeto)) {
				objeto.setReferencia(ref);
			}
		}
	}

	public boolean igual(Pesquisa pesquisa) {
		return pesquisa != null && referencia.refIgual(pesquisa.referencia);
	}

	public String getNome() {
		return nome;
	}

	public Referencia getReferencia() {
		return referencia;
	}

	public void inicializarColetores(List<String> numeros) {
		for (Referencia ref : referencias) {
			ref.inicializarColetores(numeros);
		}
	}

	public void setProcessado(boolean b) {
		for (Referencia ref : referencias) {
			ref.setProcessado(b);
		}
	}

	public boolean isProcessado() {
		for (Referencia ref : referencias) {
			if (ref.isProcessado()) {
				return true;
			}
		}
		return false;
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

	public List<Referencia> getReferenciasApos() {
		return referenciasApos;
	}

	public List<Referencia> getReferenciasLink() {
		return referenciasLink;
	}

	public void add(Referencia ref) {
		if (ref != null) {
			if (!contem(ref)) {
				referencias.add(ref);
				ref.pesquisa = this;
			}
			ref = ref.clonar();
			if (ref != null && !contemLink(ref)) {
				referenciasLink.add(ref);
				ref.pesquisa = this;
			}
		}
	}

	public void addLink(Referencia ref) {
		if (ref != null && !contemLink(ref)) {
			referenciasLink.add(ref);
			ref.pesquisa = this;
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
		return "nome=" + nome + ", ref=" + referencia;
	}
}