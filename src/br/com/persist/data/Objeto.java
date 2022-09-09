package br.com.persist.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.persist.assistencia.Constantes;

public class Objeto extends Tipo {
	private final List<Par> atributos;
	private String tempNomeAtributo;

	public Objeto() {
		atributos = new ArrayList<>();
	}

	public List<Par> getAtributos() {
		return atributos;
	}

	public void addAtributo(String nome, Tipo tipo) {
		if (getAtributo(nome) == null) {
			tipo.pai = this;
			atributos.add(new Par(nome, tipo));
		}
	}

	public Tipo getAtributo(String nome) {
		for (Par par : atributos) {
			if (par.nome.equals(nome)) {
				return par.valor;
			}
		}
		return null;
	}

	public void preAtributo() {
	}

	public void checkDoisPonto() {
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		for (int i = 0; i < atributos.size() && i < 1; i++) {
			sb.append(atributos.get(i));
		}
		for (int i = 1; i < atributos.size(); i++) {
			sb.append("," + Constantes.QL);
			sb.append(atributos.get(i));
		}
		sb.append("}");
		return sb.toString();
	}

	class Par {
		final String nome;
		final Tipo valor;

		Par(String nome, Tipo valor) {
			this.nome = Objects.requireNonNull(nome);
			this.valor = Objects.requireNonNull(valor);
		}

		@Override
		public String toString() {
			return nome + ": " + valor;
		}
	}

	public void processar(Tipo tipo) throws DataException {
		if (tempNomeAtributo != null) {
			addAtributo(tempNomeAtributo, tipo);
			tempNomeAtributo = null;
		} else if (tipo instanceof Texto) {
			tempNomeAtributo = ((Texto) tipo).getConteudo();
		} else {
			throw new DataException("Tipo invalido >>> " + tipo);
		}
	}
}