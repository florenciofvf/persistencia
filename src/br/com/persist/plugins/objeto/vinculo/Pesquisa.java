package br.com.persist.plugins.objeto.vinculo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.plugins.objeto.Objeto;

public class Pesquisa {
	private final List<Referencia> referenciasApos;
	private final List<Referencia> referencias;
	private final Referencia referencia;
	private final String nome;

	public Pesquisa(String nome, Referencia ref) {
		Objects.requireNonNull(ref);
		if (Util.estaVazio(nome)) {
			throw new IllegalStateException("Nome da pesquisa vazia.");
		}
		referenciasApos = new ArrayList<>();
		referencias = new ArrayList<>();
		this.referencia = ref;
		this.nome = nome;
	}

	public void processar(Objeto objeto) {
		if (referencia.igual(objeto)) {
			objeto.getPesquisas().add(this);
			objeto.addReferencias(referencias);
			referencia.config(objeto);
		}
		for (Referencia ref : referencias) {
			if (ref.igual(objeto)) {
				objeto.addReferencia(ref.getPesquisa().referencia);
				ref.config(objeto);
			}
		}
	}

	public void descrever(StringBuilder builder) {
		builder.append("\t<pesquisa");
		Referencia.rotuloValor(builder, "nome", nome);
		referencia.descrever(false, builder);
		builder.append(">" + Constantes.QL);
		for (Referencia ref : referencias) {
			builder.append("\t\t");
			ref.descrever(true, builder);
			builder.append(Constantes.QL);
		}
		for (Referencia ref : referenciasApos) {
			builder.append("\t\t");
			ref.descrever(true, builder);
			builder.append(Constantes.QL);
		}
		if (referencias.isEmpty() && referenciasApos.isEmpty()) {
			builder.append(Constantes.QL);
		}
		builder.append("\t</pesquisa>");
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

	public void validoInvisibilidade(boolean b) {
		for (Referencia ref : referencias) {
			ref.setValidoInvisibilidade(b);
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

	public List<Referencia> getReferencias() {
		return referencias;
	}

	public List<Referencia> getReferenciasApos() {
		return referenciasApos;
	}

	public void add(Referencia ref) {
		if (ref != null) {
			if (!ref.isLimparApos() && !contem(ref, referencias)) {
				referencias.add(ref);
				ref.pesquisa = this;
			} else if (ref.isLimparApos() && !contem(ref, referenciasApos)) {
				referenciasApos.add(ref);
			}
		}
	}

	public void addRef(Map<String, String> map) {
		add(VinculoHandler.criar(map));
	}

	public void add(List<Referencia> referencias) {
		for (Referencia ref : referencias) {
			add(ref);
		}
	}

	public static boolean contem(Referencia ref, List<Referencia> referencias) {
		for (Referencia r : referencias) {
			if (r.igual(ref)) {
				return true;
			}
		}
		return false;
	}

	public static boolean contem2(Referencia ref, List<Referencia> referencias) {
		for (Referencia r : referencias) {
			if (r.igual2(ref)) {
				return true;
			}
		}
		return false;
	}

	public String getNomeParaMenuItem() {
		return nome + " - " + referencia.getCampo();
	}

	@Override
	public String toString() {
		return "nome=" + nome + ", ref=" + referencia;
	}
}