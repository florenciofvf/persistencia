package br.com.persist.plugins.atributo;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Util;

public class Mapa {
	private final List<ChaveValor> lista;
	protected Mapa parent;

	public Mapa() {
		lista = new ArrayList<>();
	}

	public void put(String chave, Object valor) {
		if (Util.isEmpty(chave) || valor == null) {
			return;
		}
		ChaveValor cv = new ChaveValor(chave, valor);
		if (lista.contains(cv) || !valorValido(valor)) {
			return;
		}
		lista.add(cv);
		if (valor instanceof Mapa) {
			((Mapa) valor).parent = this;
		}
	}

	public Object get(String chave) {
		if (Util.isEmpty(chave)) {
			return null;
		}
		for (ChaveValor cv : lista) {
			if (cv.getChave().equals(chave)) {
				return cv.getValor();
			}
		}
		return null;
	}

	public Mapa getParent() {
		return parent;
	}

	public int getSize() {
		return lista.size();
	}

	public void clear() {
		lista.clear();
	}

	public List<String> getChaves() {
		List<String> resp = new ArrayList<>();
		for (ChaveValor cv : lista) {
			resp.add(cv.getChave());
		}
		return resp;
	}

	public List<Object> getValores() {
		List<Object> resp = new ArrayList<>();
		for (ChaveValor cv : lista) {
			resp.add(cv.getValor());
		}
		return resp;
	}

	private boolean valorValido(Object valor) {
		return (valor instanceof String) || (valor instanceof Mapa);
	}
}