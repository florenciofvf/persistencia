package br.com.persist;

import java.util.ArrayList;
import java.util.List;

public class Metadado {
	private final List<Metadado> filhos;
	private final String descricao;
	private Metadado pai;

	public Metadado(String descricao) {
		this.descricao = descricao;
		filhos = new ArrayList<>();
	}

	public int getIndice(Metadado metadado) {
		return filhos.indexOf(metadado);
	}

	public void add(Metadado metadado) {
		filhos.add(metadado);
		metadado.pai = this;
	}

	public String getDescricao() {
		return descricao;
	}

	public int getTotal() {
		return filhos.size();
	}

	public boolean estaVazio() {
		return filhos.isEmpty();
	}

	public Metadado getMetadado(int index) {
		return filhos.get(index);
	}

	public Metadado getPai() {
		return pai;
	}

	@Override
	public String toString() {
		return descricao;
	}
}