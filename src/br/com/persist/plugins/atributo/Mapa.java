package br.com.persist.plugins.atributo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.persist.assistencia.Util;

public class Mapa {
	private final List<ChaveValor> lista;
	private boolean formatEspecial;
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

	public String getString(String chave) {
		Object resp = get(chave);
		return resp != null ? resp.toString() : "";
	}

	public Mapa getMapa(String chave) {
		Object resp = get(chave);
		return resp instanceof Mapa ? (Mapa) resp : null;
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

	public boolean isFormatEspecial() {
		return formatEspecial;
	}

	public void setFormatEspecial(boolean formatEspecial) {
		this.formatEspecial = formatEspecial;
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

	@Override
	public String toString() {
		return toString(0);
	}

	private String toString(int tab) {
		StringBuilder sb = new StringBuilder("{\n");
		Iterator<ChaveValor> it = lista.iterator();
		if (it.hasNext()) {
			ChaveValor cv = it.next();
			Object valor = cv.getValor();
			String toStr = (valor instanceof String) ? Util.citar2(valor.toString()) : ((Mapa) valor).toString(tab + 1);
			sb.append(tabular(tab + 1) + Util.citar2(cv.getChave()) + ": " + toStr);
		}
		while (it.hasNext()) {
			sb.append(formatEspecial ? " " : "\n");
			ChaveValor cv = it.next();
			Object valor = cv.getValor();
			String toStr = (valor instanceof String) ? Util.citar2(valor.toString()) : ((Mapa) valor).toString(tab + 1);
			sb.append((formatEspecial ? "" : tabular(tab + 1)) + Util.citar2(cv.getChave()) + ": " + toStr);
		}
		sb.append("\n" + tabular(tab) + "}");
		return sb.toString();
	}

	private String tabular(int i) {
		i *= 4;
		StringBuilder sb = new StringBuilder();
		int c = 0;
		while (c < i) {
			sb.append(" ");
			c++;
		}
		return sb.toString();
	}
}