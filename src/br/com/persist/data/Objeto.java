package br.com.persist.data;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Constantes;

public class Objeto extends Tipo {
	private final List<NomeValor> atributos;
	private String tempNomeAtributo;

	public Objeto() {
		atributos = new ArrayList<>();
	}

	public List<NomeValor> getAtributos() {
		return atributos;
	}

	public Object converter(Object object) {
		Conversor.converter(this, object);
		return object;
	}

	public void addAtributo(String nome, Tipo tipo) {
		if (getAtributo(nome) == null) {
			tipo.pai = this;
			atributos.add(new NomeValor(nome, tipo));
		}
	}

	public Tipo getAtributo(String nome) {
		for (NomeValor nomeValor : atributos) {
			if (nomeValor.nome.equals(nome)) {
				return nomeValor.valor;
			}
		}
		return null;
	}

	public void preAtributo() throws DataException {
		if (atributos.isEmpty()) {
			throw new DataException("Objeto virgula");
		}
	}

	public void checkDoisPontos() throws DataException {
		if (tempNomeAtributo == null) {
			throw new DataException("Objeto tempNomeAtributo null");
		}
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