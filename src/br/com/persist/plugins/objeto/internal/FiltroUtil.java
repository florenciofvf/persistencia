package br.com.persist.plugins.objeto.internal;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.Constantes;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.persistencia.ChaveValor;
import br.com.persist.plugins.persistencia.Registro;

public class FiltroUtil {
	private final List<Registro> lista;
	private final Objeto objeto;
	private final boolean and;

	public FiltroUtil(Objeto objeto, boolean and, List<Registro> lista) {
		this.lista = lista != null ? lista : new ArrayList<>();
		this.objeto = objeto;
		this.and = and;
	}

	public List<Registro> getLista() {
		return lista;
	}

	public String gerar() {
		if (lista.isEmpty()) {
			return Constantes.VAZIO;
		}
		Registro registro = lista.get(0);
		if (registro.getTotal() == 1) {
			return umaChave();
		} else if (registro.getTotal() > 1) {
			return multiplasChaves();
		}
		return Constantes.VAZIO;
	}

	private String umaChave() {
		Registro registro = lista.get(0);
		ChaveValor cv = registro.get(0);
		StringBuilder sb = new StringBuilder(and ? objeto.comApelido("AND", cv.getChave()) : cv.getChave());
		sb.append(" IN(" + cv.getValor());
		for (int i = 1; i < lista.size(); i++) {
			sb.append(", ");
			registro = lista.get(i);
			cv = registro.get(0);
			sb.append(cv.getValor());
		}
		sb.append(")");
		return sb.toString();
	}

	public String multiplasChaves() {
		StringBuilder sb = new StringBuilder();
		if (lista.size() > 1) {
			sb.append(and ? "AND (" : "(");
		} else {
			sb.append(and ? "AND " : "");
		}
		Registro registro = lista.get(0);
		sb.append(andChaves(registro));
		for (int i = 1; i < lista.size(); i++) {
			sb.append(" OR ");
			registro = lista.get(i);
			sb.append(andChaves(registro));
		}
		if (lista.size() > 1) {
			sb.append(")");
		}
		return sb.toString();
	}

	private String andChaves(Registro registro) {
		ChaveValor cv = registro.get(0);
		StringBuilder sb = new StringBuilder("(" + (and ? objeto.comApelido(cv.getChave()) : cv.getChave()));
		sb.append(" = " + cv.getValor());
		for (int i = 1; i < registro.getTotal(); i++) {
			cv = registro.get(i);
			sb.append(and ? objeto.comApelido(" AND", cv.getChave()) : " AND " + cv.getChave());
			sb.append(" = " + cv.getValor());
		}
		sb.append(")");
		return sb.toString();
	}
}