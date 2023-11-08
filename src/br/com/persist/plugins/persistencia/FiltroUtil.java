package br.com.persist.plugins.persistencia;

import java.util.ArrayList;
import java.util.List;

public class FiltroUtil {
	private final List<Registro> lista;

	public FiltroUtil(List<Registro> lista) {
		this.lista = lista != null ? lista : new ArrayList<>();
	}

	public List<Registro> getLista() {
		return lista;
	}

	public String gerar() {
		return null;
	}
}